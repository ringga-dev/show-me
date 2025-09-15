<script setup lang="ts">
import { reactive, ref, watch, onMounted, computed } from 'vue'
import { parseMarkdown } from '@nuxtjs/mdc/runtime'
import MDCRenderer from '@nuxtjs/mdc/runtime/components/MDCRenderer.vue'
import { debounce } from 'perfect-debounce'
import { useBlogStore } from '~/stores/blog'
import {useHead} from "#app";
definePageMeta({ layout: 'dashboard' })
/* ------------------------------ Types ------------------------------ */
interface Category {
  id: string;
  name: string;
  slug: string
}
interface Tag {
  id: string;
  name: string;
  slug: string
}
/* ----------------------------- Mock data --------------------------- */
const categories = ref<Category[]>([
  { id: '1', name: 'Tech', slug: 'tech' },
  { id: '2', name: 'Lifestyle', slug: 'lifestyle' }
])
const tags = ref<Tag[]>([
  { id: 't1', name: 'Vue', slug: 'vue' },
  { id: 't2', name: 'Nuxt', slug: 'nuxt' },
  { id: 't3', name: 'Kotlin', slug: 'kotlin' }
])
/* --------------------------- Form state ---------------------------- */
const form = reactive({
  id: '',
  title: '',
  slug: '',
  excerpt: '',
  content: '',
  coverImage: '',
  published: false,
  isActive: true, // Mengganti featured dengan isActive sesuai model
  metaTitle: '',
  metaDescription: '',
  categories: [] as string[],
  tags: [] as string[],
  createdAt: '',
  updatedAt: ''
})
/* ------------------------- Load existing data ---------------------- */
const route = useRoute()
const blogId = route.params.id
const blogStore = useBlogStore()
const loading = ref(false)
const deleteLoading = ref(false) // Loading state khusus untuk delete
onMounted(async () => {
  loading.value = true
  try {
    // In a real app, you would fetch this from your API
    const response = await blogStore.getBlogById(blogId as string)
    if (response && response.code === 200 && response.data) {
      const blogData = response.data
      // Sesuaikan dengan model yang benar
      Object.assign(form, {
        id: blogData.id,
        title: blogData.title,
        slug: blogData.slug,
        excerpt: blogData.excerpt,
        content: blogData.content,
        coverImage: blogData.coverImage,
        published: blogData.published,
        isActive: blogData.isActive,
        metaTitle: blogData.metaTitle,
        metaDescription: blogData.metaDescription,
        categories: blogData.categories,
        tags: blogData.tags,
        createdAt: blogData.createdAt,
        updatedAt: blogData.updatedAt
      })
      previewCover.value = blogData.coverImage
    } else {
      // Fallback to mock data if API fails
      const mockBlogData = {
        id: blogId,
        title: 'Clean Architecture in Android',
        slug: 'clean-architecture-android',
        excerpt: 'Learn how to structure your Android app for scalability and testability using Clean Architecture principles.',
        content: `## Clean Architecture Principles\n\nThis is an existing blog content...`,
        coverImage: 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97',
        published: true,
        isActive: true,
        metaTitle: 'Clean Architecture Guide for Android Developers',
        metaDescription: 'Complete guide to implementing Clean Architecture in Android apps',
        categories: ['1'],
        tags: ['t1', 't3'],
        createdAt: '2023-10-15T08:30:00Z',
        updatedAt: '2023-10-20T10:15:00Z'
      }
      Object.assign(form, mockBlogData)
      previewCover.value = mockBlogData.coverImage
    }
  } catch (error) {
    console.error('Failed to load blog:', error)
    // Fallback to mock data if API fails
    const mockBlogData = {
      id: blogId,
      title: 'Clean Architecture in Android',
      slug: 'clean-architecture-android',
      excerpt: 'Learn how to structure your Android app for scalability and testability using Clean Architecture principles.',
      content: `## Clean Architecture Principles\n\nThis is an existing blog content...`,
      coverImage: 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97',
      published: true,
      isActive: true,
      metaTitle: 'Clean Architecture Guide for Android Developers',
      metaDescription: 'Complete guide to implementing Clean Architecture in Android apps',
      categories: ['1'],
      tags: ['t1', 't3'],
      createdAt: '2023-10-15T08:30:00Z',
      updatedAt: '2023-10-20T10:15:00Z'
    }
    Object.assign(form, mockBlogData)
    previewCover.value = mockBlogData.coverImage
  } finally {
    loading.value = false
  }
})
/* ------------------------- Auto-slug helper ------------------------ */
const lastTitle = ref('')
const slugify = (s: string) =>
    s.toLowerCase().trim()
        .replace(/[^\w\s-]/g, '')
        .replace(/[\s_-]+/g, '-')
        .replace(/^-+|-+$/g, '')
watch(
    () => form.title,
    (t) => {
      if (!form.slug || form.slug === slugify(lastTitle.value)) {
        form.slug = slugify(t)
        lastTitle.value = t
      }
    }
)
/* ---------------------- Cover Image URL ------------------- */
const previewCover = ref<string | null>(null)
const removeCover = () => {
  previewCover.value = null
  form.coverImage = ''
}
// Handle cover image URL
const updateCoverFromUrl = () => {
  if (form.coverImage) {
    // Validate URL format
    try {
      new URL(form.coverImage)
      previewCover.value = form.coverImage
    } catch {
      alert('Please enter a valid URL')
      form.coverImage = ''
      previewCover.value = null
    }
  } else {
    previewCover.value = null
  }
}
// Watch for changes in coverImageUrl
watch(() => form.coverImage, (newUrl) => {
  if (newUrl) {
    updateCoverFromUrl()
  } else {
    previewCover.value = null
  }
})
/* ------------------------ Live AST parser -------------------------- */
const ast = ref<Awaited<ReturnType<typeof parseMarkdown>>>()
const updateAST = debounce(async (md: string) => {
  ast.value = await parseMarkdown(md)
}, 150)
watch(() => form.content, updateAST, { immediate: true })
/* --------------------------- Submit ------------------------------- */
const onSubmit = async () => {
  if (!form.title.trim()) {
    alert('Title is required')
    return
  }
  loading.value = true
  try {
    // Siapkan data untuk dikirim sesuai model
    const blogData = {
      title: form.title,
      slug: form.slug,
      excerpt: form.excerpt,
      content: form.content,
      coverImage: form.coverImage,
      published: form.published,
      isActive: form.isActive,
      metaTitle: form.metaTitle,
      metaDescription: form.metaDescription,
      categories: form.categories,
      tags: form.tags
    }
    // Panggil fungsi updateBlog dari store
    const response = await blogStore.updateBlog(form.id, blogData)
    if (response && response.code === 200) {
      alert('Blog updated successfully!')
      // Redirect ke halaman blog setelah berhasil menyimpan
      await navigateTo('/dashboard/blogs/list')
    } else {
      alert(response?.message || 'Failed to update blog. Please try again.')
    }
  } catch (error) {
    console.error('Error updating blog:', error)
    alert('An error occurred while updating the blog.')
  } finally {
    loading.value = false
  }
}
/* --------------------------- Delete ------------------------------- */
// Implementasi modal dialog sesuai dengan pedoman Nuxt UI
const confirmOpen = ref(false)               // default HARUS false
const targetBlog = ref(form)                 // target data untuk dihapus
function askDelete() {
  confirmOpen.value = true
}
function closeConfirm() {
  confirmOpen.value = false
}
async function performDelete() {
  if (!targetBlog.value?.id) return closeConfirm()
  deleteLoading.value = true
  try {
    // Panggil fungsi deleteBlog dari store
    const response = await blogStore.deleteBlog(targetBlog.value.id)
    // Validasi response
    if (response && response.code === 200) {
      alert(`"${targetBlog.value.title}" has been deleted successfully`)
      closeConfirm()
      navigateTo('/dashboard/blogs/list')
    } else {
      throw new Error(response?.message[0] || 'Failed to delete blog')
    }
  } catch (error) {
    console.error('Failed to delete blog:', error)
    alert(error instanceof Error ? error.message : 'Failed to delete blog post')
  } finally {
    deleteLoading.value = false
  }
}
/* ---------------------- Category & Tags Management ----------------- */
const newCategory = ref('')
const newTag = ref('')
const addCategory = () => {
  const name = newCategory.value.trim()
  if (!name) return
  // Check if category already exists
  const exists = categories.value.some(cat =>
      cat.name.toLowerCase() === name.toLowerCase()
  )
  if (!exists) {
    const slug = slugify(name)
    const id = `cat_${Date.now()}`
    categories.value.push({ id, name, slug })
    form.categories.push(id)
  } else {
    // If exists, just add to form categories
    const existingCat = categories.value.find(cat =>
        cat.name.toLowerCase() === name.toLowerCase()
    )
    if (existingCat && !form.categories.includes(existingCat.id)) {
      form.categories.push(existingCat.id)
    }
  }
  newCategory.value = ''
}
const removeCategory = (categoryId: string) => {
  form.categories = form.categories.filter(id => id !== categoryId)
}
const addTag = () => {
  const name = newTag.value.trim()
  if (!name) return
  // Check if tag already exists
  const exists = tags.value.some(tag =>
      tag.name.toLowerCase() === name.toLowerCase()
  )
  if (!exists) {
    const slug = slugify(name)
    const id = `tag_${Date.now()}`
    tags.value.push({ id, name, slug })
    form.tags.push(id)
  } else {
    // If exists, just add to form tags
    const existingTag = tags.value.find(tag =>
        tag.name.toLowerCase() === name.toLowerCase()
    )
    if (existingTag && !form.tags.includes(existingTag.id)) {
      form.tags.push(existingTag.id)
    }
  }
  newTag.value = ''
}
const removeTag = (tagId: string) => {
  form.tags = form.tags.filter(id => id !== tagId)
}
const getCategoryName = (id: string) => {
  return categories.value.find(cat => cat.id === id)?.name || id
}
const getTagName = (id: string) => {
  return tags.value.find(tag => tag.id === id)?.name || id
}

// Status text untuk publish options
const publishStatus = computed(() => {
  if (!form.isActive) return 'Inactive'
  if (form.published) return 'Published'
  return 'Draft'
})

// Status color untuk publish options
const publishStatusColor = computed(() => {
  if (!form.isActive) return 'bg-gray-100 text-gray-800 dark:bg-gray-800 dark:text-gray-200'
  if (form.published) return 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200'
  return 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200'
})

// Status icon untuk publish options
const publishStatusIcon = computed(() => {
  if (!form.isActive) return 'i-heroicons-x-circle'
  if (form.published) return 'i-heroicons-check-circle'
  return 'i-heroicons-pencil'
})

// Container color untuk Published option
const publishedContainerColor = computed(() => {
  if (!form.isActive) return 'bg-gray-50 dark:bg-gray-800/50 border-gray-200 dark:border-gray-700'
  if (form.published) return 'bg-green-50 dark:bg-green-900/20 border-green-200 dark:border-green-800'
  return 'bg-yellow-50 dark:bg-yellow-900/20 border-yellow-200 dark:border-yellow-800'
})

// Container color untuk Active option
const activeContainerColor = computed(() => {
  if (form.isActive) return 'bg-green-50 dark:bg-green-900/20 border-green-200 dark:border-green-800'
  return 'bg-gray-50 dark:bg-gray-800/50 border-gray-200 dark:border-gray-700'
})

// Watch untuk memastikan konsistensi antara isActive dan published
watch(() => form.isActive, (newValue) => {
  // Jika blog di-set menjadi tidak aktif, otomatis unpublish
  if (!newValue && form.published) {
    form.published = false
  }
})

// Fungsi untuk toggle Published
const togglePublished = () => {
  if (!form.isActive) {
    alert('Please activate the blog first before publishing')
    return
  }
  form.published = !form.published
}

// Fungsi untuk toggle Active
const toggleActive = () => {
  form.isActive = !form.isActive

  // Jika blog di-set menjadi tidak aktif, otomatis unpublish
  if (!form.isActive && form.published) {
    form.published = false
    alert('Blog has been unpublished because it was set to inactive')
  }
}
useHead(() => ({
  title: `Blog Edit – Dashboard`,
}))
</script>

<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
    <div class="flex items-center justify-between mb-8">
      <h1 class="text-3xl font-bold text-slate-900 dark:text-white">Edit Blog Post</h1>
      <div class="flex gap-2">
        <UButton to="/dashboard/blogs/list" color="gray" variant="ghost" icon="i-heroicons-arrow-left">
          Back to Blogs
        </UButton>
        <!-- Implementasi modal dialog sesuai dengan pedoman Nuxt UI -->
        <UModal v-model:open="confirmOpen">
          <UButton
              color="error"
              variant="outline"
              icon="i-heroicons-trash"
              @click="askDelete"
          >
            Delete
          </UButton>
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
                <span class="font-medium">{{ targetBlog?.title || 'blog post ini' }}</span>?
                Tindakan ini tidak bisa dibatalkan.
              </p>
              <template #footer>
                <div class="flex justify-end gap-2">
                  <UButton variant="ghost" @click="closeConfirm()">Batal</UButton>
                  <UButton
                      color="error"
                      icon="i-heroicons-trash"
                      :loading="deleteLoading"
                      @click="performDelete"
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
    <form @submit.prevent="onSubmit" class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- LEFT COLUMN -->
      <div class="lg:col-span-2 space-y-6">
        <!-- Basic Info -->
        <div class="bg-white dark:bg-slate-800 rounded-xl shadow p-6 space-y-4">
          <div>
            <label for="title" class="block text-sm font-medium mb-1">Title *</label>
            <input
                id="title"
                v-model="form.title"
                required
                type="text"
                placeholder="Enter blog title"
                class="w-full border border-slate-300 dark:border-slate-600 rounded-md px-3 py-2 bg-white dark:bg-slate-700 focus:ring-2 focus:ring-indigo-500"
            >
          </div>
          <div>
            <label for="slug" class="block text-sm font-medium mb-1">Slug</label>
            <input
                id="slug"
                v-model="form.slug"
                type="text"
                placeholder="auto-generated-slug"
                class="w-full border border-slate-300 dark:border-slate-600 rounded-md px-3 py-2 bg-white dark:bg-slate-700 focus:ring-2 focus:ring-indigo-500"
            >
          </div>
          <div>
            <label for="excerpt" class="block text-sm font-medium mb-1">Excerpt</label>
            <textarea
                id="excerpt"
                v-model="form.excerpt"
                rows="3"
                placeholder="Short summary"
                class="w-full border border-slate-300 dark:border-slate-600 rounded-md px-3 py-2 bg-white dark:bg-slate-700 focus:ring-2 focus:ring-indigo-500"
            />
          </div>
          <!-- Cover -->
          <div>
            <label for="coverImage" class="block text-sm font-medium mb-1">Cover Image URL</label>
            <div class="flex gap-2 mb-3">
              <input
                  id="coverImage"
                  v-model="form.coverImage"
                  type="text"
                  placeholder="https://example.com/image.jpg"
                  class="flex-1 border border-slate-300 dark:border-slate-600 rounded-md px-3 py-2 bg-white dark:bg-slate-700 focus:ring-2 focus:ring-indigo-500 text-sm"
              />
              <UButton
                  type="button"
                  size="sm"
                  @click="updateCoverFromUrl"
                  :disabled="!form.coverImage.trim()"
              >
                Preview
              </UButton>
            </div>
            <!-- Preview -->
            <div v-if="previewCover" class="mt-2 w-60 relative">
              <img
                  :src="previewCover"
                  alt="Cover preview"
                  class="rounded-lg w-full h-auto shadow"
              >
              <button
                  @click="removeCover"
                  class="absolute -top-2 -right-2 bg-red-500 text-white rounded-full w-6 h-6 text-sm flex items-center justify-center"
              >
                ✕
              </button>
            </div>
          </div>
        </div>
        <!-- Markdown Editor -->
        <div class="bg-white dark:bg-slate-800 rounded-xl shadow p-6">
          <label class="block font-semibold mb-2">Content (Markdown)</label>
          <textarea
              v-model="form.content"
              rows="15"
              class="w-full font-mono text-sm border border-slate-300 dark:border-slate-600 rounded-md px-3 py-2 bg-white dark:bg-slate-700 resize-y"
              placeholder="Write your blog content in Markdown..."
          />
        </div>
      </div>
      <!-- RIGHT COLUMN -->
      <div class="space-y-6">
        <!-- Publish Options dengan status visual yang lebih jelas -->
        <div class="bg-white dark:bg-slate-800 rounded-xl shadow p-6 space-y-4">
          <div class="flex items-center justify-between">
            <h3 class="font-semibold">Publish Options</h3>
            <div class="flex items-center gap-2">
              <UIcon :name="publishStatusIcon" class="w-5 h-5" :class="publishStatusColor.replace('bg-', 'text-').replace('dark:bg-', 'dark:text-')" />
              <span :class="['px-2 py-1 rounded text-xs font-medium', publishStatusColor]">
                {{ publishStatus }}
              </span>
            </div>
          </div>

          <div class="space-y-4">
            <!-- Published Option -->
            <div
                class="flex items-center justify-between p-4 rounded-lg border-2 transition-all duration-200 cursor-pointer"
                :class="publishedContainerColor"
                @click="togglePublished"
            >
              <div class="flex items-center gap-3">
                <div class="flex-shrink-0">
                  <UIcon
                      :name="form.published ? 'i-heroicons-check-circle' : 'i-heroicons-pencil'"
                      class="w-6 h-6"
                      :class="form.published ? 'text-green-600 dark:text-green-400' : 'text-yellow-600 dark:text-yellow-400'"
                  />
                </div>
                <div>
                  <span class="text-sm font-medium">Published</span>
                  <p class="text-xs text-slate-500 dark:text-slate-400">Make this post publicly visible</p>
                </div>
              </div>
              <div>
                <UToggle
                    :model-value="form.published"
                    :disabled="!form.isActive"
                    size="md"
                />
              </div>
            </div>

            <!-- Active Option -->
            <div
                class="flex items-center justify-between p-4 rounded-lg border-2 transition-all duration-200 cursor-pointer"
                :class="activeContainerColor"
                @click="toggleActive"
            >
              <div class="flex items-center gap-3">
                <div class="flex-shrink-0">
                  <UIcon
                      :name="form.isActive ? 'i-heroicons-check-circle' : 'i-heroicons-x-circle'"
                      class="w-6 h-6"
                      :class="form.isActive ? 'text-green-600 dark:text-green-400' : 'text-gray-600 dark:text-gray-400'"
                  />
                </div>
                <div>
                  <span class="text-sm font-medium">Active</span>
                  <p class="text-xs text-slate-500 dark:text-slate-400">Blog is active and can be managed</p>
                </div>
              </div>
              <div>
                <UToggle
                    :model-value="form.isActive"
                    size="md"
                />
              </div>
            </div>
          </div>

          <!-- Status Information -->
          <div class="pt-4 border-t border-slate-200 dark:border-slate-700">
            <div class="rounded-lg p-3 text-sm" :class="publishStatusColor">
              <div class="flex items-start gap-2">
                <UIcon :name="publishStatusIcon" class="w-5 h-5 mt-0.5 flex-shrink-0" />
                <div>
                  <p class="font-medium">
                    <span v-if="!form.isActive">Blog is Inactive</span>
                    <span v-else-if="form.published">Blog is Published</span>
                    <span v-else>Blog is Draft</span>
                  </p>
                  <p class="text-xs mt-1">
                    <span v-if="!form.isActive">Blog will not be displayed on the site. Activate it to make it visible.</span>
                    <span v-else-if="form.published">Blog is publicly visible to all visitors.</span>
                    <span v-else>Blog is saved but not yet published. Only you can see it.</span>
                  </p>
                </div>
              </div>
            </div>

            <div class="mt-3 text-xs text-slate-500 dark:text-slate-400">
              <p v-if="form.createdAt">Created: {{ new Date(form.createdAt).toLocaleDateString() }}</p>
              <p v-if="form.updatedAt">Last updated: {{ new Date(form.updatedAt).toLocaleDateString() }}</p>
            </div>
          </div>
        </div>

        <!-- Categories -->
        <div class="bg-white dark:bg-slate-800 rounded-xl shadow p-6">
          <h3 class="font-semibold mb-3">Categories</h3>
          <!-- Selected Categories -->
          <div v-if="form.categories.length > 0" class="flex flex-wrap gap-1 mb-3">
            <UBadge
                v-for="categoryId in form.categories"
                :key="categoryId"
                color="primary"
                variant="soft"
                size="xs"
                class="flex items-center gap-1"
            >
              {{ getCategoryName(categoryId) }}
              <button @click="removeCategory(categoryId)" class="ml-1 text-blue-800 hover:text-blue-900">
                <UIcon name="i-heroicons-x-mark" class="w-3 h-3" />
              </button>
            </UBadge>
          </div>
          <!-- Add New Category -->
          <div class="flex gap-2">
            <input
                v-model="newCategory"
                type="text"
                placeholder="Add new category"
                class="flex-1 border border-slate-300 dark:border-slate-600 rounded-md px-3 py-2 bg-white dark:bg-slate-700 focus:ring-2 focus:ring-indigo-500 text-sm"
                @keyup.enter="addCategory"
            />
            <UButton
                type="button"
                size="sm"
                icon="i-heroicons-plus"
                @click="addCategory"
                :disabled="!newCategory.trim()"
            />
          </div>
          <!-- Existing Categories -->
          <div class="mt-3 pt-3 border-t border-slate-200 dark:border-slate-700">
            <p class="text-xs text-slate-500 dark:text-slate-400 mb-2">Existing categories:</p>
            <div class="flex flex-wrap gap-1">
              <UBadge
                  v-for="category in categories"
                  :key="category.id"
                  :color="form.categories.includes(category.id) ? 'primary' : 'gray'"
                  variant="soft"
                  size="xs"
                  class="cursor-pointer"
                  @click="form.categories.includes(category.id) ? removeCategory(category.id) : form.categories.push(category.id)"
              >
                {{ category.name }}
              </UBadge>
            </div>
          </div>
        </div>

        <!-- Tags -->
        <div class="bg-white dark:bg-slate-800 rounded-xl shadow p-6">
          <h3 class="font-semibold mb-3">Tags</h3>
          <!-- Selected Tags -->
          <div v-if="form.tags.length > 0" class="flex flex-wrap gap-1 mb-3">
            <UBadge
                v-for="tagId in form.tags"
                :key="tagId"
                color="primary"
                variant="soft"
                size="xs"
                class="flex items-center gap-1"
            >
              {{ getTagName(tagId) }}
              <button @click="removeTag(tagId)" class="ml-1 text-green-800 hover:text-green-900">
                <UIcon name="i-heroicons-x-mark" class="w-3 h-3" />
              </button>
            </UBadge>
          </div>
          <!-- Add New Tag -->
          <div class="flex gap-2">
            <input
                v-model="newTag"
                type="text"
                placeholder="Add new tag"
                class="flex-1 border border-slate-300 dark:border-slate-600 rounded-md px-3 py-2 bg-white dark:bg-slate-700 focus:ring-2 focus:ring-indigo-500 text-sm"
                @keyup.enter="addTag"
            />
            <UButton
                type="button"
                size="sm"
                icon="i-heroicons-plus"
                @click="addTag"
                :disabled="!newTag.trim()"
            />
          </div>
          <!-- Existing Tags -->
          <div class="mt-3 pt-3 border-t border-slate-200 dark:border-slate-700">
            <p class="text-xs text-slate-500 dark:text-slate-400 mb-2">Existing tags:</p>
            <div class="flex flex-wrap gap-1">
              <UBadge
                  v-for="tag in tags"
                  :key="tag.id"
                  :color="form.tags.includes(tag.id) ? 'primary' : 'gray'"
                  variant="soft"
                  size="xs"
                  class="cursor-pointer"
                  @click="form.tags.includes(tag.id) ? removeTag(tag.id) : form.tags.push(tag.id)"
              >
                {{ tag.name }}
              </UBadge>
            </div>
          </div>
        </div>

        <!-- SEO -->
        <div class="bg-white dark:bg-slate-800 rounded-xl shadow p-6 space-y-3">
          <h3 class="font-semibold">SEO Meta</h3>
          <input
              v-model="form.metaTitle"
              placeholder="Meta title"
              class="w-full border border-slate-300 dark:border-slate-600 rounded-md px-3 py-2 bg-white dark:bg-slate-700 focus:ring-2 focus:ring-indigo-500"
          >
          <textarea
              v-model="form.metaDescription"
              rows="3"
              placeholder="Meta description"
              class="w-full border border-slate-300 dark:border-slate-600 rounded-md px-3 py-2 bg-white dark:bg-slate-700 focus:ring-2 focus:ring-indigo-500"
          />
        </div>

        <!-- Actions -->
        <div class="flex gap-3">
          <button
              type="submit"
              :disabled="!form.title.trim || loading"
              class="flex-1 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold rounded-md py-2 disabled:opacity-60 transition-colors flex items-center justify-center"
          >
            <span v-if="loading" class="mr-2">
              <svg class="animate-spin h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
            </span>
            {{ loading ? 'Updating...' : 'Update Blog' }}
          </button>
          <NuxtLink
              to="/dashboard/blogs/list"
              class="flex-1 text-center bg-slate-200 hover:bg-slate-300 dark:bg-slate-700 dark:hover:bg-slate-600 rounded-md py-2 transition-colors"
          >
            Cancel
          </NuxtLink>
        </div>
      </div>
    </form>

    <!-- LIVE PREVIEW -->
    <div class="mt-10 bg-white dark:bg-slate-800 rounded-xl shadow p-6">
      <h2 class="text-xl font-semibold mb-4">Live Preview</h2>
      <article class="prose dark:prose-invert max-w-none">
        <h1>{{ form.title || 'Untitled Blog' }}</h1>
        <img
            v-if="previewCover || form.coverImage"
            :src="previewCover || form.coverImage"
            alt="Cover"
            class="w-full h-auto rounded-lg shadow"
        />
        <p v-if="form.excerpt" class="italic opacity-80">{{ form.excerpt }}</p>
        <MDCRenderer
            v-if="ast"
            :body="ast.body"
            :data="ast.data"
        />
      </article>
    </div>
  </div>
</template>

<style scoped>
.input {
  @reference border border-slate-300 dark:border-slate-600 rounded-md px-3 py-2 bg-white dark:bg-slate-700 w-full;
}
</style>