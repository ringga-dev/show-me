<script setup lang="ts">
import { watchDebounced } from '@vueuse/core'

definePageMeta({ layout: 'dashboard' })



const store   = useUserPortfolioStore()
const router  = useRouter()
const toast   = useToast()

/* ---------- state ---------- */
const items   = ref<Project[]>([])
const loading = ref(false)
const error   = ref<string | null>(null)

/* ---------- search, sort, filter & pagination ---------- */
const q            = ref('')
const debouncedQ   = ref('')
const page         = ref(1)
const pageSize     = 6 as const
const sortKey      = ref<'updatedAt'|'title'|'rating'>('updatedAt')
const sortDir      = ref<'asc'|'desc'>('desc')
const techFilter   = ref<string>('')   // '' = all
const minRating    = ref<number>(0)    // 0..5
const onlyWithLink = ref<boolean>(false)

watchDebounced(q, (val) => { debouncedQ.value = val; page.value = 1 }, { debounce: 250, maxWait: 500 })

/* ---------- fetch ---------- */
async function getData() {
  loading.value = true
  error.value = null
  try {
    const res = await store.getPortfolioProjects?.()
    items.value = res?.data ?? []
  } catch (e: any) {
    error.value = e?.message || 'Gagal memuat project'
  } finally {
    loading.value = false
  }
}
onMounted(getData)

/* ---------- utils ---------- */
const lc = (v?: string | number | null) => (v ?? '').toString().toLowerCase()
const DEFAULT_IMG = 'https://images.unsplash.com/photo-1518779578993-ec3579fee39f?auto=format&fit=crop&w=1200&q=60'
const fmtDate = (iso?: string | null) => { if (!iso) return '—'; try { return new Date(iso).toLocaleDateString() } catch { return iso as string } }
function onImgErr(e: Event) { (e.target as HTMLImageElement).src = DEFAULT_IMG }
const clampRating = (n?: number | null) => { const v = Number(n ?? 0); return Math.min(5, Math.max(0, Math.round(v))) }
const starArray = (n?: number | null) => Array.from({ length: 5 }, (_, i) => i < clampRating(n))

/* ---------- tech list for filter ---------- */
const allTechs = computed(() => {
  const set = new Set<string>()
  for (const it of items.value) (it.technologies || []).forEach(t => t && set.add(t))
  return Array.from(set).sort((a,b)=>a.localeCompare(b))
})

/* ---------- computed ---------- */
const filtered = computed(() => {
  const qy = debouncedQ.value.toLowerCase()
  const tech = techFilter.value
  return items.value.filter(p => {
    const matchQ = !qy || lc(p.title).includes(qy) || lc(p.description).includes(qy) ||
        (p.technologies || []).some(t => lc(t).includes(qy))
    const matchTech = !tech || (p.technologies || []).includes(tech)
    const matchRating = clampRating(p.rating) >= minRating.value
    const matchLink = !onlyWithLink.value || !!(p.githubUrl || p.demoUrl)
    return matchQ && matchTech && matchRating && matchLink
  })
})

const sorted = computed(() => {
  const arr = [...filtered.value]
  const dir = sortDir.value === 'asc' ? 1 : -1
  arr.sort((a,b) => {
    if (sortKey.value === 'title') {
      const va = lc(a.title), vb = lc(b.title)
      if (va < vb) return -1 * dir; if (va > vb) return 1 * dir; return 0
    }
    if (sortKey.value === 'rating') {
      const va = clampRating(a.rating), vb = clampRating(b.rating)
      if (va < vb) return -1 * dir; if (va > vb) return 1 * dir; return 0
    }
    const da = new Date(a.updatedAt || a.createdAt || 0).getTime()
    const db = new Date(b.updatedAt || b.createdAt || 0).getTime()
    if (da < db) return -1 * dir; if (da > db) return 1 * dir; return 0
  })
  return arr
})

const paginated  = computed(() => sorted.value.slice((page.value - 1) * pageSize, (page.value) * pageSize))
const total      = computed(() => filtered.value.length)
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize)))
watch([sortKey, sortDir, techFilter, minRating, onlyWithLink], () => (page.value = 1))

/* ---------- actions ---------- */
function goCreate() { router.push('/dashboard/portfolio/project/create') }
function goEdit(id?: string) { if (id) router.push(`/dashboard/portfolio/project/${id}`) }
async function del(id?: string) {
  if (!id) return
  if (!confirm('Hapus project ini? Tindakan tidak bisa dibatalkan.')) return
  loading.value = true
  try {
    await store.deletePortfolioProject?.(id)
    toast.add({ title: 'Deleted', description: 'Project berhasil dihapus.' })
  } catch (e: any) {
    toast.add({ color: 'error', title: 'Gagal', description: e?.message || 'Gagal menghapus project' })
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
      <h1 class="text-2xl font-semibold text-slate-800 dark:text-white">Projects</h1>

      <div class="flex items-center gap-2">

        <UButton
            :icon="sortDir==='asc' ? 'i-heroicons-arrow-up-20-solid' : 'i-heroicons-arrow-down-20-solid'"
            variant="soft" @click="sortDir = sortDir==='asc' ? 'desc':'asc'"
        />
        <UButton icon="i-heroicons-plus" @click="goCreate">Add Project</UButton>
      </div>
    </div>

    <!-- Filters -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-3">
      <UInput
          v-model="q" placeholder="Cari judul, deskripsi, teknologi…"
          icon="i-heroicons-magnifying-glass" :trailing="!!q"
      >
        <template v-if="q" #trailing>
          <UButton variant="ghost" icon="i-heroicons-x-mark-20-solid" @click="q=''" />
        </template>
      </UInput>


      <UToggle v-model="onlyWithLink" on-icon="i-heroicons-link" off-icon="i-heroicons-link-slash">
        Only with link
      </UToggle>
    </div>

    <!-- Error -->
    <UAlert
        v-if="error" icon="i-heroicons-exclamation-triangle" color="error" variant="soft"
        title="Tidak bisa memuat data" :description="error"
    />

    <!-- Content -->
    <div>
      <!-- Loading -->
      <div v-if="loading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
        <UCard v-for="i in 6" :key="'skel-'+i" class="p-0 overflow-hidden">
          <USkeleton class="h-40 w-full" />
          <div class="p-4 space-y-3">
            <USkeleton class="h-5 w-2/3" />
            <USkeleton class="h-3 w-1/2" />
            <div class="flex gap-1">
              <USkeleton v-for="j in 3" :key="j" class="h-6 w-16 rounded-full" />
            </div>
          </div>
        </UCard>
      </div>

      <!-- Grid -->
      <div v-else-if="paginated.length" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
        <UCard v-for="p in paginated" :key="p.id" class="p-0 overflow-hidden">
          <!-- Image -->
          <div class="relative h-44 bg-gray-100 dark:bg-gray-900">
            <img :src="p.imageUrl || DEFAULT_IMG" @error="onImgErr" class="absolute inset-0 w-full h-full object-cover" />
            <div class="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent" />
            <div class="absolute bottom-2 left-2 right-2 flex items-center justify-between">
              <div class="min-w-0">
                <div class="text-white font-semibold truncate">{{ p.title || '—' }}</div>
                <div v-if="p.releseVersion" class="text-white/80 text-xs mt-0.5 truncate">
                  v{{ p.releseVersion }}
                </div>
              </div>
              <!-- rating -->
              <div class="flex gap-0.5">
                <UIcon
                    v-for="(on, i) in starArray(p.rating)"
                    :key="i"
                    :name="on ? 'i-heroicons-star-20-solid' : 'i-heroicons-star'"
                    class="w-4 h-4" :class="on ? 'text-amber-400' : 'text-white/50'"
                />
              </div>
            </div>
          </div>

          <!-- Body -->
          <div class="p-4">
            <p v-if="p.description" class="text-sm text-slate-600 dark:text-slate-300 line-clamp-3">
              {{ p.description }}
            </p>

            <div v-if="p.technologies?.length" class="flex flex-wrap gap-1.5 mt-3">
              <UBadge v-for="(t, i) in p.technologies" :key="i" variant="subtle">{{ t }}</UBadge>
            </div>

            <div class="flex items-center justify-between mt-4 text-xs text-slate-500 dark:text-slate-400">
              <span>Updated {{ fmtDate(p.updatedAt) }}</span>
              <div class="flex gap-1">
                <UButton size="xs" variant="soft" icon="i-heroicons-pencil-square" @click="goEdit(p.id)" />
                <UButton size="xs" color="error" variant="soft" icon="i-heroicons-trash" @click="del(p.id)" />
              </div>
            </div>

            <div class="flex gap-2 mt-3">
              <UButton v-if="p.githubUrl" size="xs" variant="outline" icon="i-heroicons-command-line" :to="p.githubUrl" target="_blank">Code</UButton>
              <UButton v-if="p.demoUrl" size="xs" variant="outline" icon="i-heroicons-globe-alt" :to="p.demoUrl" target="_blank">Demo</UButton>
            </div>
          </div>
        </UCard>
      </div>

      <!-- Empty -->
      <UCard v-else class="py-12 text-center">
        <div class="flex flex-col items-center gap-2">
          <UIcon name="i-heroicons-cube" class="w-8 h-8 text-slate-400" />
          <p class="text-slate-500 dark:text-slate-400">Belum ada project.</p>
          <div class="flex items-center gap-2 mt-2">
            <UButton icon="i-heroicons-plus" @click="goCreate">Tambah Project</UButton>
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
