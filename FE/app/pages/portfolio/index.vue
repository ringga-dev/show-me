<!-- File: pages/index.vue -->
<template>
  <main
      class="bg-slate-50 dark:bg-slate-950 font-sans antialiased selection:bg-sky-200/60 dark:selection:bg-sky-400/30">
    <!-- Scroll progress -->
    <div class="fixed top-0 left-0 right-0 h-[3px] z-[60]" aria-hidden="true">
      <div class="h-full bg-gradient-to-r from-sky-500 via-indigo-600 to-fuchsia-500"
           :style="{ width: scrollProgress + '%' }"/>
    </div>

    <!-- HEADER -->
    <PortfolioHeader :user="user" :nav-links="navLinks"/>

    <!-- HERO -->
    <section id="home" class="relative h-[100svh] flex items-center justify-center overflow-hidden">
      <div class="absolute inset-0 -z-10 overflow-hidden" aria-hidden="true">
        <div class="orb orb-1"/>
        <div class="orb orb-2"/>
        <div class="orb orb-3"/>
        <div class="absolute inset-0 bg-grid-slate opacity-[0.05] dark:opacity-10 mix-blend-overlay"/>
        <div class="absolute inset-0 pointer-events-none"
             style="background: radial-gradient(ellipse at center, rgba(255,255,255,.25), transparent 70%);"/>
      </div>

      <div class="relative z-10 text-center px-6 max-w-5xl mx-auto">
        <h1 class="text-5xl md:text-7xl font-black leading-[1.05] text-slate-900 dark:text-white drop-shadow-[0_0_16px_rgba(0,0,0,0.12)]"
            data-aos="fade-up">
          {{ user?.name ?? '' }}
        </h1>

        <p class="mt-6 text-xl md:text-2xl text-slate-700 dark:text-slate-200" data-aos="fade-up" data-aos-delay="120">
          <span class="font-semibold">{{ currentTitle || titles?.[0] || 'Senior Mobile Developer' }}</span>
          <span class="mx-2" aria-hidden="true">·</span>
          <span class="rounded-full px-2 py-0.5 text-sm bg-emerald-600/10 text-emerald-700 dark:text-emerald-300">#OPEN_TO_WORK</span>
        </p>

        <p class="mt-2 text-lg text-slate-600 dark:text-slate-300" data-aos="fade-up" data-aos-delay="220">
          <span v-text="user?.description || ''"/>
        </p>

        <div class="mt-10 flex flex-wrap gap-4 justify-center" data-aos="fade-up" data-aos-delay="320">
          <UButton size="xl" @click="scrollTo('projects')" class="btn-primary">View Projects</UButton>
          <UButton variant="outline" size="xl" @click="scrollTo('contact')" class="btn-ghost">Contact Me</UButton>
          <UButton size="xl" variant="soft" :to="resumeUrl" target="_blank" rel="noopener"
                   class="hover:scale-105 transition-transform focus-visible:ring-2 focus-visible:ring-indigo-500">
            Download CV
          </UButton>
        </div>

        <!-- Socials -->
        <div class="mt-10 flex justify-center flex-wrap gap-3" data-aos="fade-up" data-aos-delay="420">
          <UButton
              v-for="(s, index) in social ?? []"
              :key="s.url ?? s.name ?? index"
              :to="s.url || '#'"
              target="_blank"
              rel="noopener"
              color="gray"
              variant="ghost"
              :aria-label="s.name || 'social'"
              class="text-slate-700 dark:text-white/80 hover:text-sky-600 dark:hover:text-sky-400 hover:scale-110 transition-transform focus-visible:ring-2 focus-visible:ring-sky-500"
          >
            <UIcon :name="s.icon || 'i-heroicons-globe-alt'" class="w-7 h-7"/>
          </UButton>
        </div>

        <!-- Logos marquee -->
        <div class="mt-12 opacity-80" data-aos="fade-up" data-aos-delay="520">
          <div class="text-xs uppercase tracking-wide text-slate-500 mb-3">Worked with / Featured in</div>
          <div class="marquee" aria-label="Partner logos">
            <ul class="marquee__track">
              <li v-for="(l, i) in logos || []" :key="`a-${i}-${l}`" class="marquee__item">
                <img :src="l" alt="partner logo" class="h-6 md:h-7 opacity-70 rounded-full" loading="lazy"
                     decoding="async"/>
              </li>
              <li v-for="(l, i) in logos || []" :key="`b-${i}-${l}`" class="marquee__item">
                <img :src="l" alt="partner logo" class="h-6 md:h-7 opacity-70 rounded-full" loading="lazy"
                     decoding="async"/>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </section>

    <!-- ABOUT -->
    <section id="about" class="py-24 px-6">
      <div class="max-w-6xl mx-auto grid md:grid-cols-2 gap-16 items-center">
        <div data-aos="fade-right">
          <h2 class="section-title">About Me</h2>
          <p class="prose-p" v-html="user?.aboutMe || ''"/>
          <div class="grid grid-cols-2 gap-6 mt-8">
            <div v-for="s in stats || []" :key="s.label" data-aos="zoom-in">
              <div class="stat-value">
                <CountUp :value="+(s.value || 0)" :once="true"/>
                +
              </div>
              <div class="text-sm text-slate-500">{{ s.label }}</div>
            </div>
          </div>
          <ul class="mt-8 space-y-2 text-slate-600 dark:text-slate-300">
            <li class="li-check">Play Console / release management / staged rollout</li>
            <li class="li-check">CI/CD (GH Actions + Fastlane) & code review culture</li>
            <li class="li-check">Offline-first, deep links, billing, maps/geolocation</li>
          </ul>
        </div>
        <div class="flex justify-center" data-aos="fade-left">
          <img
              :src="user?.image || 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=900&q=80&auto=format&fit=crop'"
              alt="Portrait"
              class="rounded-3xl shadow-2xl shadow-indigo-500/30 ring-1 ring-black/5 dark:ring-white/10 max-w-md w-full"
              loading="lazy"
              decoding="async"
              @error="onImgError"
          />
        </div>
      </div>
    </section>

    <!-- SERVICES (dinamis) -->
    <section id="services" class="py-24 px-6 bg-muted">
      <div class="max-w-6xl mx-auto">
        <h2 class="section-title text-center mb-12">Services</h2>
        <div class="grid md:grid-cols-3 gap-6">
          <div
              v-for="(svc, i) in services"
              :key="svc.title + i"
              class="card bg-slate-100/70"
              data-aos="fade-up"
              :data-aos-delay="i * 120"
              data-tilt
          >
            <div class="flex items-center gap-3 mb-3">
              <UIcon :name="svc.icon" class="w-6 h-6 text-indigo-600 dark:text-indigo-400"/>
              <h3 class="text-lg font-semibold text-slate-900 dark:text-white">{{ svc.title }}</h3>
            </div>
            <p class="text-sm text-slate-600 dark:text-slate-300 mb-4">{{ svc.desc }}</p>
            <ul class="space-y-1 text-sm text-slate-500 dark:text-slate-400">
              <li v-for="(b, bi) in svc.bullets" :key="bi" class="flex gap-2">
                <UIcon name="i-heroicons-sparkles" class="mt-0.5 w-4 h-4 text-sky-500"/>
                {{ b }}
              </li>
            </ul>
          </div>
        </div>
      </div>
    </section>

    <!-- SKILLS -->
    <section id="skills" class="py-24 px-6">
      <div class="max-w-6xl mx-auto">
        <h2 class="section-title text-center mb-12">Skills</h2>
        <div class="grid md:grid-cols-2 lg:grid-cols-4 gap-8">
          <div
              v-for="(cat, i) in skills || []"
              :key="cat.title || i"
              class="card bg-slate-100/70"
              data-aos="fade-up"
              :data-aos-delay="i * 150"
          >
            <h3 class="text-xl font-semibold mb-4 flex items-center gap-3">
              <UIcon :name="cat.icon || 'i-heroicons-sparkles'" class="w-6 h-6 text-indigo-600 dark:text-indigo-400"/>
              {{ cat.title }}
            </h3>
            <ul class="space-y-3">
              <li
                  v-for="(s, index) in cat.skillData ?? []"
                  :key="s.name ?? index" class="text-sm">
                <div class="flex justify-between">
                  <span class="text-slate-700 dark:text-slate-200">{{ s.name }}</span>
                  <span class="font-mono text-xs text-slate-400">{{ s.percentage ?? 0 }}%</span>
                </div>
                <div class="h-2 bg-slate-200/70 dark:bg-slate-700/60 rounded-full overflow-hidden">
                  <div class="h-full bg-gradient-to-r from-sky-500 to-indigo-600"
                       :style="{ width: (s.percentage ?? 0) + '%' }"/>
                </div>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </section>

    <!-- EXPERIENCE -->
    <section id="experience" class="py-24 px-6 bg-muted relative">
      <div class="mx-auto max-w-5xl">
        <h2 class="section-title mb-4 text-center">Experience</h2>
        <p class="mb-12 text-center text-sm text-slate-600 dark:text-slate-400">Selected roles & impact — delivery,
          quality, and measurable outcomes.</p>

        <ol
            class="relative pl-8 lg:pl-0 before:content-[''] before:absolute before:top-0 before:bottom-0 before:w-px before:left-4 lg:before:left-1/2 lg:before:-translate-x-1/2 before:bg-gradient-to-b before:from-sky-200/70 before:via-slate-200/70 before:to-fuchsia-200/70 dark:before:from-sky-800/50 dark:before:via-slate-700/60 dark:before:to-fuchsia-800/50"
        >
          <li
              v-for="(e, i) in experience || []"
              :key="e.id || i"
              class="relative pb-12 lg:grid lg:grid-cols-2 lg:gap-12"
              :data-aos="i % 2 === 0 ? 'fade-right' : 'fade-left'"
              :data-aos-delay="i * 120"
          >
            <span
                class="absolute left-4 lg:left-1/2 lg:-translate-x-1/2 top-3 z-10 flex h-6 w-6 items-center justify-center rounded-full bg-gradient-to-br from-indigo-600 to-sky-500 text-white ring-4 ring-white dark:ring-slate-900 shadow-md"
                aria-hidden="true">
              <UIcon name="i-heroicons-briefcase" class="h-3.5 w-3.5"/>
            </span>

            <div
                :class="[
                'group relative mt-6 rounded-bl-2xl rounded-br-2xl border shadow-sm backdrop-blur',
                'border-slate-200/70 bg-white/70 dark:border-slate-700/60 dark:bg-slate-800/50',
                'p-5 sm:p-6 transition-all hover:shadow-xl hover:-translate-y-0.5',
                i % 2 === 0 ? 'lg:col-start-1' : 'lg:col-start-2'
              ]"
            >
              <span
                  class="pointer-events-none absolute inset-x-0 top-0 h-0.5 rounded-t-2xl bg-gradient-to-r from-sky-400 via-indigo-500 to-fuchsia-500"
                  aria-hidden="true"/>

              <div class="flex items-start justify-between gap-4">
                <div class="flex items-start gap-3">
                  <img
                      v-if="(e as any).logo"
                      :src="(e as any).logo"
                      alt="Company logo"
                      class="h-10 w-10 rounded-xl object-cover ring-1 ring-black/5 dark:ring-white/10"
                      loading="lazy"
                      decoding="async"
                      @error="onImgError"
                  />
                  <div>
                    <h3 class="font-semibold text-slate-900 dark:text-white">{{ e.title }}</h3>
                    <p class="text-xs text-slate-500">
                      {{ e.company }}
                      <template v-if="e.location"> · {{ e.location }}</template>
                    </p>
                  </div>
                </div>

                <span v-if="period(e.startDate, e.endDate)"
                      class="rounded-full bg-slate-100 px-2.5 py-1 text-xs font-medium text-slate-700 dark:bg-slate-700/60 dark:text-slate-200">{{
                    period(e.startDate, e.endDate)
                  }}</span>
              </div>

              <p v-if="e.description" class="mt-3 text-sm text-slate-700/90 dark:text-slate-300/90">{{
                  e.description
                }}</p>

              <ul v-if="(e.highlights || []).filter(Boolean).length"
                  class="mt-4 space-y-2 text-sm text-slate-700 dark:text-slate-300">
                <li v-for="b in (e.highlights || []).filter(Boolean)" :key="b" class="flex gap-2">
                  <UIcon name="i-heroicons-check-badge" class="mt-0.5 h-4 w-4 text-emerald-500"/>
                  <span>{{ b }}</span>
                </li>
              </ul>

              <div v-if="(e.technologies || []).filter(Boolean).length" class="mt-4 flex flex-wrap items-center gap-2">
                <UBadge v-for="t in (e.technologies || []).filter(Boolean)" :key="t" variant="soft" color="primary">{{
                    t
                  }}
                </UBadge>
              </div>
            </div>
          </li>
        </ol>

        <div v-if="!loadingExp && !((experience && experience.length))" class="text-center text-sm text-slate-500 mt-8">
          No experience data yet.
        </div>
      </div>
    </section>

    <!-- PROJECTS -->
    <section id="projects" class="py-24 px-6">
      <div class="max-w-6xl mx-auto">
        <h2 class="section-title text-center mb-12">Featured Projects</h2>

        <div class="flex flex-wrap items-end justify-end gap-3 mb-8" data-aos="fade-up">
          <UInput v-model="q" placeholder="Search projects..." class="w-64" icon="i-heroicons-magnifying-glass"/>
        </div>

        <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
          <div
              v-for="(p, i) in filteredProjects"
              :key="p.id || i"
              class="group relative rounded-2xl p-0.5 bg-gradient-to-br from-sky-200/40 via-slate-200/30 to-fuchsia-200/40 dark:from-sky-900/30 dark:via-slate-800/40 dark:to-fuchsia-900/30 hover:from-sky-300/50 hover:to-fuchsia-300/50 transition-all duration-300"
              data-aos="fade-up"
              :data-aos-delay="i * 80"
              data-tilt
          >
            <div
                class="card h-full rounded-[1rem] bg-slate-100/70 dark:bg-slate-900/40 border border-slate-200/60 dark:border-slate-700/60 shadow-sm backdrop-blur p-4 hover:-translate-y-0.5 hover:shadow-xl transition-all duration-300 focus-within:ring-2 focus-within:ring-indigo-500">
              <span
                  class="pointer-events-none absolute inset-x-2 top-2 h-0.5 rounded-full bg-gradient-to-r from-sky-400 via-indigo-500 to-fuchsia-500"
                  aria-hidden="true"/>

              <div class="overflow-hidden rounded-xl mb-4 ring-1 ring-black/5 dark:ring-white/10">
                <img
                    :src="p.imageUrl || placeholderSrc"
                    :alt="p.title ? `${p.title} preview` : 'project image'"
                    class="w-full h-56 object-cover transition-transform duration-500 group-hover:scale-[1.03]"
                    loading="lazy"
                    decoding="async"
                    @error="onImgError"
                />
              </div>

              <h3 class="text-lg md:text-xl font-semibold tracking-tight text-slate-900 dark:text-white mb-1 line-clamp-2 break-words">
                {{ p.title || 'Untitled Project' }}</h3>

              <p v-if="p.dateCreated" class="text-xs text-slate-500 mb-2">{{ getYear(p.dateCreated) }}</p>

              <p class="text-sm text-slate-700/90 dark:text-slate-300/90 mb-4 line-clamp-3 break-words">
                {{ p.description || '' }}</p>

              <div class="flex flex-wrap gap-2 mb-4">
                <UBadge
                    v-for="t in (p.technologies || [])"
                    :key="t"
                    color="primary"
                    variant="soft"
                    class="!px-2 !py-1 rounded-full text-[11px] font-medium whitespace-nowrap overflow-hidden text-ellipsis max-w-28 sm:max-w-32"
                >
                  {{ t }}
                </UBadge>
              </div>

              <div class="grid grid-cols-3 gap-3 text-center">
                <div
                    class="rounded-xl border border-slate-200/60 dark:border-slate-700/60 bg-white/60 dark:bg-slate-800/50 py-3">
                  <div class="text-base font-semibold text-slate-900 dark:text-slate-100">
                    {{ (p.userRequest ?? 0).toLocaleString('en-US') }}
                  </div>
                  <div class="text-[10px] uppercase tracking-wider text-slate-500">MAU</div>
                </div>
                <div
                    class="rounded-xl border border-slate-200/60 dark:border-slate-700/60 bg-white/60 dark:bg-slate-800/50 py-3">
                  <div class="text-base font-semibold text-slate-900 dark:text-slate-100">{{ p.rating ?? 0 }}</div>
                  <div class="text-[10px] uppercase tracking-wider text-slate-500">Rating</div>
                </div>
                <div
                    class="rounded-xl border border-slate-200/60 dark:border-slate-700/60 bg-white/60 dark:bg-slate-800/50 py-3">
                  <div class="text-base font-semibold text-slate-900 dark:text-slate-100">{{
                      p.releseVersion || '-'
                    }}
                  </div>
                  <div class="text-[10px] uppercase tracking-wider text-slate-500">Release</div>
                </div>
              </div>

              <div class="mt-4 flex gap-3">
                <UButton v-if="p.githubUrl" :to="p.githubUrl" target="_blank" size="xs" icon="i-simple-icons-github"
                         variant="soft">GitHub
                </UButton>
                <UButton v-if="p.demoUrl" :to="p.demoUrl" target="_blank" size="xs"
                         icon="i-heroicons-arrow-top-right-on-square" variant="soft">Demo
                </UButton>
              </div>
            </div>
          </div>
        </div>

        <div v-if="filteredProjects.length === 0" class="text-center text-sm text-slate-500 mt-8">No projects found.
        </div>
      </div>
    </section>

    <!-- TESTIMONIALS (FULL WIDTH, Animated Slider) -->
    <section id="testimonials" class="py-24 px-6 bg-muted">
      <div class="max-w-6xl mx-auto">
        <h2 class="section-title text-center mb-12">What People Say</h2>

        <div
            class="ts ts--full relative overflow-hidden pr-20 pl-20"
            ref="sliderEl"
            tabindex="0"
            aria-roledescription="carousel"
            :aria-label="`Testimonials (${testimonials.length})`"
            @keydown.left.prevent="prevSlide"
            @keydown.right.prevent="nextSlide"
        >
          <div class="ts__track" :style="{ transform: `translateX(-${slideIndex * 100}%)` }">
            <article v-for="(t,i) in testimonials" :key="t.author + i" class="ts__item">
              <figure
                  class="card w-full h-full flex flex-col justify-between
                       bg-white/70 dark:bg-slate-900/50 border border-slate-200/60 dark:border-slate-700/60
                       shadow-sm transition-all duration-500 ease-out
                       ring-0 hover:ring-[1.5px] hover:ring-slate-900/5 dark:hover:ring-white/10
                       will-change-transform"
                  :class="i === slideIndex ? 'scale-[1.01] shadow-lg' : 'opacity-90'"
              >
                <header class="flex items-center gap-3 mb-4">
                  <img v-if="t.avatar" :src="t.avatar" alt=""
                       class="h-10 w-10 rounded-full ring-2 ring-white/70 dark:ring-white/10 object-cover"
                       loading="lazy" decoding="async"/>
                  <div class="min-w-0">
                    <div class="font-semibold text-slate-900 dark:text-slate-100 truncate">{{ t.author }}</div>
                    <div class="text-xs text-slate-500 dark:text-slate-400 truncate">{{ t.role }}</div>
                  </div>
                  <div v-if="t.rating" class="ml-auto flex gap-0.5 text-amber-500" aria-label="rating">
                    <UIcon name="i-heroicons-star-solid" class="h-4 w-4" v-for="n in Math.round(+t.rating)" :key="n"/>
                  </div>
                </header>

                <blockquote class="text-slate-700 dark:text-slate-200 leading-relaxed text-lg">
                  <span class="font-black text-sky-600/80 dark:text-sky-400/80 mr-1">“</span>
                  {{ t.quote }}
                  <span class="font-black text-sky-600/80 dark:text-sky-400/80 ml-1">”</span>
                </blockquote>
              </figure>
            </article>
          </div>

          <!-- controls -->
          <div class="absolute inset-y-0 left-0 right-0 flex items-center justify-between px-1 pointer-events-none">
            <UButton
                aria-label="Previous testimonial"
                icon="i-heroicons-chevron-left"
                variant="soft" color="gray" class="pointer-events-auto"
                @click="prevSlide"
            />
            <UButton
                aria-label="Next testimonial"
                icon="i-heroicons-chevron-right"
                variant="soft" color="gray" class="pointer-events-auto"
                @click="nextSlide"
            />
          </div>

          <!-- dots -->
          <div class="mt-6 flex items-center justify-center gap-2" role="tablist">
            <button
                v-for="(_, i) in testimonials"
                :key="i"
                class="h-2.5 w-2.5 rounded-full transition-all"
                :class="i === slideIndex ? 'bg-indigo-500 scale-110' : 'bg-slate-300 dark:bg-slate-600'"
                @click="goSlide(i)"
                :aria-label="`Go to testimonial ${i + 1}`"
                role="tab"
                :aria-selected="i === slideIndex"
            />
          </div>
        </div>
      </div>
    </section>

    <!-- CONTACT -->
    <section id="contact" class="py-24 px-6">
      <div class="max-w-2xl mx-auto text-center">
        <h2 class="section-title mb-4">Get In Touch</h2>
        <p class="mb-8 text-slate-600 dark:text-slate-300">Let’s build the next amazing mobile experience together!</p>
        <div class="flex justify-center gap-3 flex-wrap" data-aos="fade-up" data-aos-delay="200">
          <UButton :to="mailtoHref" size="xl" class="btn-primary">{{ email }}</UButton>
          <UButton variant="outline" size="xl" @click="copyEmail" :disabled="copied"
                   class="focus-visible:ring-2 focus-visible:ring-indigo-500">
            <UIcon :name="copied ? 'i-heroicons-check' : 'i-heroicons-clipboard-document'" class="w-5 h-5 mr-2"/>
            {{ copied ? 'Copied' : 'Copy Email' }}
          </UButton>
          <UButton size="xl" color="success" :to="whatsAppHref" target="_blank" rel="noopener">WhatsApp</UButton>
        </div>
      </div>
    </section>

    <!-- FOOTER -->
    <PortfolioFooter :user="user" :socials="social" :links="footerLinks"/>
  </main>
</template>

<script setup lang="ts">
import {ref, computed, onMounted, onBeforeUnmount, defineComponent, h, watchEffect} from 'vue'

const store = useUserPortfolioStore()

/* ---------- Meta & SEO ---------- */
definePageMeta({
  title: 'Ringga Septia Pribadi – Senior Mobile Developer',
  description: 'Senior Android & Flutter developer crafting clean, scalable mobile experiences from Batam, Indonesia. Open to work.'
})
useSeoMeta({
  title: 'Portfolio - Ringga Septia Pribadi',
  ogTitle: 'Ringga Septia Pribadi – Senior Mobile Developer',
  description: 'Senior Android & Flutter developer crafting clean, scalable mobile experiences from Batam, Indonesia. Open to work.',
  ogImage: 'https://chat-github.nuxt.dev/home.png',
  twitterCard: 'summary_large_image',
  themeColor: '#0ea5e9'
})
useHead({
  link: [{rel: 'canonical', href: 'https://ringga-dev.my.id/'}],
  script: [{
    type: 'application/ld+json',
    innerHTML: JSON.stringify({
      '@context': 'https://schema.org',
      '@type': 'Person',
      name: 'Ringga Septia Pribadi',
      jobTitle: 'Senior Mobile Developer',
      url: 'https://ringga-dev.my.id',
      sameAs: ['https://github.com/ringga-dev', 'https://www.linkedin.com/in/ringga-septia-pribadi/'],
      address: {'@type': 'PostalAddress', addressCountry: 'ID'}
    })
  }]
})

/* ---------- Fetch dari API (SSR + CSR) ---------- */
const {data: skillsData} = await useAsyncData('skills', async () => (await store.getPortfolioSkillPublic())?.data ?? [])
const {
  data: experienceData,
  pending: pendingExperience
} = await useAsyncData('experience', async () => (await store.getPortfolioExperiencePublic())?.data ?? [])
const {data: projectsData} = await useAsyncData('projects', async () => (await store.getPortfolioProjectsPublic())?.data ?? [])
const {data: userData} = await useAsyncData('user', async () => (await store.fetchDetailsPublic())?.data ?? null)

/* ---------- Mappings ke template ---------- */
const skills = computed(() => skillsData.value ?? null)
const experience = computed(() => experienceData.value ?? null)
const projects = computed(() => projectsData.value ?? null)
const user = computed(() => userData.value ?? null)
const social = computed(() => user.value?.socialMedia ?? null)
const logos = computed(() => user.value?.workedWith ?? null)

const email = computed(() => user.value?.email || 'ringga.dev@gmail.com')
const resumeUrl = ref('/files/Ringga_Resume.pdf')

/* ---------- Titles rotator ---------- */
const titles = ref<string[] | null>(null)
const currentTitle = ref<string | null>(null)
let titleIdx = -1
let titleTimer: ReturnType<typeof setInterval> | null = null
watchEffect(() => {
  titles.value = user.value?.titles ?? []
  if (titles.value?.length && !currentTitle.value) {
    currentTitle.value = titles.value[0] ?? null
    titleIdx = 0
  }
})
const rotateTitle = () => {
  if (!titles.value?.length) return
  titleIdx = (titleIdx + 1) % titles.value.length
  currentTitle.value = titles.value[titleIdx] ?? null
}

/* ---------- Stats dinamis ---------- */
const stats = computed(() => [
  {label: 'Projects', value: projects.value?.length || 0},
  {label: 'Apps Shipped', value: 12},
  {label: 'Years', value: 12},
  {label: 'Techs', value: 12}
])

/* ---------- Services (dinamis dari skills, fallback) ---------- */
const services = computed(() => {
  const defaults = [
    {
      icon: 'i-heroicons-device-phone-mobile',
      title: 'Android (Kotlin/Compose)',
      desc: 'Native apps, offline-first, deep links',
      bullets: ['Hilt/DI', 'Room/Flow', 'Play Billing']
    },
    {
      icon: 'i-logos-flutter',
      title: 'Flutter',
      desc: 'Cross-platform performance',
      bullets: ['BLoC/Provider', 'Animations', 'Native bridges']
    },
    {
      icon: 'i-heroicons-rectangle-stack',
      title: 'CI/CD & Release',
      desc: 'Automation & quality gates',
      bullets: ['Fastlane', 'GH Actions', 'Play Console']
    }
  ]
  const sk = (skills.value || []).filter(Boolean)
  if (!sk.length) return defaults
  const picked = sk.slice(0, 3)
  return picked.map((c: any) => ({
    icon: c?.icon || 'i-heroicons-sparkles',
    title: c?.title || 'Service',
    desc: c?.description || '—',
    bullets: (c?.skillData || [])
        .filter(Boolean)
        .slice(0, 3)
        .map((x: any) => [x?.name, typeof x?.percentage === 'number' ? `${x.percentage}%` : ''].filter(Boolean).join(' '))
  }))
})

/* ---------- Experience state ---------- */
const loadingExp = computed(() => pendingExperience.value)

/* ---------- Nav / Footer links ---------- */
const navLinks = [
  {name: 'Home', href: '/'},
  {name: 'Blog', href: '/blog'},
  {name: 'Contact', href: '/#contact'}
]
const footerLinks = [
  {name: 'Home', href: 'home'},
  {name: 'About', href: 'about'},
  {name: 'Skills', href: 'skills'},
  {name: 'Projects', href: 'projects'},
  {name: 'Contact', href: 'contact'}
]

/* ---------- Filter Projects ---------- */
const q = ref('')
const activeFilter = ref<'All' | string>('All')
const filteredProjects = computed(() => {
  const list = (projects.value || []).slice()
  const byCategory = activeFilter.value === 'All' ? list : list.filter((p: any) => p.category === activeFilter.value)
  const s = q.value.trim().toLowerCase()
  if (!s) return byCategory
  return byCategory.filter((p: any) =>
      (p.title || '').toLowerCase().includes(s) ||
      (p.description || '').toLowerCase().includes(s) ||
      (p.technologies || []).some((t: any) => (t || '').toLowerCase().includes(s))
  )
})

/* ---------- Contact helpers ---------- */
const mailtoHref = computed(() => {
  const subject = encodeURIComponent('Inquiry: Mobile App Project')
  const body = encodeURIComponent('Hi Ringga,\n\nI would like to discuss a mobile app project.\n\nThanks,\n')
  return `mailto:${email.value}?subject=${subject}&body=${body}`
})
const whatsAppHref = computed(() => {
  const msg = encodeURIComponent('Hi Ringga, I would like to discuss a mobile app project.')
  const phone = user.value?.phone?.replace(/[^\d+]/g, '') || '6281234567890'
  return `https://wa.me/${phone.replace('+', '')}?text=${msg}`
})
const copied = ref(false)

function copyEmail() {
  if (typeof window === 'undefined') return
  navigator.clipboard.writeText(email.value).then(() => {
    copied.value = true
    setTimeout(() => (copied.value = false), 1500)
  })
}

/* ---------- CountUp ---------- */
const CountUp = defineComponent({
  props: {value: {type: Number, required: true}, once: {type: Boolean, default: true}},
  setup(props) {
    const el = ref<HTMLElement | null>(null)
    const display = ref(0)
    let started = false
    let io: IntersectionObserver | null = null

    const start = async () => {
      if (started && props.once) return
      started = true
      const {default: gsap} = await import('gsap')
      await gsap.to(display, {
        duration: 1.6,
        value: props.value,
        roundProps: 'value',
        ease: 'power2.out',
        onUpdate: () => {
          display.value = Math.floor((display.value as unknown as number) || 0);
        }
      })
    }

    onMounted(() => {
      if (typeof window === 'undefined') return
      io = new IntersectionObserver((entries) => entries.forEach((e) => e.isIntersecting && start()), {threshold: 0.6})
      if (el.value) io.observe(el.value)
    })
    onBeforeUnmount(() => {
      io?.disconnect();
      io = null
    })

    return () => h('span', {ref: el}, (display.value as unknown as number)?.toString())
  }
})

/* ---------- Testimonials slider ---------- */
const testimonials = [
  {
    quote: 'Delivers fast with quality, great communication, and solid architecture decisions.',
    author: 'Adi P.',
    role: 'Product Manager',
    avatar: 'https://randomuser.me/api/portraits/men/32.jpg',
    rating: 5
  },
  {
    quote: 'Mentors juniors effectively and sets high standards in code reviews.',
    author: 'Sarah K.',
    role: 'Engineering Manager',
    avatar: 'https://randomuser.me/api/portraits/women/44.jpg',
    rating: 4
  },
  {
    quote: 'Strong ownership; turned ambiguous requirements into shippable increments.',
    author: 'Kevin T.',
    role: 'CTO',
    avatar: 'https://randomuser.me/api/portraits/men/65.jpg',
    rating: 5
  },
  {
    quote: 'Very collaborative and proactive in identifying risks early.',
    author: 'Linda W.',
    role: 'Tech Lead',
    avatar: 'https://randomuser.me/api/portraits/women/22.jpg',
    rating: 4
  }
]

const sliderEl = ref<HTMLElement | null>(null)
const slideIndex = ref(0)
let sliderTimer: ReturnType<typeof setInterval> | null = null

function safeLen() {
  return Math.max(1, testimonials.length || 0)
}

function goSlide(i: number) {
  slideIndex.value = ((i % safeLen()) + safeLen()) % safeLen()
}

function nextSlide() {
  goSlide(slideIndex.value + 1)
}

function prevSlide() {
  goSlide(slideIndex.value - 1)
}

/* ---------- Tilt & scroll progress ---------- */
const scrollProgress = ref(0)

function handleScroll() {
  if (typeof window === 'undefined') return
  const scrolled = window.scrollY
  const height = Math.max(1, document.documentElement.scrollHeight - window.innerHeight)
  scrollProgress.value = Math.min(100, Math.max(0, (scrolled / height) * 100))
}

const tiltDisposers: Array<() => void> = []

function attachTilt() {
  if (typeof window === 'undefined') return
  const isFine = matchMedia('(hover:hover) and (pointer:fine)').matches
  const reduce = matchMedia('(prefers-reduced-motion: reduce)').matches
  if (!isFine || reduce) return
  const cards = document.querySelectorAll<HTMLElement>('[data-tilt]')
  cards.forEach((card) => {
    const onMove = (e: MouseEvent) => {
      const rect = card.getBoundingClientRect()
      const x = (e.clientX - rect.left) / rect.width
      const y = (e.clientY - rect.top) / rect.height
      const rotateX = (0.5 - y) * 8
      const rotateY = (x - 0.5) * 8
      card.style.transform = `perspective(900px) rotateX(${rotateX}deg) rotateY(${rotateY}deg)`
    }
    const onLeave = () => (card.style.transform = 'perspective(900px) rotateX(0) rotateY(0)')
    card.addEventListener('mousemove', onMove)
    card.addEventListener('mouseleave', onLeave)
    tiltDisposers.push(() => {
      card.removeEventListener('mousemove', onMove)
      card.removeEventListener('mouseleave', onLeave)
    })
  })
}

/* ---------- Lifecycle ---------- */
onMounted(async () => {
  const {default: AOS} = await import('aos')
  await import('aos/dist/aos.css')
  AOS.init({duration: 800, once: true})

  const reduceMotion = matchMedia('(prefers-reduced-motion: reduce)').matches
  if (!reduceMotion && (testimonials.length > 1)) {
    titleTimer = setInterval(rotateTitle, 2400)
    sliderTimer = setInterval(nextSlide, 3500)
  } else {
    if (((titles.value?.length ?? 0) > 1) && !reduceMotion)  titleTimer = setInterval(rotateTitle, 2400)
  }

  attachTilt()
  window.addEventListener('scroll', handleScroll, {passive: true})
  handleScroll()

  const el = sliderEl.value
  if (el && (testimonials.length > 1)) {
    const pause = () => sliderTimer && (clearInterval(sliderTimer), (sliderTimer = null))
    const resume = () => !reduceMotion && !sliderTimer && (sliderTimer = setInterval(nextSlide, 3500))
    el.addEventListener('mouseenter', pause)
    el.addEventListener('mouseleave', resume)
    el.addEventListener('focusin', pause)
    el.addEventListener('focusout', resume)
    tiltDisposers.push(() => {
      el.removeEventListener('mouseenter', pause)
      el.removeEventListener('mouseleave', resume)
      el.removeEventListener('focusin', pause)
      el.removeEventListener('focusout', resume)
    })
  }
})
onBeforeUnmount(() => {
  if (titleTimer) clearInterval(titleTimer)
  if (sliderTimer) clearInterval(sliderTimer)
  window.removeEventListener('scroll', handleScroll)
  tiltDisposers.forEach((fn) => fn())
})

/* ---------- Helpers ---------- */
const placeholderSrc = 'https://placehold.co/600x400?text=No+Image'

function scrollTo(id: string) {
  if (typeof window === 'undefined') return
  document.getElementById(id)?.scrollIntoView({behavior: 'smooth', block: 'start'})
}

function onImgError(e: Event) {
  const el = e.target as HTMLImageElement
  if (el && el.src !== placeholderSrc) el.src = placeholderSrc
}

function getYear(d: Date | string) {
  try {
    const dt = d instanceof Date ? d : new Date(d as any)
    return (dt as unknown as number) ? dt.getFullYear() : ''
  } catch {
    return ''
  }
}
const fmt = (d?: Date | null) => (d ? new Intl.DateTimeFormat('id-ID', {
  month: 'short',
  year: 'numeric'
}).format(new Date(d as any)) : '')
const period = (start?: Date | null, end?: Date | null) =>
    !start && !end ? '' : start && !end ? `${fmt(start)} — Present` : !start && end ? `— ${fmt(end)}` : `${fmt(start)} — ${fmt(end)}`

</script>

<style>
@reference "tailwindcss/theme";
@reference "tailwindcss/utilities";
/* === tokens, helpers, card, timeline, buttons, hero orbs, marquee, slider === */
:root {
  --c-text-h: #0f172a;
  --c-text-b: #1f2937;
  --c-text-m: #334155;
  --c-text-s: #475569;
  --c-bg-page: #f8fafc;
  --c-bg-muted: #f1f5f9;
  --c-bg-card: #ffffff;
  --c-bg-soft: #f8fafc;
  --c-bd-1: #cbd5e1;
  --c-bd-2: #e2e8f0;
  --c-ac-1: #0ea5e9;
  --c-ac-2: #6366f1;
  --c-ac-3: #f472b6
}

.dark {
  --c-text-h: #e5e7eb;
  --c-text-b: #cbd5e1;
  --c-text-m: #94a3b8;
  --c-text-s: #64748b;
  --c-bg-page: #020617;
  --c-bg-muted: #0b1220;
  --c-bg-card: rgba(2, 6, 23, .75);
  --c-bg-soft: rgba(15, 23, 42, .55);
  --c-bd-1: #334155;
  --c-bd-2: #1f2937;
  --c-ac-1: #38bdf8;
  --c-ac-2: #818cf8;
  --c-ac-3: #fb7185
}

main {
  background-color: var(--c-bg-page)
}

.bg-muted {
  background-color: var(--c-bg-muted)
}

.section-title {
  @apply text-4xl md:text-5xl font-bold;
  background: linear-gradient(90deg, var(--c-ac-1), var(--c-ac-2));
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent
}

.prose-p {
  color: var(--c-text-b);
  @apply mb-6 leading-relaxed dark:text-slate-300
}

.li-check {
  @apply flex items-center gap-3
}

.li-check::before {
  content: '';
  @apply inline-block w-4 h-4 rounded-full bg-emerald-500/90
}

.card {
  background-color: var(--c-bg-card);
  border: 1px solid var(--c-bd-2);
  @apply rounded-2xl p-6 shadow-lg transition-all backdrop-blur-xl
}

.dark .card {
  border-color: var(--c-bd-1)
}

.card:hover {
  box-shadow: 0 22px 55px -22px rgba(79, 70, 229, .28)
}

.kpi {
  background-color: var(--c-bg-soft);
  border: 1px solid var(--c-bd-2);
  @apply rounded-lg p-2
}

.dark .kpi {
  border-color: transparent
}

.kpi-v {
  @apply text-base font-semibold text-slate-900 dark:text-slate-100
}

.kpi-l {
  @apply text-[10px] uppercase tracking-wider text-slate-500
}

.stat-value {
  @apply text-4xl font-extrabold;
  background: linear-gradient(90deg, var(--c-ac-1), var(--c-ac-2));
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent
}

:root:not(.dark) #experience ol::before {
  background: linear-gradient(to bottom, #bae6fd, #e2e8f0, #f5d0fe)
}

.dark #experience ol::before {
  background: linear-gradient(to bottom, rgba(56, 189, 248, .5), rgba(51, 65, 85, .6), rgba(244, 114, 182, .45))
}

.timeline-dot {
  @apply absolute -start-3 flex h-6 w-6 items-center justify-center rounded-full text-white ring-8 ring-white dark:ring-slate-900;
  background-image: linear-gradient(135deg, var(--c-ac-2), var(--c-ac-1))
}

.btn-primary {
  @apply text-white shadow-lg transform hover:scale-105 transition-transform focus-visible:ring-2 focus-visible:ring-indigo-500;
  background-image: linear-gradient(90deg, var(--c-ac-1), var(--c-ac-2))
}

.btn-ghost {
  @apply border-2 transform hover:scale-105 transition-transform focus-visible:ring-2;
  color: var(--c-text-h);
  border-color: var(--c-text-h)
}

.dark .btn-ghost {
  color: #fff;
  border-color: #ffffff66
}

.btn-ghost:hover {
  background-color: currentColor;
  color: #fff
}

.dark .btn-ghost:hover {
  background-color: #fff;
  color: #0f172a
}

.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(32px);
  opacity: .55;
  mix-blend-mode: screen
}

.orb-1 {
  width: 42vw;
  height: 42vw;
  left: -10vw;
  top: -8vw;
  background: radial-gradient(circle at 30% 30%, #38bdf8, transparent 60%);
  animation: float 14s ease-in-out infinite
}

.orb-2 {
  width: 38vw;
  height: 38vw;
  right: -12vw;
  top: 10vh;
  background: radial-gradient(circle at 60% 40%, #6366f1, transparent 60%);
  animation: float 16s ease-in-out -4s infinite
}

.orb-3 {
  width: 46vw;
  height: 46vw;
  left: 10vw;
  bottom: -12vw;
  background: radial-gradient(circle at 40% 60%, #f472b6, transparent 60%);
  animation: float 18s ease-in-out -8s infinite
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) translateX(0)
  }
  50% {
    transform: translateY(-2.5vh) translateX(1.5vw)
  }
}

.bg-grid-slate {
  background-image: linear-gradient(to right, rgba(100, 116, 139, .11) 1px, transparent 1px), linear-gradient(to bottom, rgba(100, 116, 139, .11) 1px, transparent 1px);
  background-size: 24px 24px
}

.dark .bg-grid-slate {
  background-image: linear-gradient(to right, rgba(100, 116, 139, .22) 1px, transparent 1px), linear-gradient(to bottom, rgba(100, 116, 139, .22) 1px, transparent 1px)
}

.marquee {
  position: relative;
  overflow: hidden;
  mask-image: linear-gradient(to right, transparent, black 10%, black 90%, transparent)
}

.marquee__track {
  display: flex;
  gap: 48px;
  animation: marquee 16s linear infinite
}

.marquee__item {
  flex: 0 0 auto
}

@keyframes marquee {
  from {
    transform: translateX(0)
  }
  to {
    transform: translateX(-50%)
  }
}

/* (lama) util slider sederhana */
.slider {
  position: relative;
  overflow: hidden
}

.slider__track {
  display: grid;
  grid-auto-flow: column;
  grid-auto-columns: 100%;
  transition: transform .5s ease
}

.slider__item {
  padding: .25rem
}

.slider__dots {
  display: flex;
  justify-content: center;
  gap: .5rem;
  margin-top: .75rem
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 9999px;
  background: rgba(148, 163, 184, .62)
}

.dot--active {
  background: rgb(99, 102, 241)
}

/* ===== Testimonials slider (no side bug) ===== */
.ts__track {
  display: grid;
  grid-auto-flow: column;
  grid-auto-columns: 100%;
  gap: 0;
  transition: transform .55s cubic-bezier(.2, .6, .2, 1);
  will-change: transform;
}

.ts__item {
  padding: 0 .5rem;
  display: flex;
  margin-left: 20px;
  align-items: stretch;
}

@media (min-width: 768px) {
  .ts__item {
    padding: 0 .75rem
  }
}

/* ===== FULL WIDTH mode: 1 slide = 100% ===== */
.ts--full .ts__track {
  grid-auto-columns: 100%
}

.ts--full .ts__item {
  padding: 0
}

.ts--full .card {
  width: 100%
}

@keyframes gradientXY {
  0% {
    background-position: 0% 50%
  }
  50% {
    background-position: 100% 50%
  }
  100% {
    background-position: 0% 50%
  }
}
</style>
