<template>
  <main id="main" class="bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100">
    <PortfolioHeader />

    <!-- Reading progress -->
    <div class="fixed top-0 left-0 right-0 z-[60] h-[3px]">
      <div class="h-full bg-gradient-to-r from-sky-500 via-indigo-600 to-fuchsia-500" :style="{ width: scrollProgress + '%' }"/>
    </div>

    <!-- Hero -->
    <section class="relative overflow-hidden">
      <div class="pointer-events-none absolute inset-0 -z-10">
        <div class="absolute -left-16 top-10 h-72 w-72 rounded-full blur-3xl opacity-20 bg-gradient-to-br from-sky-400 to-indigo-500 dark:from-sky-600 dark:to-indigo-700 animate-float"/>
        <div class="absolute right-0 -bottom-10 h-72 w-72 rounded-full blur-2xl opacity-20 bg-gradient-to-br from-pink-400 to-fuchsia-500 dark:from-pink-600 dark:to-fuchsia-700 animate-float-slow"/>
      </div>

      <div class="mx-auto max-w-6xl px-6 py-16 text-center">
        <h1 class="text-4xl md:text-5xl font-extrabold bg-gradient-to-r from-sky-500 to-indigo-600 bg-clip-text text-transparent">
          Terms of Service
        </h1>
        <div class="mt-3 flex flex-wrap items-center justify-center gap-2 text-sm">
          <span class="inline-flex items-center gap-2 rounded-full bg-slate-100 px-3 py-1 text-slate-700 dark:bg-slate-800 dark:text-slate-200">
            <UIcon name="i-heroicons-calendar" class="h-4 w-4"/> Updated: {{ lastUpdated }}
          </span>
          <span class="inline-flex items-center gap-2 rounded-full bg-slate-100 px-3 py-1 text-slate-700 dark:bg-slate-800 dark:text-slate-200">
            <UIcon name="i-heroicons-book-open" class="h-4 w-4"/> ~{{ readingTime }} min read
          </span>
        </div>
        <div class="mt-6 flex flex-wrap items-center justify-center gap-3">
          <UButton icon="i-heroicons-printer" variant="outline" @click="printPage">Print</UButton>
          <UButton icon="i-heroicons-arrow-uturn-left" color="gray" variant="ghost" to="/">Back to Home</UButton>
        </div>
      </div>
    </section>

    <!-- Content -->
    <section class="py-16">
      <div class="mx-auto grid max-w-6xl grid-cols-1 gap-10 px-6 lg:grid-cols-[260px_minmax(0,1fr)]">
        <!-- TOC -->
        <aside class="hidden lg:block">
          <div class="sticky top-24 rounded-2xl border border-slate-200/60 bg-white/70 p-4 backdrop-blur dark:border-slate-700/60 dark:bg-slate-800/50">
            <h2 class="mb-3 text-sm font-semibold text-slate-700 dark:text-slate-200">Contents</h2>
            <nav class="text-sm">
              <ul class="space-y-1.5">
                <li v-for="item in toc" :key="item.id">
                  <a
                      :href="'#' + item.id"
                      class="block rounded-md px-2 py-1 transition-colors"
                      :class="activeId === item.id
                      ? 'bg-sky-100 text-sky-700 dark:bg-sky-900/40 dark:text-sky-300'
                      : 'text-slate-600 hover:text-sky-600 hover:bg-slate-100 dark:text-slate-300 dark:hover:text-sky-400 dark:hover:bg-slate-800'"
                  >
                    {{ item.label }}
                  </a>
                </li>
              </ul>
            </nav>
          </div>
        </aside>

        <!-- Article -->
        <article ref="articleRef" class="prose prose-slate max-w-none dark:prose-invert">
          <section id="intro" class="scroll-mt-28">
            <p>
              These Terms of Service (“Terms”) govern your access to and use of this website and any related content or materials made available by <strong>Ringga Septia Pribadi</strong>.
              By accessing or using the site, you agree to be bound by these Terms.
            </p>
          </section>

          <section id="accept" class="scroll-mt-28">
            <h2>Acceptance of Terms</h2>
            <p>
              Your access and use of the site indicates your acceptance of these Terms and our
              <NuxtLink to="/privacy" class="text-sky-600 hover:underline dark:text-sky-400">Privacy Policy</NuxtLink>.
              If you do not agree, do not use this site.
            </p>
          </section>

          <section id="eligibility" class="scroll-mt-28">
            <h2>Eligibility</h2>
            <p>You must be at least 13 years old (or the legal age in your jurisdiction) to use the site.</p>
          </section>

          <section id="accounts" class="scroll-mt-28">
            <h2>Accounts</h2>
            <p>If account features are provided in the future, you are responsible for safeguarding your credentials and all activity under your account.</p>
          </section>

          <section id="use" class="scroll-mt-28">
            <h2>Acceptable Use</h2>
            <ul>
              <li>Do not misuse or interfere with the site, or access using non-standard methods.</li>
              <li>No malicious code, spam, or unlawful/infringing content.</li>
              <li>No scraping or reverse engineering significant parts of the site without permission.</li>
            </ul>
          </section>

          <section id="ip" class="scroll-mt-28">
            <h2>Intellectual Property</h2>
            <p>Unless otherwise stated, all content is owned by Ringga Septia Pribadi or licensors and protected by IP laws.</p>
          </section>

          <section id="ugc" class="scroll-mt-28">
            <h2>User Content & Feedback</h2>
            <p>If you provide feedback, you grant us a worldwide, royalty-free license to use it to improve our services.</p>
          </section>

          <section id="third" class="scroll-mt-28">
            <h2>Third-Party Links</h2>
            <p>We are not responsible for third-party sites linked from here; review their policies.</p>
          </section>

          <section id="fees" class="scroll-mt-28">
            <h2>Fees & Payments</h2>
            <p>No paid products currently. If introduced, we will update these Terms accordingly.</p>
          </section>

          <section id="disclaimers" class="scroll-mt-28">
            <h2>Disclaimers</h2>
            <p>The site is provided “as is” and “as available” without warranties of any kind.</p>
          </section>

          <section id="liability" class="scroll-mt-28">
            <h2>Limitation of Liability</h2>
            <p>We are not liable for indirect or consequential damages to the maximum extent permitted by law.</p>
          </section>

          <section id="indemnity" class="scroll-mt-28">
            <h2>Indemnification</h2>
            <p>You agree to defend, indemnify, and hold us harmless from claims arising from your use of the site.</p>
          </section>

          <section id="termination" class="scroll-mt-28">
            <h2>Termination</h2>
            <p>We may suspend or terminate access at any time; provisions that should survive will do so.</p>
          </section>

          <section id="law" class="scroll-mt-28">
            <h2>Governing Law & Disputes</h2>
            <p>These Terms are governed by the laws of the Republic of Indonesia.</p>
          </section>

          <section id="changes" class="scroll-mt-28">
            <h2>Changes to These Terms</h2>
            <p>We may modify these Terms from time to time and update the “Last updated” date.</p>
          </section>

          <section id="contact" class="scroll-mt-28">
            <h2>Contact</h2>
            <p>
              <strong>Ringga Septia Pribadi</strong><br/>
              Batam, Indonesia<br/>
              <a :href="mailtoHref">{{ email }}</a>
            </p>
          </section>

          <div class="not-prose mt-10 flex flex-wrap items-center gap-3">
            <UButton icon="i-heroicons-arrow-up" variant="outline" @click="backToTop">Back to top</UButton>
            <UButton icon="i-heroicons-printer" @click="printPage">Print this page</UButton>
          </div>
        </article>
      </div>
    </section>

    <PortfolioFooter />
  </main>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'

const email = 'ringga.dev@gmail.com'
const mailtoHref = `mailto:${email}?subject=${encodeURIComponent('Terms of Service inquiry')}`
const lastUpdated = 'August 15, 2025'
const readingTime = 7

const toc = [
  { id: 'intro', label: 'Introduction' },
  { id: 'accept', label: 'Acceptance of Terms' },
  { id: 'eligibility', label: 'Eligibility' },
  { id: 'accounts', label: 'Accounts' },
  { id: 'use', label: 'Acceptable Use' },
  { id: 'ip', label: 'Intellectual Property' },
  { id: 'ugc', label: 'User Content & Feedback' },
  { id: 'third', label: 'Third-Party Links' },
  { id: 'fees', label: 'Fees & Payments' },
  { id: 'disclaimers', label: 'Disclaimers' },
  { id: 'liability', label: 'Limitation of Liability' },
  { id: 'indemnity', label: 'Indemnification' },
  { id: 'termination', label: 'Termination' },
  { id: 'law', label: 'Governing Law & Disputes' },
  { id: 'changes', label: 'Changes' },
  { id: 'contact', label: 'Contact' }
]

const activeId = ref(toc[0].id)
const articleRef = ref<HTMLElement | null>(null)
let io: IntersectionObserver | null = null
function handleIO(entries: IntersectionObserverEntry[]) {
  const visible = entries.filter(e => e.isIntersecting).sort((a, b) =>
      (a.target as HTMLElement).offsetTop - (b.target as HTMLElement).offsetTop
  )
  if (visible[0]) activeId.value = (visible[0].target as HTMLElement).id
}

/** progress */
const scrollProgress = ref(0)
function onScroll() {
  const y = window.scrollY
  const h = document.documentElement.scrollHeight - window.innerHeight
  scrollProgress.value = h > 0 ? Math.min(100, (y / h) * 100) : 0
}

function backToTop() { window.scrollTo({ top: 0, behavior: 'smooth' }) }
function printPage() { window.print() }

definePageMeta({ layout: 'default' })
useSeoMeta({
  title: 'Terms of Service – Ringga Septia Pribadi',
  description: 'The terms that govern your use of this website.',
  ogTitle: 'Terms of Service – Ringga Septia Pribadi',
  ogDescription: 'The terms that govern your use of this website.',
  ogType: 'website',
  twitterCard: 'summary_large_image'
})
useHead({
  link: [{ rel: 'canonical', href: 'http://localhost:3000/terms' }],
  script: [{
    type: 'application/ld+json',
    innerHTML: JSON.stringify({
      '@context': 'https://schema.org',
      '@type': 'WebPage',
      name: 'Terms of Service',
      url: 'http://localhost:3000/terms',
      dateModified: '2025-08-15',
      publisher: { '@type': 'Person', name: 'Ringga Septia Pribadi' }
    })
  }]
})

onMounted(() => {
  io = new IntersectionObserver(handleIO, { rootMargin: '0px 0px -70% 0px', threshold: 0.1 })
  toc.forEach(t => {
    const el = document.getElementById(t.id)
    if (el) io!.observe(el)
  })
  window.addEventListener('scroll', onScroll, { passive: true })
  onScroll()
})
onBeforeUnmount(() => {
  io?.disconnect()
  window.removeEventListener('scroll', onScroll)
})
</script>

<style>
@keyframes float { 0%,100%{transform:translateY(0)} 50%{transform:translateY(-8px)} }
.animate-float { animation: float 12s ease-in-out infinite; }
.animate-float-slow { animation: float 16s ease-in-out infinite; }
.prose h2 { scroll-margin-top: 7rem; }
</style>
