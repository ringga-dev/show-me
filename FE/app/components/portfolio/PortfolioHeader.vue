<!-- components/PortfolioHeader.vue -->
<template>
  <header
      class="sticky top-0 w-full z-40 transition-all duration-300"
      :class="
      isScrolled
        ? 'bg-white/80 dark:bg-slate-900/80 shadow-lg backdrop-blur-lg'
        : 'bg-white/20 dark:bg-slate-900/20 shadow-none'
    "
  >
    <nav class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-20">
        <!-- Logo + Breadcrumb -->
        <div class="flex items-center gap-x-3">
          <ULink to="/" class="flex items-center gap-x-2 group">
            <UIcon name="i-lucide-lightbulb" class="size-7 text-slate-900 dark:text-slate-100 group-hover:text-sky-500 transition-colors" />
            <span class="font-bold text-lg text-slate-900 dark:text-slate-100">ringga<span class="text-amber-600">.</span>
      <span class="text-primary">dev</span>
    </span>
          </ULink>
          <span class="text-gray-400 dark:text-gray-500">/</span>
          <span class="font-semibold text-sky-600 dark:text-sky-400">Portfolio</span>
        </div>


        <!-- Desktop Nav -->
        <ul class="hidden lg:flex items-center space-x-3">
          <li v-for="link in navLinks" :key="link.href">
            <UButton
                variant="ghost"
                :to="link.href"
                :class="
                $route.path === link.href
                  ? 'bg-sky-500 text-white dark:bg-sky-400 dark:text-slate-900'
                  : 'text-slate-700 hover:text-sky-600 dark:text-slate-300 dark:hover:text-sky-400'
              "
            >
              {{ link.name }}
            </UButton>
          </li>
        </ul>

        <!-- Theme + CTA -->
        <div class="flex items-center gap-x-3">
          <UButton
              :icon="isDark ? 'i-heroicons-sun' : 'i-heroicons-moon'"
              color="gray"
              variant="ghost"
              aria-label="Toggle theme"
              @click="toggleColorMode"
          />
          <UButton
              @click="$router.push('/#contact')"
              class="hidden lg:inline-flex bg-gradient-to-r from-sky-500 to-indigo-600 text-white rounded-full px-5 py-2"
          >
            Let's Talk
          </UButton>

          <!-- Mobile menu -->
          <UButton
              :icon="isOpen ? 'i-heroicons-x-mark' : 'i-heroicons-bars-3'"
              color="gray"
              variant="ghost"
              class="lg:hidden"
              @click="isOpen = !isOpen"
          />
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
        <div v-if="isOpen" class="lg:hidden pb-4">
          <ul class="flex flex-col space-y-2">
            <li v-for="link in navLinks" :key="link.href">
              <UButton
                  variant="ghost"
                  :to="link.href"
                  class="w-full text-left"
                  @click="isOpen = false"
              >
                {{ link.name }}
              </UButton>
            </li>
          </ul>
        </div>
      </Transition>
    </nav>
  </header>
</template>

<script setup lang="ts">
import type { UserPortfolioModel } from '~/types/models/UserPortfolioModel'

const props = defineProps<{
  user: UserPortfolioModel | null
  navLinks: { name: string; href: string }[]
}>()

const colorMode = useColorMode()
const isDark = computed(() => colorMode.value === 'dark')
const isScrolled = ref(false)
const isOpen = ref(false)

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
