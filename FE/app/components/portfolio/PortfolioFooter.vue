<!-- components/PortfolioFooter.vue -->
<template>
  <footer role="contentinfo" class="relative overflow-hidden bg-slate-100 dark:bg-slate-900 text-slate-900 dark:text-slate-100">
    <div class="h-1 w-full bg-gradient-to-r from-sky-500 via-indigo-600 to-fuchsia-500" aria-hidden="true"></div>

    <div class="pointer-events-none absolute inset-0" aria-hidden="true">
      <div class="absolute left-[-6rem] top-[-6rem] h-80 w-80 rounded-full bg-gradient-to-r from-sky-400 to-indigo-500 opacity-10 blur-3xl dark:from-sky-600 dark:to-indigo-700" />
      <div class="absolute bottom-[-4rem] right-[-4rem] h-64 w-64 rounded-full bg-gradient-to-r from-pink-400 to-purple-500 opacity-10 blur-2xl dark:from-pink-600 dark:to-purple-700" />
    </div>

    <div class="relative mx-auto max-w-7xl px-6 py-16">
      <div class="grid grid-cols-1 gap-12 md:grid-cols-4">
        <!-- Brand -->
        <section aria-labelledby="footer-brand">
          <h4 id="footer-brand" class="mb-3 text-2xl font-bold">
            {{ user?.name || '' }}
          </h4>
          <p class="mb-5 max-w-prose text-sm text-slate-600 dark:text-slate-300">
            {{ user?.description || '' }}
          </p>

          <!-- Socials -->
          <div class="flex flex-wrap gap-2">
            <UButton
                v-for="s in (socials || [])"
                :key="s.name || s.url"
                :to="s.url || '#'"
                target="_blank"
                rel="noopener"
                color="gray"
                variant="ghost"
                :aria-label="s.name || 'social'"
                class="hover:text-sky-600 hover:scale-110 transition-transform dark:hover:text-sky-400"
            >
              <UIcon :name="s.icon || 'i-heroicons-globe-alt'" class="h-5 w-5" />
            </UButton>
          </div>
        </section>

        <!-- Quick Links -->
        <nav aria-labelledby="footer-quicklinks" class="md:col-span-1">
          <h5 id="footer-quicklinks" class="mb-4 font-semibold">Quick Links</h5>
          <ul class="space-y-2 text-sm">
            <li v-for="link in links" :key="link.name">
              <button
                  type="button"
                  @click="scrollTo(link.href)"
                  class="inline-flex items-center gap-2 text-slate-700 transition-colors hover:text-sky-600 dark:text-slate-300 dark:hover:text-sky-400"
              >
                <UIcon name="i-heroicons-arrow-small-right" class="h-4 w-4 opacity-70" />
                <span>{{ link.name }}</span>
              </button>
            </li>
          </ul>
        </nav>

        <!-- Contact -->
        <section aria-labelledby="footer-contact">
          <h5 id="footer-contact" class="mb-4 font-semibold">Contact</h5>
          <ul class="space-y-2 text-sm text-slate-700 dark:text-slate-300">
            <li class="flex items-center gap-2">
              <UIcon name="i-heroicons-envelope" class="h-4 w-4 opacity-70" />
              <a :href="mailtoHref" class="hover:underline">{{ emailText }}</a>
              <UButton size="xs" variant="ghost" @click="copyEmail" :disabled="copied">
                <UIcon :name="copied ? 'i-heroicons-check' : 'i-heroicons-clipboard-document'" class="h-4 w-4" />
              </UButton>
            </li>
            <li v-if="mapsHref" class="flex items-center gap-2">
              <UIcon name="i-heroicons-map-pin" class="h-4 w-4 opacity-70" />
              <a :href="mapsHref" target="_blank" rel="noopener" class="hover:underline">{{ addressText }}</a>
            </li>
            <li v-if="whatsAppHref" class="flex items-center gap-2">
              <UIcon name="i-heroicons-phone" class="h-4 w-4 opacity-70" />
              <a :href="whatsAppHref" target="_blank" rel="noopener" class="hover:underline">WhatsApp</a>
            </li>
          </ul>
        </section>

        <!-- Newsletter / CTA -->
        <section aria-labelledby="footer-newsletter">
          <h5 id="footer-newsletter" class="mb-4 font-semibold">Stay in the loop</h5>
          <p class="mb-3 text-sm text-slate-600 dark:text-slate-300">
            Get occasional updates about new projects and availability.
          </p>
          <form @submit.prevent="handleSubscribe" class="flex flex-col gap-3">
            <UInput
                v-model="subscriber"
                type="email"
                placeholder="you@email.com"
                autocomplete="email"
                icon="i-heroicons-envelope"
                size="md"
                :ui="{ base: 'bg-white/70 dark:bg-slate-800/50 backdrop-blur' }"
            />
            <UButton type="submit" class="bg-gradient-to-r from-sky-500 to-indigo-600 text-white hover:from-sky-600 hover:to-indigo-700">
              Subscribe
            </UButton>
            <p class="text-xs text-slate-500">No spam. Unsubscribe anytime.</p>
          </form>
        </section>
      </div>

      <div class="my-10 h-px w-full bg-gradient-to-r from-transparent via-slate-300 to-transparent dark:via-slate-700" aria-hidden="true"></div>

      <div class="flex flex-col items-center justify-between gap-3 text-sm text-slate-600 dark:text-slate-400 md:flex-row">
        <p>© {{ currentYear }} {{ user?.name || 'Ringga Septia Pribadi' }}. All rights reserved.</p>
        <div class="flex flex-wrap items-center gap-4">
          <span class="opacity-80">Built with ❤️ using Nuxt & Tailwind</span>
          <span aria-hidden="true" class="hidden h-4 w-px bg-slate-300 dark:bg-slate-700 md:inline-block"></span>
          <a href="/privacy" class="hover:text-sky-600 dark:hover:text-sky-400">Privacy</a>
          <a href="/terms" class="hover:text-sky-600 dark:hover:text-sky-400">Terms</a>
        </div>
      </div>
    </div>

    <!-- Back to top -->
    <div class="pointer-events-none fixed bottom-6 right-6 z-50">
      <UButton
          v-show="showToTop"
          icon="i-heroicons-arrow-up"
          size="lg"
          class="pointer-events-auto rounded-full shadow-lg shadow-indigo-500/20 bg-gradient-to-r from-sky-500 to-indigo-600 text-white hover:from-sky-600 hover:to-indigo-700"
          @click="scrollTop"
          aria-label="Back to top"
      />
    </div>
  </footer>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import type { UserPortfolioModel, UserPortfolioSocialModel } from '~/types/models/UserPortfolioModel'
import { useToast } from '#imports'

const props = defineProps<{
  user: UserPortfolioModel | null
  socials: UserPortfolioSocialModel[] | null
  links: { name: string; href: string }[]
}>()

/** --- Contact helpers (dinamis dari user) --- */
const emailText = computed(() => props.user?.email || 'ringga.dev@gmail.com')
const addressText = computed(() => props.user?.address || 'Batam, Kepulauan Riau')
const phoneE164 = computed(() => {
  // Bersihkan ke digit+plus sederhana (fallback)
  const raw = props.user?.phone || ''
  const cleaned = raw.replace(/[^\d+]/g, '')
  return cleaned || ''
})

const mailtoHref = computed(() => {
  const subject = encodeURIComponent('Inquiry: Mobile App Project')
  const body = encodeURIComponent('Hi Ringga,\n\nI would like to discuss a mobile app project.\n\nThanks,\n')
  return `mailto:${emailText.value}?subject=${subject}&body=${body}`
})

const whatsAppHref = computed(() => {
  if (!phoneE164.value) return ''
  const msg = encodeURIComponent('Hi, I would like to discuss a mobile app project.')
  return `https://wa.me/${phoneE164.value.replace('+', '')}?text=${msg}`
})

const mapsHref = computed(() => {
  if (!addressText.value) return ''
  const q = encodeURIComponent(addressText.value)
  return `https://www.google.com/maps/search/?api=1&query=${q}`
})

/** --- Newsletter --- */
const subscriber = ref('')
const toast = useToast()
function isEmail(v: string) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v)
}
function handleSubscribe() {
  if (!isEmail(subscriber.value)) {
    toast.add?.({ title: 'Invalid email', description: 'Please enter a valid email address.', color: 'warning' })
    return
  }
  // TODO: panggil API newsletter kamu di sini
  toast.add?.({ title: 'Subscribed', description: 'Thanks! You’ll receive updates occasionally.', color: 'success' })
  subscriber.value = ''
}

/** --- Copy email --- */
const copied = ref(false)
function copyEmail() {
  if (typeof window === 'undefined') return
  navigator.clipboard.writeText(emailText.value).then(() => {
    copied.value = true
    setTimeout(() => (copied.value = false), 1200)
  })
}

/** --- Smooth scroll --- */
function scrollTo(id: string) {
  if (typeof window === 'undefined') return
  document.getElementById(id)?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}
function scrollTop() {
  if (typeof window === 'undefined') return
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

/** --- Bottom bar helpers --- */
const currentYear = computed(() => new Date().getFullYear())

/** --- Back to top visibility --- */
const showToTop = ref(false)
function onScroll() {
  showToTop.value = window.scrollY > 320
}
onMounted(() => {
  window.addEventListener('scroll', onScroll, { passive: true })
  onScroll()
})
onBeforeUnmount(() => {
  window.removeEventListener('scroll', onScroll)
})
</script>
