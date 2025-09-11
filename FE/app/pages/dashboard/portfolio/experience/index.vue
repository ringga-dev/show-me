<script setup lang="ts">
import { watchDebounced } from '@vueuse/core'

definePageMeta({ layout: 'dashboard' })

/* ---------- types (aman kalau belum ada global type) ---------- */
type UserPortfolioExperience = {
  id?: string
  title?: string
  company?: string
  location?: string
  startDate?: string
  endDate?: string
  description?: string
  technologies?: string[]
  highlights?: string[]
  createdAt?: string
  updatedAt?: string
}

/* ---------- store & router ---------- */
const store  = useUserPortfolioStore()
const router = useRouter()

/* ---------- state ---------- */
const items   = ref<UserPortfolioExperience[]>([])
const loading = ref(false)
const error   = ref<string | null>(null)

/* ---------- search, sort, filter & pagination ---------- */
const q            = ref('')
const debouncedQ   = ref('')
const page         = ref(1)
const pageSize     = 6 as const
const sortKey      = ref<'title' | 'company' | 'updatedAt'>('updatedAt')
const sortDir      = ref<'asc' | 'desc'>('desc')
const rangeFilter  = ref<'all' | 'current' | 'past'>('all')
const techFilter   = ref<string>('') // '' = all

watchDebounced(
    q,
    val => { debouncedQ.value = val; page.value = 1 },
    { debounce: 250, maxWait: 500 }
)

/* ---------- fetch ---------- */
async function getData() {
  loading.value = true
  error.value = null
  try {
    const res = await store.getPortfolioExperience()
    items.value = res?.data ?? []
  } catch (e: any) {
    error.value = e?.message || 'Gagal memuat data'
  } finally {
    loading.value = false
  }
}
onMounted(getData)

/* ---------- utils ---------- */
const lc = (v?: string | number | null) => (v ?? '').toString().toLowerCase()
const isCurrent = (end?: string) => !end || !end.trim()
const formatDate = (iso?: string) => {
  if (!iso) return '—'
  try { return new Date(iso).toLocaleDateString() } catch { return iso }
}
const rangeText = (a?: string, b?: string) => {
  const s = (a || '').trim()
  const e = (b || '').trim()
  if (!s && !e) return '—'
  return `${s || '—'} – ${e || 'Present'}`
}
const initials = (name?: string) =>
    (name || 'X')
        .split(' ')
        .map(p => p.trim()[0])
        .filter(Boolean)
        .slice(0, 2)
        .join('')
        .toUpperCase()

/* ---------- tech helpers ---------- */
const allTechs = computed(() => {
  const set = new Set<string>()
  for (const it of items.value) (it.technologies || []).forEach(t => t && set.add(t))
  return Array.from(set).sort((a, b) => a.localeCompare(b))
})
const techFreq = computed(() => {
  const map = new Map<string, number>()
  for (const it of items.value) {
    for (const t of it.technologies || []) {
      const key = (t || '').trim()
      if (!key) continue
      map.set(key, (map.get(key) || 0) + 1)
    }
  }
  return Array.from(map.entries()).sort((a, b) => b[1] - a[1])
})
const topTechs = computed(() => techFreq.value.slice(0, 3).map(([t]) => t))

/* ---------- computed: filter -> sort -> paginate ---------- */
const filtered = computed(() => {
  // text filter
  const base = !debouncedQ.value
      ? items.value
      : items.value.filter(x =>
          lc(x.title).includes(debouncedQ.value.toLowerCase()) ||
          lc(x.company).includes(debouncedQ.value.toLowerCase()) ||
          lc(x.location).includes(debouncedQ.value.toLowerCase()) ||
          (x.technologies || []).some(t => lc(t).includes(debouncedQ.value.toLowerCase()))
      )

  // range filter
  const byRange = base.filter(x => {
    if (rangeFilter.value === 'all') return true
    return rangeFilter.value === 'current' ? isCurrent(x.endDate) : !isCurrent(x.endDate)
  })

  // tech filter
  if (!techFilter.value) return byRange
  return byRange.filter(x => (x.technologies || []).some(t => t === techFilter.value))
})

const sorted = computed(() => {
  const arr = [...filtered.value]
  const dir = sortDir.value === 'asc' ? 1 : -1
  arr.sort((a, b) => {
    if (sortKey.value === 'title') {
      const va = lc(a.title), vb = lc(b.title)
      if (va < vb) return -1 * dir
      if (va > vb) return 1 * dir
      return 0
    }
    if (sortKey.value === 'company') {
      const va = lc(a.company), vb = lc(b.company)
      if (va < vb) return -1 * dir
      if (va > vb) return 1 * dir
      return 0
    }
    // updatedAt (fallback createdAt)
    const da = new Date(a.updatedAt || a.createdAt || 0).getTime()
    const db = new Date(b.updatedAt || b.createdAt || 0).getTime()
    if (da < db) return -1 * dir
    if (da > db) return 1 * dir
    return 0
  })
  return arr
})

const paginated  = computed(() => {
  const start = (page.value - 1) * pageSize
  return sorted.value.slice(start, start + pageSize)
})
const total      = computed(() => filtered.value.length)
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize)))

watch([sortKey, sortDir, rangeFilter, techFilter], () => (page.value = 1))

/* ---------- KPI summary ---------- */
const totalExp    = computed(() => items.value.length)
const currentExp  = computed(() => items.value.filter(x => isCurrent(x.endDate)).length)
const uniqueTechs = computed(() => allTechs.value.length)

/* ---------- actions ---------- */
function goCreate() {
  router.push('/dashboard/portfolio/experience/create')
}
function goEdit(id?: string) {
  if (!id) return
  router.push(`/dashboard/portfolio/experience/${id}`)
}
async function del(id?: string) {
  if (!id) return
  const ok = confirm('Hapus pengalaman ini? Tindakan tidak bisa dibatalkan.')
  if (!ok) return
  loading.value = true
  try {
    await store.deletePortfolioExperience(id)
  } finally {
    loading.value = false
  }
  await getData()
}
</script>

<template>
  <div class="space-y-4">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
      <h1 class="text-2xl font-semibold text-slate-800 dark:text-white">Experience</h1>

      <div class="flex flex-wrap items-center gap-2">
        <!-- Sort -->

        <UButton
            :icon="sortDir === 'asc' ? 'i-heroicons-arrow-up-20-solid' : 'i-heroicons-arrow-down-20-solid'"
            variant="soft"
            aria-label="Toggle sort direction"
            @click="sortDir = sortDir === 'asc' ? 'desc' : 'asc'"
        />
        <!-- Range filter -->
        <div class="hidden sm:flex items-center gap-1">
          <UButton
              size="xs"
              :variant="rangeFilter === 'all' ? 'solid' : 'ghost'"
              icon="i-heroicons-list-bullet"
              @click="rangeFilter = 'all'"
          >All</UButton>
          <UButton
              size="xs"
              :variant="rangeFilter === 'current' ? 'solid' : 'ghost'"
              icon="i-heroicons-bolt"
              @click="rangeFilter = 'current'"
          >Current</UButton>
          <UButton
              size="xs"
              :variant="rangeFilter === 'past' ? 'solid' : 'ghost'"
              icon="i-heroicons-clock"
              @click="rangeFilter = 'past'"
          >Past</UButton>
        </div>

        <UButton
            variant="soft"
            icon="i-heroicons-arrow-path"
            @click="getData"
        >
          Refresh
        </UButton>
        <UButton icon="i-heroicons-plus" @click="goCreate">
          Add Experience
        </UButton>
      </div>
    </div>

    <!-- Filters row -->
    <div class="flex flex-col sm:flex-row sm:items-center gap-3">
      <UInput
          v-model="q"
          placeholder="Cari judul, perusahaan, lokasi, atau teknologi…"
          icon="i-heroicons-magnifying-glass"
          :trailing="!!q"
          class="w-full sm:max-w-xs"
      >
        <template v-if="q" #trailing>
          <UButton
              variant="ghost"
              icon="i-heroicons-x-mark-20-solid"
              aria-label="Clear search"
              @click="q = ''"
          />
        </template>
      </UInput>

      <div v-if="!loading && total" class="text-sm text-slate-500 dark:text-slate-400">
        {{ total }} result{{ total > 1 ? 's' : '' }}
      </div>
    </div>

    <!-- KPI summary -->
    <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
      <UCard class="p-4">
        <div class="flex items-center justify-between">
          <div>
            <div class="text-xs text-slate-500 dark:text-slate-400">Total Experience</div>
            <div class="text-2xl font-semibold text-slate-800 dark:text-white">{{ totalExp }}</div>
          </div>
          <UIcon name="i-heroicons-briefcase" class="w-8 h-8 text-slate-400" />
        </div>
      </UCard>
      <UCard class="p-4">
        <div class="flex items-center justify-between">
          <div>
            <div class="text-xs text-slate-500 dark:text-slate-400">Current Roles</div>
            <div class="text-2xl font-semibold text-slate-800 dark:text-white">{{ currentExp }}</div>
          </div>
          <UIcon name="i-heroicons-bolt" class="w-8 h-8 text-amber-500" />
        </div>
      </UCard>
      <UCard class="p-4">
        <div class="flex items-center justify-between w-full">
          <div class="min-w-0">
            <div class="text-xs text-slate-500 dark:text-slate-400">Top Tech</div>
            <div class="flex flex-wrap gap-1 mt-1">
              <UBadge v-for="t in topTechs" :key="t" variant="subtle">{{ t }}</UBadge>
            </div>
          </div>
          <div class="text-right">
            <div class="text-xs text-slate-500 dark:text-slate-400">Unique Tech</div>
            <div class="text-2xl font-semibold text-slate-800 dark:text-white">{{ uniqueTechs }}</div>
          </div>
        </div>
      </UCard>
    </div>

    <!-- Error -->
    <UAlert
        v-if="error"
        icon="i-heroicons-exclamation-triangle"
        color="error"
        variant="soft"
        title="Tidak bisa memuat data"
        :description="error"
    />

    <!-- Content -->
    <div>
      <!-- Loading skeletons -->
      <div v-if="loading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
        <UCard v-for="i in 6" :key="'skel-'+i" class="p-4">
          <div class="flex items-start justify-between gap-2 mb-3">
            <div class="flex items-center gap-3">
              <USkeleton class="w-10 h-10 rounded-full" />
              <div class="space-y-2">
                <USkeleton class="h-4 w-40" />
                <USkeleton class="h-3 w-24" />
              </div>
            </div>
            <USkeleton class="h-6 w-20 rounded-full" />
          </div>
          <USkeleton class="h-3 w-28 mb-3" />
          <USkeleton class="h-12 w-full rounded-md" />
          <div class="flex flex-wrap gap-2 mt-3">
            <USkeleton v-for="j in 4" :key="'chip-'+j" class="h-6 w-16 rounded-full" />
          </div>
        </UCard>
      </div>

      <!-- Grid -->
      <div v-else-if="paginated.length" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
        <UCard
            v-for="exp in paginated"
            :key="exp.id || exp.title"
            class="p-4 hover:shadow-md transition-shadow"
        >
          <!-- Header -->
          <div class="flex items-start justify-between gap-2">
            <div class="flex items-start gap-3 min-w-0">
              <UAvatar size="md" class="rounded-full">
                <template #fallback>
                  <span class="text-xs">{{ initials(exp.company) }}</span>
                </template>
              </UAvatar>
              <div class="min-w-0">
                <div class="text-base font-semibold text-slate-800 dark:text-white truncate">
                  {{ exp.title || '—' }}
                </div>
                <div class="text-xs text-slate-500 dark:text-slate-400 truncate">
                  {{ exp.company || '—' }} • {{ exp.location || '—' }}
                </div>
              </div>
            </div>

            <UBadge
                :color="isCurrent(exp.endDate) ? 'primary' : 'gray'"
                variant="subtle"
                class="shrink-0"
            >
              {{ isCurrent(exp.endDate) ? 'Current' : 'Ended' }}
            </UBadge>
          </div>

          <!-- Range -->
          <div class="mt-1 text-xs text-slate-500 dark:text-slate-400">
            {{ rangeText(exp.startDate, exp.endDate) }}
          </div>

          <!-- Highlights (jika ada) -->
          <ul v-if="exp.highlights?.length" class="mt-3 space-y-1 text-sm text-slate-700 dark:text-slate-300">
            <li v-for="(h, i) in exp.highlights.slice(0, 3)" :key="i" class="flex gap-2">
              <UIcon name="i-heroicons-check-circle" class="w-4 h-4 mt-0.5 text-emerald-500" />
              <span class="line-clamp-2">{{ h }}</span>
            </li>
          </ul>

          <!-- Description fallback -->
          <p v-else-if="exp.description" class="text-sm text-slate-600 dark:text-slate-300 mt-3 line-clamp-3">
            {{ exp.description }}
          </p>

          <!-- Tech chips -->
          <div v-if="exp.technologies?.length" class="flex flex-wrap gap-1.5 mt-3">
            <UBadge
                v-for="(t, i) in exp.technologies"
                :key="i"
                variant="subtle"
            >
              {{ t }}
            </UBadge>
          </div>

          <!-- Footer -->
          <div class="mt-4 flex items-center justify-between">
            <div class="text-xs text-slate-500 dark:text-slate-400">
              Updated {{ formatDate(exp.updatedAt) }}
            </div>
            <div class="flex gap-1">
              <UButton size="xs" variant="soft" icon="i-heroicons-pencil-square" @click="goEdit(exp.id)" />
              <UButton size="xs" color="error" variant="soft" icon="i-heroicons-trash" @click="del(exp.id)" />
            </div>
          </div>
        </UCard>
      </div>

      <!-- Empty -->
      <UCard v-else class="py-12 text-center">
        <div class="flex flex-col items-center gap-2">
          <UIcon name="i-heroicons-briefcase" class="w-8 h-8 text-slate-400" />
          <p class="text-slate-500 dark:text-slate-400">Belum ada pengalaman.</p>
          <div class="flex items-center gap-2 mt-2">
            <UButton icon="i-heroicons-plus" @click="goCreate">Tambah Experience</UButton>
            <UButton variant="ghost" icon="i-heroicons-arrow-path" @click="q = ''">Reset Pencarian</UButton>
          </div>
        </div>
      </UCard>
    </div>

    <!-- Pagination -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pt-2">
      <div v-if="!loading && total" class="text-xs text-slate-500 dark:text-slate-400">
        Halaman {{ page }} dari {{ totalPages }}
      </div>
      <div class="flex justify-end">
        <UPagination v-model:page="page" :items-per-page="pageSize" :total="total" />
      </div>
    </div>
  </div>
</template>
