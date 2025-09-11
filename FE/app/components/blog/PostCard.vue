<!-- components/PostCard.vue -->
<template>
  <article
      class="bg-white dark:bg-gray-800 rounded-xl shadow-md hover:shadow-xl transition-shadow overflow-hidden"
  >
    <img
        :src="post.image"
        :alt="post.title"
        class="w-full h-48 object-cover"
    />
    <div class="p-6">
      <span class="text-xs font-semibold text-sky-600 dark:text-sky-400">
        {{ post.category }}
      </span>
      <h3 class="mt-2 text-lg font-bold text-gray-900 dark:text-white">
        {{ post.title }}
      </h3>
      <p class="mt-2 text-sm text-gray-600 dark:text-gray-300 line-clamp-2">
        {{ post.excerpt }}
      </p>
      <div class="mt-4 flex items-center justify-between text-sm text-gray-500 dark:text-gray-400">
        <span>{{ post.date }}</span>
        <span>{{ post.views }} views</span>
      </div>
      <UButton
          :to="`/blog/${post.slug}`"
          variant="ghost"
          size="sm"
          class="mt-4 text-sky-600 hover:text-sky-700 dark:text-sky-400 dark:hover:text-sky-300"
      >
        Read more →
      </UButton>
    </div>
  </article>
</template>

<script setup>
defineProps({
  post: {
    type: Object,
    required: true
  }
})
</script>