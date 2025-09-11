<template>
  <footer class="relative bg-gray-100 dark:bg-gray-900 text-gray-900 dark:text-gray-100">
    <div class="absolute inset-0 pointer-events-none">
      <div
          class="absolute top-0 left-0 w-80 h-80 rounded-full blur-3xl opacity-10
               bg-gradient-to-r from-sky-400 to-indigo-500
               dark:from-sky-600 dark:to-indigo-700"
      ></div>
      <div
          class="absolute bottom-0 right-0 w-64 h-64 rounded-full blur-2xl opacity-10
               bg-gradient-to-r from-pink-400 to-purple-500
               dark:from-pink-600 dark:to-purple-700"
      ></div>
    </div>
    <div class="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
      <div class="grid md:grid-cols-3 gap-10 text-center md:text-left">
        <div>
          <h4 class="text-2xl font-bold mb-4">Ringga Septia Pribadi</h4>
          <p class="text-sm text-gray-600 dark:text-gray-400 mb-4">
            Senior Android & Flutter developer crafting clean, scalable mobile experiences. Currently open to work.
          </p>
          <div class="flex justify-center md:justify-start space-x-4">
            <UButton
                v-for="s in socials"
                :key="s.name"
                :to="s.url"
                target="_blank"
                color="gray"
                variant="ghost"
                class="hover:text-sky-600 dark:hover:text-sky-400"
            >
              <UIcon :name="s.icon" class="text-xl" />
            </UButton>
          </div>
        </div>
        <div>
          <h5 class="font-semibold mb-4">Quick Links</h5>
          <ul class="space-y-2 text-sm">
            <li v-for="link in links" :key="link.name">
              <UButton variant="ghost" :to="link.href" class="text-gray-700 dark:text-gray-300 hover:text-sky-600 dark:hover:text-sky-400">
                {{ link.name }}
              </UButton>
            </li>
          </ul>
        </div>
        <div>
          <h5 class="font-semibold mb-4">Contact</h5>
          <ul class="space-y-2 text-sm text-gray-700 dark:text-gray-300">
            <li>ringga.dev@gmail.com</li>
            <li>Batam, Kepulauan Riau</li>
          </ul>
        </div>
      </div>
      <hr class="border-gray-300 dark:border-gray-700 my-8" />
      <div class="flex flex-col md:flex-row justify-between items-center text-sm text-gray-600 dark:text-gray-400">
        <p>© {{ new Date().getFullYear() }} Ringga Septia Pribadi. All rights reserved.</p>
        <p>Built with ❤️ using Nuxt & Tailwind</p>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
const socials = [
  { name: 'GitHub', url: 'https://github.com/ringga-dev', icon: 'i-simple-icons-github' },
  { name: 'LinkedIn', url: 'https://linkedin.com/in/ringga-septia-pribadi', icon: 'i-simple-icons-linkedin' },
  { name: 'Dribbble', url: 'https://dribbble.com/alexjohnson', icon: 'i-simple-icons-dribbble' },
]
const links = [
  { name: 'Home', href: '/' },
  { name: 'Blog', href: '/blog' },
  { name: 'Contact', href: '/#contact' },
]
</script>