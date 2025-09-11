<script setup lang="ts">
import { watchDebounced } from '@vueuse/core'

definePageMeta({ layout: 'dashboard' })

/* ---------- types ---------- */
type SkillItem = { name: string; percentage: number }
type SkillCategory = {
  id: string
  title: string
  description?: string
  skillData: SkillItem[]
  createdAt?: string
  updatedAt?: string
}

/* ---------- store & state ---------- */
const skillStore = useUserPortfolioStore()

const categories = ref<SkillCategory[]>([])
const loading = ref(false)
const error   = ref<string | null>(null)

/* ---------- search, sort & pagination ---------- */
const q          = ref('')
const debouncedQ = ref('')
const page       = ref(1)
const pageSize   = 6 as const
const sortKey    = ref<'title' | 'count' | 'updatedAt'>('title')
const sortDir    = ref<'asc' | 'desc'>('asc')

watchDebounced(
    q,
    (val) => {
      debouncedQ.value = val
      page.value = 1
    },
    { debounce: 250, maxWait: 500 }
)

/* ---------- fetch ---------- */
async function getData() {
  loading.value = true
  error.value = null
  try {
    const res = await skillStore.getPortfolioSkill() // { data: SkillCategory[] }
    categories.value = res?.data ?? []
  } catch (e: unknown) {
    error.value = e instanceof Error ? e.message : 'Gagal memuat data'
  } finally {
    loading.value = false
  }
}
onMounted(getData)

/* ---------- utils ---------- */
const safeLower = (v?: string | number | null) => (v ?? '').toString().toLowerCase()

const clampPercent = (n: number) => {
  if (Number.isNaN(n)) return 0
  return Math.max(0, Math.min(100, Math.round(n)))
}
const barClass = (p: number) => {
  if (p >= 80) return 'bg-emerald-500 dark:bg-emerald-400'
  if (p >= 50) return 'bg-amber-500 dark:bg-amber-400'
  return 'bg-rose-500 dark:bg-rose-400'
}
const gradientFor = (p: number) => {
  const v = clampPercent(p)
  if (v >= 80) return 'linear-gradient(90deg,#34d399,#06b6d4)'
  if (v >= 50) return 'linear-gradient(90deg,#f59e0b,#f97316)'
  return 'linear-gradient(90deg,#ef4444,#f43f5e)'
}
const avgPercent = (items?: SkillItem[]) => {
  const arr = (items ?? []).map(s => clampPercent(Number(s.percentage)))
  if (!arr.length) return 0
  return Math.round(arr.reduce((a, b) => a + b, 0) / arr.length)
}
const formatDate = (iso?: string) => {
  if (!iso) return '—'
  try { return new Date(iso).toLocaleDateString() } catch { return iso }
}

/* ---------- ring progress helper ---------- */
const R = 26
const C = 2 * Math.PI * R
const ringDashOffset = (p: number) => {
  const v = clampPercent(p)
  return ((100 - v) / 100) * C
}
const ringStrokeColor = (p: number) => {
  const v = clampPercent(p)
  if (v >= 80) return '#10b981'
  if (v >= 50) return '#f59e0b'
  return '#ef4444'
}

/* ---------- computed (filter/sort/paginate) ---------- */
const filtered = computed(() => {
  if (!debouncedQ.value) return categories.value
  const query = debouncedQ.value.toLowerCase()
  return categories.value.filter(cat => {
    const inTitle = safeLower(cat.title).includes(query)
    const inSkills = (cat.skillData || []).some(s =>
        safeLower(s.name).includes(query) || safeLower(s.percentage).includes(query)
    )
    return inTitle || inSkills
  })
})

const sorted = computed(() => {
  const arr = [...filtered.value]
  const dir = sortDir.value === 'asc' ? 1 : -1

  arr.sort((a, b) => {
    if (sortKey.value === 'title') {
      const va = safeLower(a.title)
      const vb = safeLower(b.title)
      if (va < vb) return -1 * dir
      if (va > vb) return 1 * dir
      return 0
    }
    if (sortKey.value === 'count') {
      const ca = a.skillData?.length ?? 0
      const cb = b.skillData?.length ?? 0
      if (ca < cb) return -1 * dir
      if (ca > cb) return 1 * dir
      return 0
    }
    // updatedAt (fallback ke createdAt)
    const da = new Date(a.updatedAt || a.createdAt || 0).getTime()
    const db = new Date(b.updatedAt || b.createdAt || 0).getTime()
    if (da < db) return -1 * dir
    if (da > db) return 1 * dir
    return 0
  })

  return arr
})

const paginated = computed(() => {
  const start = (page.value - 1) * pageSize
  return sorted.value.slice(start, start + pageSize)
})

const total = computed(() => filtered.value.length)
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize)))

watch([sortKey, sortDir], () => (page.value = 1))

/* ---------- actions ---------- */
function editCategory(id?: string) {
  if (!id) return
  navigateTo(`/dashboard/portfolio/skill/${id}`)
}
async function deleteCategory(id?: string) {
  if (!id) return
  const ok = confirm('Hapus kategori ini? Tindakan tidak bisa dibatalkan.')
  if (!ok) return

  loading.value = true
  error.value = null
  try {
    const res = await skillStore.deletePortfolioSkill(id)
    if (res?.message?.[0]) {
      // tampilkan pesan jika backend mengirim info
      error.value = res.message[0]
    }
  } catch (e: unknown) {
    error.value = e instanceof Error ? e.message : 'Gagal menghapus kategori'
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
      <h1 class="text-2xl font-semibold text-slate-800 dark:text-white">Skills</h1>

      <div class="flex items-center gap-2">

        <UButton
            :icon="sortDir === 'asc' ? 'i-heroicons-arrow-up-20-solid' : 'i-heroicons-arrow-down-20-solid'"
            variant="soft"
            aria-label="Toggle sort direction"
            @click="sortDir = sortDir === 'asc' ? 'desc' : 'asc'"
        />
        <UButton
            to="/dashboard/portfolio/skill/create"
            icon="i-heroicons-plus"
            as="nuxt-link"
        >
          Add Category
        </UButton>
      </div>
    </div>

    <!-- Filters -->
    <div class="flex flex-col sm:flex-row sm:items-center gap-3">
      <UInput
          v-model="q"
          placeholder="Cari kategori atau skill…"
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
          <div class="space-y-3">
            <USkeleton class="h-5 w-2/3" />
            <USkeleton class="h-3 w-1/3" />
            <div class="flex items-center justify-between pt-2">
              <USkeleton class="w-16 h-16 rounded-full" />
              <USkeleton class="h-8 w-20 rounded-lg" />
            </div>
            <div class="space-y-2 pt-2">
              <div v-for="j in 3" :key="'bar-'+j" class="space-y-1">
                <div class="flex items-center justify-between">
                  <USkeleton class="h-3 w-24" />
                  <USkeleton class="h-4 w-10 rounded-full" />
                </div>
                <USkeleton class="h-3 w-full rounded-full" />
              </div>
            </div>
          </div>
        </UCard>
      </div>

      <!-- Grid cards -->
      <div v-else-if="paginated.length" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
        <UCard
            v-for="cat in paginated"
            :key="cat.id"
            class="p-4"
        >
          <!-- Header kategori -->
          <div class="flex items-start justify-between gap-3">
            <div class="min-w-0">
              <div class="text-base font-semibold text-slate-800 dark:text-white truncate">
                {{ cat.title || '—' }}
              </div>
              <p v-if="cat.description" class="text-xs text-slate-500 dark:text-slate-400 mt-0.5 truncate">
                {{ cat.description }}
              </p>
              <div class="mt-1 text-xs text-slate-500 dark:text-slate-400">
                {{ cat.skillData?.length || 0 }} skill • Updated {{ formatDate(cat.updatedAt) }}
              </div>
            </div>

            <!-- Ring average -->
            <div class="relative w-[72px] h-[72px] shrink-0">
              <svg viewBox="0 0 64 64" class="w-[72px] h-[72px] -rotate-90">
                <circle cx="32" cy="32" :r="26" stroke-width="8" fill="none"
                        class="text-gray-200 dark:text-gray-800" stroke="currentColor" />
                <circle cx="32" cy="32" :r="26" stroke-width="8" stroke-linecap="round" fill="none"
                        class="transition-[stroke-dashoffset] duration-500 will-change-auto"
                        :style="{
                          strokeDasharray: (2 * Math.PI * 26) + 'px',
                          strokeDashoffset: ringDashOffset(avgPercent(cat.skillData)) + 'px',
                          stroke: ringStrokeColor(avgPercent(cat.skillData))
                        }" />
              </svg>
              <div class="absolute inset-0 rotate-90 flex items-center justify-center">
                <span class="text-sm font-semibold text-slate-700 dark:text-slate-200">
                  {{ avgPercent(cat.skillData) }}%
                </span>
              </div>
            </div>
          </div>

          <!-- Daftar skill -->
          <div class="space-y-3 mt-4">
            <div
                v-for="(s, i) in cat.skillData"
                :key="cat.id + '-' + i + '-' + s.name"
                class="group"
            >
              <div class="flex items-center justify-between mb-1">
                <span class="text-sm font-medium text-gray-800 dark:text-gray-200 truncate">{{ s.name }}</span>
                <span class="text-xs font-semibold px-2 py-0.5 rounded-full bg-gray-100 text-gray-700 dark:bg-gray-800 dark:text-gray-300">
                  {{ clampPercent(s.percentage) }}%
                </span>
              </div>
              <div class="h-3 w-full rounded-full bg-gray-200/70 dark:bg-gray-800/70 overflow-hidden">
                <div
                    class="h-3 rounded-full transition-all duration-500"
                    :class="barClass(clampPercent(s.percentage))"
                    :style="{ width: clampPercent(s.percentage) + '%', background: gradientFor(s.percentage) }"
                    role="progressbar"
                    :aria-valuenow="clampPercent(s.percentage)"
                    aria-valuemin="0"
                    aria-valuemax="100"
                    :aria-label="`${s.name} ${clampPercent(s.percentage)} percent`"
                />
              </div>
            </div>
          </div>

          <!-- Actions -->
          <div class="flex justify-end items-center gap-1 mt-4">
            <UButton
                icon="i-heroicons-pencil-square"
                size="xs"
                variant="soft"
                aria-label="Edit category"
                @click="editCategory(cat.id)"
            />
            <UButton
                icon="i-heroicons-trash"
                size="xs"
                color="error"
                variant="soft"
                aria-label="Delete category"
                @click="deleteCategory(cat.id)"
            />
          </div>
        </UCard>
      </div>

      <!-- Empty state -->
      <UCard v-else class="py-12 text-center">
        <div class="flex flex-col items-center gap-2">
          <UIcon name="i-heroicons-chart-bar" class="w-8 h-8 text-slate-400" />
          <p class="text-slate-500 dark:text-slate-400">Tidak ada kategori skill.</p>
          <div class="flex items-center gap-2 mt-2">
            <UButton to="/dashboard/portfolio/skill/create" as="nuxt-link" icon="i-heroicons-plus">Tambah Kategori</UButton>
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
        <UPagination
            v-model:page="page"
            :items-per-page="pageSize"
            :total="total"
            class="rounded-lg"
        />
      </div>
    </div>
  </div>
</template>
