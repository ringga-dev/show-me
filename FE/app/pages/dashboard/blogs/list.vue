<script setup lang="ts">
import type { BlogModel } from "~/types/models/BlogModel"
import { useBlogStore } from "~/stores/blog"

definePageMeta({ layout: 'dashboard' })

const blogStore = useBlogStore()

// State
const blogs = ref<BlogModel[]>([])
const loading = ref(false)
const error = ref<string | null>(null)
const deleteLoading = ref(false) // Loading state khusus untuk delete

// Modal state - sesuai dengan pedoman Nuxt UI
const confirmOpen = ref(false)               // default HARUS false
const blogToDelete = ref<BlogModel | null>(null)

// Search & pagination
const page = ref(1)
const pageSize = ref(6)
const q = ref('')

// View mode untuk preview blog
const viewMode = ref<'grid' | 'list'>('grid')

// Computed properties
const filtered = computed(() => {
  if (!blogs.value || blogs.value.length === 0) return []
  // Validasi input pencarian
  const searchTerm = q.value.trim().toLowerCase()
  if (!searchTerm) return blogs.value
  return blogs.value.filter(blog => {
    // Validasi properti blog sebelum mengaksesnya
    const title = blog.title?.toLowerCase() || ''
    const excerpt = blog.excerpt?.toLowerCase() || ''
    const categories = blog.categories || []
    const tags = blog.tags || []
    return (
        title.includes(searchTerm) ||
        excerpt.includes(searchTerm) ||
        categories.some(cat => cat.toLowerCase().includes(searchTerm)) ||
        tags.some(tag => tag.toLowerCase().includes(searchTerm))
    )
  })
})

const paginated = computed(() => {
  const start = (page.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filtered.value.slice(start, end)
})

const totalPages = computed(() => Math.ceil(filtered.value.length / pageSize.value) || 1)

// Methods
const loadBlogs = async () => {
  loading.value = true
  error.value = null
  try {
    const response: BaseResponse<BlogModel[]> | undefined = await blogStore.getBlog()
    // Validasi response
    if (!response) {
      throw new Error('No response from server')
    }
    if (response.code !== 200 || !response.data) {
      throw new Error(response.message?.join(', ') || 'Failed to load blogs')
    }
    blogs.value = response.data
  } catch (err) {
    console.error('Failed to load blogs:', err)
    error.value = err instanceof Error ? err.message : 'An unexpected error occurred'
  } finally {
    loading.value = false
  }
}

const getStatusText = (blog: BlogModel) => {
  if (!blog.isActive) return 'Inactive'
  if (!blog.published) return 'Draft'
  return 'Published'
}

const getStatusColor = (blog: BlogModel) => {
  if (!blog.isActive) return 'gray'
  if (!blog.published) return 'yellow'
  return 'green'
}

const formatDate = (dateString?: string | Date | null) => {
  if (!dateString) return '-'
  try {
    const date = new Date(dateString)
    return isNaN(date.getTime()) ? '-' : date.toLocaleDateString()
  } catch {
    return '-'
  }
}

const handlePageChange = (newPage: number) => {
  if (newPage >= 1 && newPage <= totalPages.value) {
    page.value = newPage
    // Scroll to top for better UX
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

// Delete methods - sesuai dengan pedoman Nuxt UI
const askDelete = (blog: BlogModel) => {
  blogToDelete.value = blog
  confirmOpen.value = true
}

const closeConfirm = () => {
  confirmOpen.value = false
  blogToDelete.value = null
}

const deleteBlog = async () => {
  if (!blogToDelete.value) return closeConfirm()
  deleteLoading.value = true
  try {
    // Panggil fungsi deleteBlog dari store
    const response = await blogStore.deleteBlog(blogToDelete.value.id ?? "")
    // Validasi response
    if (response && response.code === 200) {
      // Hapus dari array lokal
      blogs.value = blogs.value.filter(blog => blog.id !== blogToDelete.value?.id)
      // Show success message
      useToast().add({
        title: 'Blog deleted',
        description: `"${blogToDelete.value.title}" has been deleted successfully`,
        color: 'primary'
      })
    } else {
      throw new Error(response?.message?.join(', ') || 'Failed to delete blog')
    }
  } catch (error) {
    console.error('Failed to delete blog:', error)
    useToast().add({
      title: 'Error',
      description: error instanceof Error ? error.message : 'Failed to delete blog post',
      color: 'error'
    })
  } finally {
    deleteLoading.value = false
    closeConfirm()
  }
}

// Toggle view mode
const toggleViewMode = () => {
  viewMode.value = viewMode.value === 'grid' ? 'list' : 'grid'
}

// Watchers
watch(q, () => {
  page.value = 1
})

watch(pageSize, () => {
  page.value = 1
})

// Lifecycle
onMounted(() => {
  loadBlogs()
})
</script>

<template>
  <div class="container mx-auto px-4 py-6">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row items-start sm:items-center justify-between mb-6 gap-4">
      <h1 class="text-2xl font-bold text-slate-800 dark:text-white">Blog Posts</h1>
      <div class="flex flex-col sm:flex-row gap-3 w-full sm:w-auto">
        <UButton
            to="/dashboard/blogs/create"
            icon="i-heroicons-plus"
            color="primary"
            as="nuxt-link"
            class="w-full sm:w-auto justify-center"
        >
          Add Blog Post
        </UButton>
        <UButton
            :icon="viewMode === 'grid' ? 'i-heroicons-list-bullet' : 'i-heroicons-squares-2x2'"
            color="gray"
            variant="outline"
            size="sm"
            @click="toggleViewMode"
            class="w-full sm:w-auto justify-center"
            :title="viewMode === 'grid' ? 'Switch to list view' : 'Switch to grid view'"
        >
          {{ viewMode === 'grid' ? 'List View' : 'Grid View' }}
        </UButton>
      </div>
    </div>

    <!-- Error State -->
    <div v-if="error" class="mb-6">
      <UAlert color="error" :title="error" />
    </div>

    <!-- Search & Page Size Controls -->
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-6">
      <UInput
          v-model="q"
          placeholder="Search blog posts..."
          icon="i-heroicons-magnifying-glass"
          class="w-full sm:w-64"
      />
      <div class="flex items-center gap-2">
        <span class="text-sm text-slate-600 dark:text-slate-400">Items per page:</span>
        <USelect
            v-model="pageSize"
            :options="[3, 6, 9, 12]"
            class="w-20"
        />
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center py-12">
      <ULoading class="w-10 h-10" />
    </div>

    <!-- Blog Cards Grid -->
    <div v-else-if="viewMode === 'grid'" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <UCard v-for="blog in paginated" :key="blog.id" class="overflow-hidden">
        <!-- Cover Image -->
        <div class="h-40 overflow-hidden bg-slate-100 dark:bg-slate-800">
          <img
              :src="blog.coverImage || 'https://placehold.co/400x300'"
              :alt="blog.title || 'Blog cover image'"
              class="w-full h-full object-cover"
              loading="lazy"
          />
        </div>
        <!-- Card Content -->
        <div class="p-4">
          <!-- Title and Status -->
          <div class="flex justify-between items-start mb-2">
            <h3 class="text-lg font-semibold text-slate-800 dark:text-white line-clamp-2">
              {{ blog.title }}
            </h3>
            <UBadge
                :color="getStatusColor(blog)"
                variant="subtle"
                size="xs"
                class="flex-shrink-0 ml-2"
            >
              {{ getStatusText(blog) }}
            </UBadge>
          </div>
          <!-- Excerpt -->
          <p v-if="blog.excerpt" class="text-sm text-slate-600 dark:text-slate-400 mb-3 line-clamp-2">
            {{ blog.excerpt }}
          </p>
          <!-- Categories -->
          <div v-if="blog.categories && blog.categories.length > 0" class="flex flex-wrap gap-1 mb-3">
            <UBadge
                v-for="category in blog.categories.slice(0, 3)"
                :key="category"
                color="primary"
                variant="soft"
                size="xs"
            >
              {{ category }}
            </UBadge>
            <UBadge
                v-if="blog.categories.length > 3"
                color="gray"
                variant="soft"
                size="xs"
            >
              +{{ blog.categories.length - 3 }}
            </UBadge>
          </div>
          <!-- Stats -->
          <div class="flex justify-between text-sm text-slate-600 dark:text-slate-400 mb-4">
            <div class="flex items-center gap-4">
              <div class="flex items-center gap-1">
                <UIcon name="i-heroicons-eye" class="w-4 h-4" />
                <span>{{ blog.viewCount || 0 }}</span>
              </div>
              <div class="flex items-center gap-1">
                <UIcon name="i-heroicons-chat-bubble-left-ellipsis" class="w-4 h-4" />
                <span>0</span>
              </div>
              <div class="flex items-center gap-1">
                <UIcon name="i-heroicons-heart" class="w-4 h-4" />
                <span>0</span>
              </div>
            </div>
            <div class="text-xs">
              {{ formatDate(blog.createdAt) }}
            </div>
          </div>
          <!-- Actions -->
          <div class="flex justify-end gap-2 pt-2 border-t border-slate-100 dark:border-slate-800">
            <UButton
                :to="`/dashboard/blogs/${blog.id}`"
                icon="i-heroicons-pencil-square"
                color="gray"
                variant="ghost"
                size="sm"
                as="nuxt-link"
            />
            <UButton
                :to="`/blogs/${blog.slug}`"
                icon="i-heroicons-eye"
                color="gray"
                variant="ghost"
                size="sm"
                as="nuxt-link"
            />
            <!-- Implementasi modal dialog sesuai dengan pedoman Nuxt UI -->
            <UModal v-model:open="confirmOpen">
              <UButton
                  icon="i-heroicons-trash"
                  color="error"
                  variant="ghost"
                  size="sm"
                  @click="askDelete(blog)"
              />
              <template #content>
                <UCard>
                  <template #header>
                    <div class="flex items-center gap-2">
                      <UIcon name="i-heroicons-trash" class="text-red-500"/>
                      <h3 class="text-base font-semibold">Hapus Blog Post</h3>
                    </div>
                  </template>
                  <p class="text-sm text-slate-600 dark:text-slate-300">
                    Anda yakin ingin menghapus
                    <span class="font-medium">{{ blogToDelete?.title || 'blog post ini' }}</span>?
                    Tindakan ini tidak bisa dibatalkan.
                  </p>
                  <template #footer>
                    <div class="flex justify-end gap-2">
                      <UButton variant="ghost" @click="closeConfirm()">Batal</UButton>
                      <UButton
                          color="error"
                          icon="i-heroicons-trash"
                          :loading="deleteLoading"
                          @click="deleteBlog"
                      >
                        Hapus
                      </UButton>
                    </div>
                  </template>
                </UCard>
              </template>
            </UModal>
          </div>
        </div>
      </UCard>
    </div>

    <!-- Blog List View -->
    <div v-else class="bg-white dark:bg-slate-800 rounded-xl shadow overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-slate-200 dark:divide-slate-700">
          <thead class="bg-slate-50 dark:bg-slate-700">
          <tr>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-slate-500 dark:text-slate-300 uppercase tracking-wider">
              Title
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-slate-500 dark:text-slate-300 uppercase tracking-wider">
              Status
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-slate-500 dark:text-slate-300 uppercase tracking-wider">
              Categories
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-slate-500 dark:text-slate-300 uppercase tracking-wider">
              Date
            </th>
            <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-slate-500 dark:text-slate-300 uppercase tracking-wider">
              Actions
            </th>
          </tr>
          </thead>
          <tbody class="bg-white dark:bg-slate-800 divide-y divide-slate-200 dark:divide-slate-700">
          <tr v-for="blog in paginated" :key="blog.id" class="hover:bg-slate-50 dark:hover:bg-slate-700">
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="flex items-center">
                <div class="flex-shrink-0 h-10 w-10">
                  <img class="h-10 w-10 rounded-full object-cover" :src="blog.coverImage || 'https://placehold.co/100x100'" alt="" />
                </div>
                <div class="ml-4">
                  <div class="text-sm font-medium text-slate-900 dark:text-white">{{ blog.title }}</div>
                  <div class="text-sm text-slate-500 dark:text-slate-400">{{ blog.excerpt }}</div>
                </div>
              </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <UBadge
                  :color="getStatusColor(blog)"
                  variant="subtle"
                  size="xs"
              >
                {{ getStatusText(blog) }}
              </UBadge>
            </td>
            <td class="px-6 py-4">
              <div class="flex flex-wrap gap-1">
                <UBadge
                    v-for="category in blog.categories.slice(0, 2)"
                    :key="category"
                    color="primary"
                    variant="soft"
                    size="xs"
                >
                  {{ category }}
                </UBadge>
                <UBadge
                    v-if="blog.categories.length > 2"
                    color="gray"
                    variant="soft"
                    size="xs"
                >
                  +{{ blog.categories.length - 2 }}
                </UBadge>
              </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-500 dark:text-slate-400">
              {{ formatDate(blog.createdAt) }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
              <div class="flex justify-end gap-2">
                <UButton
                    :to="`/dashboard/blogs/${blog.id}`"
                    icon="i-heroicons-pencil-square"
                    color="gray"
                    variant="ghost"
                    size="xs"
                    as="nuxt-link"
                />
                <UButton
                    :to="`/blogs/${blog.slug}`"
                    icon="i-heroicons-eye"
                    color="gray"
                    variant="ghost"
                    size="xs"
                    as="nuxt-link"
                />
                <!-- Implementasi modal dialog sesuai dengan pedoman Nuxt UI -->
                <UModal v-model:open="confirmOpen">
                  <UButton
                      icon="i-heroicons-trash"
                      color="error"
                      variant="ghost"
                      size="xs"
                      @click="askDelete(blog)"
                  />
                  <template #content>
                    <UCard>
                      <template #header>
                        <div class="flex items-center gap-2">
                          <UIcon name="i-heroicons-trash" class="text-red-500"/>
                          <h3 class="text-base font-semibold">Hapus Blog Post</h3>
                        </div>
                      </template>
                      <p class="text-sm text-slate-600 dark:text-slate-300">
                        Anda yakin ingin menghapus
                        <span class="font-medium">{{ blogToDelete?.title || 'blog post ini' }}</span>?
                        Tindakan ini tidak bisa dibatalkan.
                      </p>
                      <template #footer>
                        <div class="flex justify-end gap-2">
                          <UButton variant="ghost" @click="closeConfirm()">Batal</UButton>
                          <UButton
                              color="error"
                              icon="i-heroicons-trash"
                              :loading="deleteLoading"
                              @click="deleteBlog"
                          >
                            Hapus
                          </UButton>
                        </div>
                      </template>
                    </UCard>
                  </template>
                </UModal>
              </div>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Empty State -->
    <div v-if="paginated.length === 0" class="col-span-full">
      <UCard class="text-center py-12">
        <UIcon name="i-heroicons-document-text" class="w-16 h-16 mx-auto text-slate-300 dark:text-slate-600 mb-4" />
        <h3 class="text-lg font-medium text-slate-800 dark:text-white mb-2">No blog posts found</h3>
        <p class="text-slate-600 dark:text-slate-400 mb-4">
          {{ q ? 'No posts match your search criteria.' : 'Get started by creating your first blog post.' }}
        </p>
        <UButton
            v-if="!q"
            to="/dashboard/blogs/create"
            icon="i-heroicons-plus"
            color="primary"
            as="nuxt-link"
        >
          Create Blog Post
        </UButton>
      </UCard>
    </div>

    <!-- Pagination -->
    <div v-if="filtered.length > pageSize" class="flex flex-col sm:flex-row justify-between items-center mt-8 gap-4">
      <div class="text-sm text-slate-600 dark:text-slate-400">
        Showing {{ (page - 1) * pageSize + 1 }} to {{ Math.min(page * pageSize, filtered.length) }} of {{ filtered.length }} results
      </div>

      <div class="flex items-center gap-2">
        <UButton
            icon="i-heroicons-chevron-left"
            color="gray"
            variant="ghost"
            :disabled="page <= 1"
            @click="handlePageChange(page - 1)"
        />

        <div class="flex gap-1">
          <UButton
              v-for="pageNum in Math.min(5, totalPages)"
              :key="pageNum"
              :color="page === pageNum ? 'primary' : 'gray'"
              variant="ghost"
              size="sm"
              @click="handlePageChange(pageNum)"
          >
            {{ pageNum }}
          </UButton>

          <span v-if="totalPages > 5" class="px-2">...</span>

          <UButton
              v-if="totalPages > 5"
              :color="page === totalPages ? 'primary' : 'gray'"
              variant="ghost"
              size="sm"
              @click="handlePageChange(totalPages)"
          >
            {{ totalPages }}
          </UButton>
        </div>

        <UButton
            icon="i-heroicons-chevron-right"
            color="gray"
            variant="ghost"
            :disabled="page >= totalPages"
            @click="handlePageChange(page + 1)"
        />
      </div>
    </div>
  </div>
</template>