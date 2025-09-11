<script setup lang="ts">
import {useBlogStore} from "~/stores/blog";
import type {BlogModel} from "~/types/models/BlogModel";
const blogStore = useBlogStore()

/* ---------- State Management ---------- */
const blogs = ref<BlogModel[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

/* ---------- API Function ---------- */
async function getBlogPublic() {
  loading.value = true;
  error.value = null;
  try {
    const response = await  blogStore.getBlogPublic()
    if (response && response.code === 200 && response.data) {
      blogs.value = response.data;
      return response.data;
    } else {
      throw new Error(response?.message || 'Failed to fetch blogs');
    }
  } catch (err) {
    console.error('Error fetching blogs:', err);
    error.value = err instanceof Error ? err.message : 'Unknown error occurred';
  } finally {
    loading.value = false;
  }
  return null;
}

/* ---------- SEO ---------- */
useSeoMeta({
  title: 'Blog — Ringga.Dev',
  description: 'Articles on Android, Flutter, and mobile development by Ringga Septia Pribadi.',
  ogTitle: 'Blog — Ringga.Dev',
  ogDescription: 'Articles on Android, Flutter, and mobile development by Ringga Septia Pribadi.',
  ogImage: 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97',
  twitterCard: 'summary_large_image',
})

/* ---------- UI Config ---------- */
const tabs = ['Latest', 'Popular', 'Most Liked']
const categories = ['All', 'Android', 'Compose', 'Flutter', 'DevOps']

/* ---------- Reactive State ---------- */
const tab = ref('Latest')
const category = ref('All')
const q = ref('')
const pageSize = 6
const page = ref(1) // Ini akan diubah oleh UPagination

/* ---------- Filtering Logic ---------- */
const filteredPosts = computed(() => {
  let list = [...blogs.value]

  // Filter by category
  if (category.value !== 'All') {
    list = list.filter(p => {
      // Check if categories array exists and contains the selected category
      return p.categories && p.categories.some(cat =>
          cat.toLowerCase() === category.value.toLowerCase()
      )
    })
  }

  // Filter by search query
  if (q.value) {
    const needle = q.value.toLowerCase()
    list = list.filter(p =>
        (p.title && p.title.toLowerCase().includes(needle)) ||
        (p.excerpt && p.excerpt.toLowerCase().includes(needle))
    )
  }

  // Sort by tab
  switch (tab.value) {
    case 'Popular':
      list.sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0))
      break
    case 'Most Liked':
      // Since likes isn't in the model, we'll use viewCount as a fallback
      list.sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0))
      break
    default:
      list.sort((a, b) => {
        const dateA = a.createdAt ? new Date(a.createdAt).getTime() : 0
        const dateB = b.createdAt ? new Date(b.createdAt).getTime() : 0
        return dateB - dateA
      })
  }
  return list
})

/* ---------- Pagination ---------- */
const paginated = computed(() => {
  const start = (page.value - 1) * pageSize
  return filteredPosts.value.slice(start, start + pageSize)
})

/* ---------- Lifecycle Hooks ---------- */
// Fetch blogs when component is mounted
onMounted(async () => {
  await getBlogPublic()
})

/* Reset ke halaman 1 saat filter berubah */
watch([q, tab, category], () => {
  page.value = 1
  console.log('Filters changed - reset to page 1')
})
</script>

<template>
  <div class="min-h-screen bg-slate-50 dark:bg-slate-900">
    <!-- Header -->
    <BlogHeader />

    <!-- Hero -->
    <section class="py-20 bg-gradient-to-br from-slate-50 to-indigo-50 dark:from-slate-900 dark:to-indigo-950">
      <div class="max-w-7xl mx-auto px-4 text-center">
        <h1 class="text-4xl md:text-5xl font-extrabold bg-gradient-to-r from-indigo-600 to-purple-600 bg-clip-text text-transparent">
          Stories & Insights
        </h1>
        <p class="mt-4 max-w-xl mx-auto text-slate-600 dark:text-slate-300">
          Tutorials, case studies, and behind-the-scenes notes on Android, Flutter, and everything mobile.
        </p>
      </div>
    </section>

    <!-- Sticky Filters -->
    <section class="sticky top-20 z-10 bg-white/70 dark:bg-slate-900/70 backdrop-blur-md shadow-sm">
      <div class="max-w-7xl mx-auto px-4 py-4 flex items-center justify-between gap-4 flex-wrap">
        <!-- Tabs -->
        <div class="flex rounded-full border border-slate-300 dark:border-slate-700">
          <button
              v-for="t in tabs"
              :key="t"
              @click="tab = t"
              :class="tab === t
              ? 'bg-indigo-600 text-white'
              : 'text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-700'"
              class="px-4 py-1.5 text-sm font-medium transition rounded-full"
          >
            {{ t }}
          </button>
        </div>

        <!-- Search -->
        <UInput
            v-model="q"
            icon="i-heroicons-magnifying-glass"
            placeholder="Search..."
            class="w-full sm:w-64"
        />
      </div>

      <!-- Category pills -->
      <div class="max-w-7xl mx-auto px-4 pb-3">
        <div class="flex gap-2 overflow-x-auto no-scrollbar">
          <button
              v-for="c in categories"
              :key="c"
              @click="category = c"
              :class="category === c
              ? 'bg-indigo-600 text-white border-transparent'
              : 'border-slate-300 dark:border-slate-600 text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-700'"
              class="shrink-0 px-3 py-1.5 text-xs font-medium rounded-full border transition"
          >
            {{ c }}
          </button>
        </div>
      </div>
    </section>

    <!-- Grid & Pagination -->
    <main class="max-w-7xl mx-auto px-4 py-10">
      <!-- Loading State -->
      <div v-if="loading" class="flex justify-center items-center py-20">
        <UIcon name="i-heroicons-arrow-path" class="animate-spin h-12 w-12 text-indigo-600" />
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="text-center py-20">
        <UIcon name="i-heroicons-exclamation-triangle" class="mx-auto h-12 w-12 text-red-500" />
        <h3 class="mt-4 text-lg font-semibold">Error loading blogs</h3>
        <p class="text-sm text-slate-600 dark:text-slate-400">{{ error }}</p>
        <UButton
            @click="getBlogPublic"
            class="mt-4"
            icon="i-heroicons-arrow-path"
        >
          Try Again
        </UButton>
      </div>

      <!-- Blog Grid -->
      <div v-else-if="paginated.length > 0" class="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
        <BlogCard v-for="post in paginated" :key="post.slug" :post="post" />
      </div>

      <!-- Empty State -->
      <div v-else class="text-center py-20">
        <UIcon name="i-heroicons-magnifying-glass" class="mx-auto h-12 w-12 text-slate-400" />
        <h3 class="mt-4 text-lg font-semibold">No posts found</h3>
        <p class="text-sm text-slate-600 dark:text-slate-400">Try another query or category.</p>
      </div>

      <!-- Pagination -->
      <UPagination
          v-if="!loading && !error && filteredPosts.length > pageSize"
          v-model:page="page"
          :items-per-page="pageSize"
          :total="filteredPosts.length"
          class="mt-10 flex justify-center"
      />
    </main>

    <!-- Footer -->
    <BlogFooter />
  </div>
</template>

<style scoped>
.no-scrollbar::-webkit-scrollbar {
  display: none;
}
.no-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>