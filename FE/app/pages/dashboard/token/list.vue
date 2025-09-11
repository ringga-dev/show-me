<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import type { TokenModel } from '~/types/models/TokenModel'
import type { BaseResponse } from '~/types/models/BaseResponse'
import { useTokenStore } from '~/stores/token'
import { useToast } from '#imports'

definePageMeta({ layout: 'dashboard' })

/* ---------- state ---------- */
const tokens = ref<TokenModel[]>([])
const loading = ref(false)

const q = ref('')
const page = ref(1)
const pageSize = 9

const filterActive = ref<'all' | 'active' | 'inactive'>('all')
const sortBy = ref<'recent' | 'name' | 'expires' | 'usage'>('recent')

const toast = useToast()
const tokenStore = useTokenStore()

/* ---------- helpers ---------- */
function normalizeTokens(data: any[]): TokenModel[] {
  return (data || []).map((t: any) => ({
    ...t,
    expiredAt: t?.expiredAt ? new Date(t.expiredAt) : new Date()
  }))
}

function pctUsed(t: TokenModel) {
  const q = Math.max(Number(t.quota ?? 0), 1)
  const u = Math.max(Number(t.usageCount ?? 0), 0)
  return Math.min(Math.round((u / q) * 100), 100)
}

function relativeTime(date: Date) {
  try {
    const diff = date.getTime() - Date.now()
    const abs = Math.abs(diff)
    const rtf = new Intl.RelativeTimeFormat(undefined, { numeric: 'auto' })
    const mins = Math.round(abs / 60000)
    if (mins < 60) return rtf.format(Math.sign(diff) * mins, 'minute')
    const hrs = Math.round(abs / 3600000)
    if (hrs < 24) return rtf.format(Math.sign(diff) * hrs, 'hour')
    const days = Math.round(abs / 86400000)
    return rtf.format(Math.sign(diff) * days, 'day')
  } catch {
    return date.toLocaleDateString()
  }
}

/* ---------- fetch ---------- */
async function getData() {
  loading.value = true
  try {
    const res = (await tokenStore.getToken()) as BaseResponse<TokenModel[]> | null
    tokens.value = Array.isArray(res?.data) ? normalizeTokens(res!.data!) : []
  } catch (e: unknown) {
    const msg = e instanceof Error ? e.message : 'Gagal memuat data'
    toast.add({ title: 'Error', description: msg, color: 'error' })
    tokens.value = []
  } finally {
    loading.value = false
  }
}
onMounted(getData)

/* ---------- search (debounced) ---------- */
const qDebounced = ref('')
let qTimer: number | null = null
watch(q, (val) => {
  if (qTimer) clearTimeout(qTimer)
  // debounce 250ms
  qTimer = window.setTimeout(() => {
    qDebounced.value = val
    page.value = 1
  }, 250)
})

/* ---------- filters & sorts ---------- */
const filtered = computed(() => {
  const query = qDebounced.value.toLowerCase().trim()
  let list = tokens.value.slice()

  // filter active
  if (filterActive.value !== 'all') {
    list = list.filter(t => filterActive.value === 'active' ? t.isActive : !t.isActive)
  }

  // search
  if (query) {
    list = list.filter(t =>
        (t.name || '').toLowerCase().includes(query) ||
        (t.token || '').toLowerCase().includes(query) ||
        (t.note || '').toLowerCase().includes(query) ||
        (t.id || '').toLowerCase().includes(query)
    )
  }

  // sort
  list.sort((a, b) => {
    switch (sortBy.value) {
      case 'name':
        return (a.name || '').localeCompare(b.name || '')
      case 'expires':
        return new Date(a.expiredAt as any).getTime() - new Date(b.expiredAt as any).getTime()
      case 'usage':
        return pctUsed(b) - pctUsed(a)
      case 'recent':
      default:
        // kalau ada updatedAt/createdAt boleh pakai; fallback expires desc
        return new Date(b.expiredAt as any).getTime() - new Date(a.expiredAt as any).getTime()
    }
  })

  return list
})

const paginated = computed(() => {
  const start = (page.value - 1) * pageSize
  return filtered.value.slice(start, start + pageSize)
})

/* ---------- header stats ---------- */
const stats = computed(() => {
  const total = tokens.value.length
  const active = tokens.value.filter(t => t.isActive).length
  const totalQuota = tokens.value.reduce((s, t) => s + Number(t.quota ?? 0), 0)
  const totalUsed = tokens.value.reduce((s, t) => s + Number(t.usageCount ?? 0), 0)
  const avgUsed = totalQuota ? Math.round((totalUsed / Math.max(totalQuota, 1)) * 100) : 0
  return { total, active, totalQuota, totalUsed, avgUsed }
})

/* ---------- actions ---------- */
async function copyToken(token: string) {
  try {
    await navigator.clipboard.writeText(token)
    toast.add({ title: 'Copied', description: 'Token disalin ke clipboard.' })
  } catch {
    toast.add({ title: 'Gagal menyalin', color: 'error' })
  }
}

async function remove(id?: string) {
  if (!id) return
  const ok = confirm('Delete this token?')
  if (!ok) return
  try {
    await tokenStore.deleteToken(id)
    tokens.value = tokens.value.filter(t => t.id !== id)
    toast.add({ title: 'Deleted', description: `Token ${id} removed` })
    if (!paginated.value.length && page.value > 1) page.value -= 1
  } catch (e: unknown) {
    const msg = e instanceof Error ? e.message : 'Gagal menghapus token'
    toast.add({ title: 'Error', description: msg, color: 'error' })
  }
}
</script>

<template>
  <div class="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8">
    <!-- Heading -->
    <div class="mb-8 flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
      <div>
        <h1 class="text-3xl font-extrabold tracking-tight text-slate-900 dark:text-white">
          API Tokens
        </h1>
        <p class="text-slate-600 dark:text-slate-400">Kelola akses dan kuota API dengan aman.</p>
      </div>

      <!-- Quick actions -->
      <div class="flex flex-wrap items-center gap-2">
        <UButton to="/dashboard/token/create" icon="i-heroicons-plus" size="md" color="primary">
          Add Token
        </UButton>
        <UButton variant="ghost" icon="i-heroicons-arrow-path" @click="getData">
          Reload
        </UButton>
      </div>
    </div>

    <!-- Stats -->
    <div class="mb-6 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
      <UCard class="bg-gradient-to-br from-indigo-500/10 to-transparent border-indigo-500/30">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-xs uppercase text-slate-500">Total Tokens</p>
            <p class="mt-1 text-2xl font-bold text-slate-900 dark:text-white">{{ stats.total }}</p>
          </div>
          <UIcon name="i-heroicons-key" class="h-8 w-8 text-indigo-500" />
        </div>
      </UCard>

      <UCard class="bg-gradient-to-br from-emerald-500/10 to-transparent border-emerald-500/30">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-xs uppercase text-slate-500">Active</p>
            <p class="mt-1 text-2xl font-bold text-slate-900 dark:text-white">{{ stats.active }}</p>
          </div>
          <UIcon name="i-heroicons-power" class="h-8 w-8 text-emerald-500" />
        </div>
      </UCard>

      <UCard class="bg-gradient-to-br from-sky-500/10 to-transparent border-sky-500/30">
        <div>
          <p class="text-xs uppercase text-slate-500">Usage</p>
          <div class="mt-1 flex items-end gap-2">
            <p class="text-2xl font-bold text-slate-900 dark:text-white">{{ stats.totalUsed }}</p>
            <p class="text-sm text-slate-500">/ {{ stats.totalQuota }}</p>
          </div>
          <div class="mt-3 h-2 w-full rounded-full bg-slate-200 dark:bg-slate-700">
            <div class="h-2 rounded-full bg-sky-500" :style="{ width: stats.avgUsed + '%' }" />
          </div>
          <p class="mt-1 text-xs text-slate-500">{{ stats.avgUsed }}% avg used</p>
        </div>
      </UCard>

      <UCard class="bg-gradient-to-br from-amber-500/10 to-transparent border-amber-500/30">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-xs uppercase text-slate-500">Next Expiry</p>
            <p class="mt-1 text-lg font-semibold text-slate-900 dark:text-white">
              {{
                tokens.length
                    ? new Date([...tokens].sort((a,b)=>new Date(a.expiredAt as any).getTime()-new Date(b.expiredAt as any).getTime())[0].expiredAt as any).toLocaleDateString()
                    : '—'
              }}
            </p>
          </div>
          <UIcon name="i-heroicons-clock" class="h-8 w-8 text-amber-500" />
        </div>
      </UCard>
    </div>

    <!-- Controls -->
    <div class="mb-6 flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
      <div class="flex flex-1 items-center gap-2">
        <UInput
            v-model="q"
            placeholder="Search by name, token, note, or id…"
            icon="i-heroicons-magnifying-glass"
            class="w-full md:max-w-md"
        />
      </div>

      <div class="text-xs text-slate-500 dark:text-slate-400">
        Showing <span class="font-medium">{{ paginated.length }}</span> of <span class="font-medium">{{ filtered.length }}</span> results
      </div>
    </div>

    <!-- Loading skeleton -->
    <div v-if="loading" class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
      <UCard v-for="i in 6" :key="i" class="p-4">
        <USkeleton class="mb-2 h-5 w-2/3" />
        <USkeleton class="mb-4 h-4 w-1/2" />
        <USkeleton class="mb-2 h-2 w-full rounded" />
        <USkeleton class="mb-2 h-2 w-5/6 rounded" />
        <div class="mt-4 flex justify-end gap-2">
          <USkeleton class="h-8 w-20 rounded" />
          <USkeleton class="h-8 w-8 rounded" />
        </div>
      </UCard>
    </div>

    <!-- Grid -->
    <div v-else class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
      <template v-if="paginated.length">
        <UCard
            v-for="t in paginated"
            :key="t.id || t.token"
            class="group relative overflow-hidden border border-slate-200/60 bg-white/70 backdrop-blur dark:border-slate-700/60 dark:bg-slate-800/60"
        >
          <!-- glow -->
          <div class="pointer-events-none absolute inset-0 -z-10 opacity-0 blur-2xl transition-opacity duration-300 group-hover:opacity-100" aria-hidden="true">
            <div class="absolute -inset-8 bg-gradient-to-br from-indigo-500/20 via-fuchsia-500/10 to-emerald-500/20" />
          </div>

          <div class="space-y-3">
            <!-- Name + Token -->
            <div>
              <div class="flex items-center gap-2">
                <UIcon name="i-heroicons-key" class="h-5 w-5 text-indigo-500" />
                <h2 class="text-lg font-extrabold text-slate-900 dark:text-white">{{ t.name || '—' }}</h2>
              </div>
              <div class="mt-1 flex items-center gap-2">
                <p class="break-all font-mono text-xs font-semibold text-slate-800 dark:text-slate-200">
                  {{ (t.token || '').slice(0, 24) }}…
                </p>
                <UTooltip text="Copy token">
                  <UButton color="gray" variant="ghost" size="2xs" icon="i-heroicons-clipboard" @click="copyToken(t.token || '')" />
                </UTooltip>
              </div>
              <p class="mt-1 line-clamp-2 text-xs text-slate-500 dark:text-slate-400">{{ t.note }}</p>
            </div>

            <!-- Progress -->
            <div>
              <div class="mb-1 flex justify-between text-xs text-slate-600 dark:text-slate-400">
                <span>Used: {{ t.usageCount ?? 0 }}</span>
                <span>Quota: {{ t.quota ?? 0 }}</span>
              </div>
              <div class="h-2 w-full rounded-full bg-slate-200/80 dark:bg-slate-700/80">
                <div
                    class="h-2 rounded-full transition-all"
                    :class="[
                    pctUsed(t) >= 90 ? 'bg-rose-500' : pctUsed(t) >= 70 ? 'bg-amber-500' : 'bg-emerald-500'
                  ]"
                    :style="{ width: pctUsed(t) + '%' }"
                />
              </div>
              <div class="mt-1 text-right text-[11px] text-slate-500">
                {{ pctUsed(t) }}%
              </div>
            </div>

            <!-- Badges -->
            <div class="flex items-center justify-between">
              <UBadge :color="t.isActive ? 'primary' : 'error'" variant="soft" size="xs">
                {{ t.isActive ? 'Active' : 'Inactive' }}
              </UBadge>

              <div class="flex items-center gap-2 text-xs text-slate-500">
                <UIcon name="i-heroicons-calendar" class="h-4 w-4" />
                <span>Exp: {{ new Date(t.expiredAt as any).toLocaleDateString() }}</span>
                <span class="text-slate-400">•</span>
                <span>{{ relativeTime(new Date(t.expiredAt as any)) }}</span>
              </div>
            </div>
          </div>

          <!-- Actions -->
          <div class="mt-4 flex justify-end gap-2">
            <UButton
                :to="`/dashboard/token/${t.id}`"
                icon="i-heroicons-pencil-square"
                color="gray"
                variant="ghost"
                size="xs"
            >
              Edit
            </UButton>
            <UTooltip text="Delete token">
              <UButton
                  icon="i-heroicons-trash"
                  color="error"
                  variant="ghost"
                  size="xs"
                  @click="remove(t.id)"
              />
            </UTooltip>
          </div>
        </UCard>
      </template>

      <!-- Empty state -->
      <UCard v-else class="py-12 text-center border-dashed">
        <div class="flex flex-col items-center gap-3">
          <div class="relative">
            <div class="absolute -inset-3 rounded-full bg-indigo-500/10 blur-xl" />
            <UIcon name="i-heroicons-key" class="relative h-10 w-10 text-indigo-500" />
          </div>
          <p class="text-slate-700 dark:text-slate-300 font-medium">No tokens found</p>
          <p class="text-sm text-slate-500 dark:text-slate-400">Coba buat token baru atau ubah filter pencarian.</p>
          <div class="mt-2 flex items-center gap-2">
            <UButton to="/dashboard/token/create" icon="i-heroicons-plus">Create Token</UButton>
            <UButton variant="ghost" icon="i-heroicons-arrow-path" @click="getData">Reload</UButton>
          </div>
        </div>
      </UCard>
    </div>

    <!-- Pagination -->
    <div class="mt-8 flex items-center justify-between">
      <div class="text-xs text-slate-500 dark:text-slate-400">
        Page {{ page }} • {{ filtered.length }} results
      </div>
      <UPagination
          v-model:page="page"
          :items-per-page="pageSize"
          :total="filtered.length"
      />
    </div>
  </div>
</template>

<style scoped>
/* subtle card hover lift */
:deep(.u-card) { transition: transform .25s ease, box-shadow .25s ease; }
:deep(.u-card:hover) { transform: translateY(-2px); }
/* clamp utility (for notes) if Nuxt UI not providing */
.line-clamp-2{
  display:-webkit-box; -webkit-line-clamp:2; -webkit-box-orient:vertical; overflow:hidden;
}
</style>
