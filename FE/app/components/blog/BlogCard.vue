<script setup lang="ts">

defineProps<{
  post: BlogModel
}>()

// Helper function to format date
function formatDate(dateString?: string | Date | null) {
  if (!dateString) return 'No date'

  const date = typeof dateString === 'string' ? new Date(dateString) : dateString

  if (isNaN(date.getTime())) return 'Invalid date'

  return new Intl.DateTimeFormat('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  }).format(date)
}
</script>

<template>
  <article
      class="group bg-white dark:bg-slate-800 rounded-2xl shadow-sm hover:shadow-xl transition-all duration-300 overflow-hidden flex flex-col relative"
  >
    <!-- Cover Image -->
    <div v-if="post.coverImage" class="aspect-video overflow-hidden">
      <img
          :src="post.coverImage"
          :alt="post.title || 'Blog post cover'"
          class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
      />
    </div>

    <!-- Placeholder for no image -->
    <div v-else class="aspect-video bg-gradient-to-br from-slate-200 to-slate-300 dark:from-slate-700 dark:to-slate-800 flex items-center justify-center">
      <UIcon name="i-heroicons-photo" class="w-12 h-12 text-slate-400 dark:text-slate-500" />
    </div>

    <div class="p-5 flex flex-col flex-1">
      <!-- Categories -->
      <div v-if="post.categories && post.categories.length > 0" class="flex flex-wrap gap-1">
        <UBadge
            v-for="category in post.categories"
            :key="category"
            color="primary"
            variant="soft"
            size="xs"
            class="text-xs"
        >
          {{ category }}
        </UBadge>
      </div>

      <!-- Title -->
      <h3 class="mt-2 text-lg font-bold text-slate-900 dark:text-white leading-tight">
        {{ post.title || 'Untitled Post' }}
      </h3>

      <!-- Excerpt -->
      <p v-if="post.excerpt" class="mt-2 text-sm text-slate-600 dark:text-slate-300 line-clamp-2">
        {{ post.excerpt }}
      </p>

      <!-- Tags -->
      <div v-if="post.tags && post.tags.length > 0" class="mt-3 flex flex-wrap gap-1">
        <UBadge
            v-for="tag in post.tags"
            :key="tag"
            color="gray"
            variant="outline"
            size="xs"
            class="text-xs"
        >
          {{ tag }}
        </UBadge>
      </div>

      <!-- Footer with date and stats -->
      <footer class="mt-auto pt-3 text-xs text-slate-500 dark:text-slate-400 flex items-center justify-between">
        <div class="flex items-center gap-1">
          <UIcon name="i-heroicons-calendar" class="w-3 h-3" />
          <span>{{ formatDate(post.createdAt) }}</span>
        </div>
        <div class="flex items-center gap-3">
          <div v-if="post.viewCount !== undefined" class="flex items-center gap-1">
            <UIcon name="i-heroicons-eye" class="w-3 h-3" />
            <span>{{ post.viewCount }} views</span>
          </div>
          <div v-if="post.published !== undefined" class="flex items-center gap-1">
            <UIcon
                :name="post.published ? 'i-heroicons-globe-alt' : 'i-heroicons-lock-closed'"
                class="w-3 h-3"
            />
            <span>{{ post.published ? 'Published' : 'Draft' }}</span>
          </div>
        </div>
      </footer>
    </div>

    <!-- Link to full post -->
    <NuxtLink
        v-if="post.slug"
        :to="`/blog/${post.slug}`"
        class="absolute inset-0"
        aria-label="Read more"
    />
  </article>
</template>