<template>
  <header
      class="sticky top-0 w-full z-40 transition-all duration-300"
      :class="
      isScrolled
        ? 'bg-white/80 dark:bg-slate-900/80 shadow-lg backdrop-blur-lg'
        : 'bg-transparent'
    "
  >
    <nav class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-20">
        <!-- Logo + Breadcrumb -->
        <div class="flex items-center gap-x-3">
          <ULink to="/" class="flex items-center gap-x-2">
            <UIcon name="i-lucide-lightbulb" class="size-7 text-slate-900 dark:text-slate-100" />
            <span class="font-bold text-lg text-slate-900 dark:text-slate-100">Ringga.Dev</span>
          </ULink>
          <span class="text-gray-400 dark:text-gray-500">/</span>
          <span class="font-semibold text-sky-600 dark:text-sky-400">Blog</span>
        </div>

        <!-- Theme + CTA -->
        <div class="flex items-center gap-x-3">
          <UButton
              :icon="isDark ? 'i-heroicons-sun' : 'i-heroicons-moon'"
              color="gray"
              variant="ghost"
              @click="toggleColorMode"
          />
          <UButton
              @click="$router.push('/#contact')"
              class="hidden lg:inline-flex bg-gradient-to-r from-sky-500 to-indigo-600 text-white rounded-full px-5 py-2"
          >
            Let's Talk
          </UButton>

        </div>
      </div>

      <!-- Mobile Nav -->
      <Transition
          enter-active-class="transition duration-300 ease-out"
          enter-from-class="opacity-0 -translate-y-2"
          enter-to-class="opacity-100 translate-y-0"
          leave-active-class="transition duration-200 ease-in"
          leave-from-class="opacity-100 translate-y-0"
          leave-to-class="opacity-0 -translate-y-2"
      >

      </Transition>
    </nav>
  </header>
</template>

<script setup lang="ts">
const colorMode = useColorMode()
const isDark = computed(() => colorMode.value === 'dark')
const isScrolled = ref(false)


const toggleColorMode = () => {
  colorMode.preference = isDark.value ? 'light' : 'dark'
}
let removeScroll: (() => void) | undefined
onMounted(() => {
  const onScroll = () => (isScrolled.value = window.scrollY > 40)
  window.addEventListener('scroll', onScroll)
  removeScroll = () => window.removeEventListener('scroll', onScroll)
})
onUnmounted(() => removeScroll?.())
</script>