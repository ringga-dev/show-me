<template>
  <UApp>
    <NuxtRouteAnnouncer />
    <NuxtLoadingIndicator />

    <!-- 👇 wrap with NuxtLayout so layouts are actually used -->
    <NuxtLayout>
      <NuxtPage />
    </NuxtLayout>
  </UApp>
</template>

<script setup lang="ts">
import '@/assets/css/main.css'
useHead({
  meta: [
    { charset: 'utf-8' },
    { name: 'viewport', content: 'width=device-width, initial-scale=1' },
  ],
  bodyAttrs: {
    class: 'bg-white dark:bg-gray-900',
  },
  link: [
    { rel: 'icon', href: '/logo.png', sizes: '48x48' },
    {
      rel: 'icon',
      type: 'image/png',
      href: '/logo.png'
    }
  ],
});

useSeoMeta({
  title: 'Ringga.dev - My Personal Website',
  ogTitle: 'Ringga.dev - My Personal Website',
  description:
      'My Web ',
  twitterCard: 'summary_large_image',
});
</script>