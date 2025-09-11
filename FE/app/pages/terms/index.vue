<!-- File: pages/terms.vue (Nuxt 3 + Tailwind + Nuxt UI) -->
<template>
  <main id="main" class="bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100">
    <PortfolioHeader />

    <!-- Hero -->
    <section class="relative overflow-hidden bg-gradient-to-b from-slate-100/60 to-transparent dark:from-slate-900/40">
      <div class="mx-auto max-w-6xl px-6 py-16 text-center">
        <h1 class="text-4xl md:text-5xl font-extrabold bg-gradient-to-r from-sky-500 to-indigo-600 bg-clip-text text-transparent">
          Terms of Service
        </h1>
        <p class="mt-3 text-sm text-slate-600 dark:text-slate-400">
          Last updated: {{ lastUpdated }}
        </p>

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
              <ul class="space-y-2">
                <li v-for="item in toc" :key="item.id">
                  <a :href="'#' + item.id" class="block rounded-md px-2 py-1 text-slate-600 hover:text-sky-600 hover:bg-slate-100 dark:text-slate-300 dark:hover:text-sky-400 dark:hover:bg-slate-800">
                    {{ item.label }}
                  </a>
                </li>
              </ul>
            </nav>
          </div>
        </aside>

        <!-- Article -->
        <article class="prose prose-slate max-w-none dark:prose-invert">
          <!-- Intro -->
          <section :id="toc[0].id" class="scroll-mt-28">
            <p>
              These Terms of Service (“Terms”) govern your access to and use of this website and any related
              content or materials made available by <strong>Ringga Septia Pribadi</strong> (“we”, “our”, or “us”).
              By accessing or using the site, you agree to be bound by these Terms.
            </p>
          </section>

          <!-- Acceptance -->
          <section :id="toc[1].id" class="scroll-mt-28">
            <h2>Acceptance of Terms</h2>
            <p>
              Your access and use of the site indicates your acceptance of these Terms and our
              <NuxtLink to="/privacy" class="text-sky-600 hover:underline dark:text-sky-400">Privacy Policy</NuxtLink>.
              If you do not agree, do not use this site.
            </p>
          </section>

          <!-- Eligibility -->
          <section :id="toc[2].id" class="scroll-mt-28">
            <h2>Eligibility</h2>
            <p>
              You must be at least 13 years old (or the legal age in your jurisdiction) to use the site.
              If you use the site on behalf of an organization, you represent that you have authority to bind that organization.
            </p>
          </section>

          <!-- Accounts (optional) -->
          <section :id="toc[3].id" class="scroll-mt-28">
            <h2>Accounts</h2>
            <p>
              This site may offer contact forms or project inquiries. If any account features are provided in the future,
              you are responsible for maintaining the confidentiality of your credentials and for all activities under your account.
            </p>
          </section>

          <!-- Acceptable Use -->
          <section :id="toc[4].id" class="scroll-mt-28">
            <h2>Acceptable Use</h2>
            <ul>
              <li>Do not misuse the site, interfere with its operation, or access it using methods other than the provided interface.</li>
              <li>Do not upload or transmit malicious code, spam, or content that infringes third-party rights or violates laws.</li>
              <li>Do not attempt to reverse engineer, scrape, or copy significant portions of the site without permission.</li>
            </ul>
          </section>

          <!-- Intellectual Property -->
          <section :id="toc[5].id" class="scroll-mt-28">
            <h2>Intellectual Property</h2>
            <p>
              Unless otherwise stated, all content on this site (including text, graphics, logos, and code) is owned by
              Ringga Septia Pribadi or its licensors and is protected by applicable intellectual property laws.
              You are granted a limited, non-exclusive, non-transferable license to view and use the site for personal or
              internal business purposes.
            </p>
          </section>

          <!-- User Content & Feedback -->
          <section :id="toc[6].id" class="scroll-mt-28">
            <h2>User Content & Feedback</h2>
            <p>
              If you provide feedback or suggestions, you grant us a non-exclusive, worldwide, royalty-free license
              to use, reproduce, and adapt such feedback for improving the site and services, without any obligation to compensate you.
            </p>
          </section>

          <!-- Third-Party Links -->
          <section :id="toc[7].id" class="scroll-mt-28">
            <h2>Third-Party Links</h2>
            <p>
              This site may contain links to third-party websites or services that are not owned or controlled by us.
              We are not responsible for their content, privacy policies, or practices.
            </p>
          </section>

          <!-- Fees & Payments (optional) -->
          <section :id="toc[8].id" class="scroll-mt-28">
            <h2>Fees & Payments</h2>
            <p>
              This site does not currently sell products or subscriptions. If paid services are introduced later,
              applicable terms (pricing, taxes, refund policy) will be provided and these Terms will be updated.
            </p>
          </section>

          <!-- Disclaimers -->
          <section :id="toc[9].id" class="scroll-mt-28">
            <h2>Disclaimers</h2>
            <p>
              The site is provided on an “as-is” and “as-available” basis without warranties of any kind,
              whether express or implied, including but not limited to implied warranties of merchantability,
              fitness for a particular purpose, and non-infringement. We do not warrant that the site will be
              uninterrupted, error-free, or secure.
            </p>
          </section>

          <!-- Limitation of Liability -->
          <section :id="toc[10].id" class="scroll-mt-28">
            <h2>Limitation of Liability</h2>
            <p>
              To the maximum extent permitted by law, we shall not be liable for any indirect, incidental, special,
              consequential, or punitive damages, or any loss of profits or revenues, whether incurred directly or indirectly,
              or any loss of data, use, goodwill, or other intangible losses resulting from your use of or inability to use the site.
            </p>
          </section>

          <!-- Indemnification -->
          <section :id="toc[11].id" class="scroll-mt-28">
            <h2>Indemnification</h2>
            <p>
              You agree to defend, indemnify, and hold harmless Ringga Septia Pribadi from and against any claims,
              liabilities, damages, losses, and expenses (including reasonable attorneys’ fees) arising out of or in any way
              connected with your use of the site or violation of these Terms.
            </p>
          </section>

          <!-- Termination -->
          <section :id="toc[12].id" class="scroll-mt-28">
            <h2>Termination</h2>
            <p>
              We may suspend or terminate your access to the site at any time for any reason, including if we reasonably
              believe you have violated these Terms. Upon termination, any provisions which by their nature should survive
              will survive (e.g., intellectual property, disclaimers, limitation of liability).
            </p>
          </section>

          <!-- Governing Law & Disputes -->
          <section :id="toc[13].id" class="scroll-mt-28">
            <h2>Governing Law & Dispute Resolution</h2>
            <p>
              These Terms are governed by the laws of the <strong>Republic of Indonesia</strong>, without regard to conflict-of-laws principles.
              Any disputes shall be subject to the exclusive jurisdiction of the courts in Indonesia, unless otherwise required by applicable law.
            </p>
          </section>

          <!-- Changes -->
          <section :id="toc[14].id" class="scroll-mt-28">
            <h2>Changes to These Terms</h2>
            <p>
              We may modify these Terms from time to time. We will update the “Last updated” date above and, where appropriate,
              provide additional notice. Continued use of the site after changes indicates your acceptance of the revised Terms.
            </p>
          </section>

          <!-- Contact -->
          <section :id="toc[15].id" class="scroll-mt-28">
            <h2>Contact</h2>
            <p>
              Questions about these Terms? Contact:<br />
              <strong>Ringga Septia Pribadi</strong><br />
              Batam, Indonesia<br />
              <a :href="mailtoHref" class="text-sky-600 hover:underline dark:text-sky-400">{{ email }}</a>
            </p>
          </section>

          <div class="mt-10 flex flex-wrap items-center gap-3">
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
const email = 'ringga.dev@gmail.com'
const mailtoHref = `mailto:${email}?subject=${encodeURIComponent('Terms of Service inquiry')}`

// Set manual date agar konsisten (ubah sesuai kebutuhan)
const lastUpdated = new Date('2025-08-15').toLocaleDateString('en-US', {
  year: 'numeric', month: 'long', day: 'numeric'
})

const toc = [
  { id: 'intro',        label: 'Introduction' },
  { id: 'accept',       label: 'Acceptance of Terms' },
  { id: 'eligibility',  label: 'Eligibility' },
  { id: 'accounts',     label: 'Accounts' },
  { id: 'use',          label: 'Acceptable Use' },
  { id: 'ip',           label: 'Intellectual Property' },
  { id: 'ugc',          label: 'User Content & Feedback' },
  { id: 'third',        label: 'Third-Party Links' },
  { id: 'fees',         label: 'Fees & Payments' },
  { id: 'disclaimers',  label: 'Disclaimers' },
  { id: 'liability',    label: 'Limitation of Liability' },
  { id: 'indemnity',    label: 'Indemnification' },
  { id: 'termination',  label: 'Termination' },
  { id: 'law',          label: 'Governing Law & Disputes' },
  { id: 'changes',      label: 'Changes to These Terms' },
  { id: 'contact',      label: 'Contact' }
]

function backToTop() { window.scrollTo({ top: 0, behavior: 'smooth' }) }
function printPage()  { window.print() }

definePageMeta({ layout: 'default' })
useSeoMeta({
  title: 'Terms of Service – Ringga Septia Pribadi',
  description: 'The terms that govern your use of this website.',
  ogTitle: 'Terms of Service – Ringga Septia Pribadi',
  ogDescription: 'The terms that govern your use of this website.',
  ogType: 'website',
  twitterCard: 'summary_large_image',
})
useHead({
  script: [
    {
      type: 'application/ld+json',
      innerHTML: JSON.stringify({
        '@context': 'https://schema.org',
        '@type': 'WebPage',
        name: 'Terms of Service',
        url: 'http://localhost:3000/terms',
        dateModified: new Date('2025-08-15').toISOString(),
        about: { '@type': 'Thing', name: 'Terms of Service' },
        publisher: { '@type': 'Person', name: 'Ringga Septia Pribadi' }
      })
    }
  ]
})
</script>

<style>
/* Prose tweaks & smooth anchors */
.prose h2 { margin-top: 2.25rem; scroll-margin-top: 7rem; }
.prose a { text-decoration: none; }
.prose a:hover { text-decoration: underline; }
</style>
