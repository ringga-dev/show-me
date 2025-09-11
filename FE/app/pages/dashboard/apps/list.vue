<script setup lang="ts">
import type { AppModel } from '~/types/models/AppModel'
import { useAppsStore } from '~/stores/apps'

definePageMeta({
  layout: 'dashboard'
})

const apps = useAppsStore()
const toast = useToast()

/* ------------- State ------------- */
const appList = ref<AppModel[]>([])
const loading = ref(false)

/* ------------- Search & Pagination ------------- */
const page = ref(1)
const pageSize = 9
const q = ref('')

const filtered = computed(() => {
  const query = q.value.trim().toLowerCase()
  if (!query) return appList.value
  return appList.value.filter(p => (p.name ?? '').toLowerCase().includes(query))
})

const totalPages = computed(() =>
    Math.max(1, Math.ceil(filtered.value.length / pageSize))
)

const paginated = computed(() => {
  // pastikan page valid saat data/filters berubah
  const current = Math.min(page.value, totalPages.value)
  const start = (current - 1) * pageSize
  return filtered.value.slice(start, start + pageSize)
})

// reset ke halaman 1 saat search berubah
watch(q, () => { page.value = 1 })

/* ------------- Fetch ------------- */
async function getData() {
  loading.value = true
  try {
    const res = await apps.getApps()
    appList.value = Array.isArray(res?.data) ? res.data : []
  } catch (e: unknown) {
    const msg = e instanceof Error ? e.message : 'Gagal memuat data'
    toast.add({ title: 'Error', description: msg, color: 'error' })
  } finally {
    loading.value = false
  }
}

onMounted(getData)

/* ------------- Actions ------------- */
async function remove(id: string) {
  if (!id) return
  if (!confirm('Delete this app?')) return

  // Optimistic update (sesuaikan jika punya API delete)
  const res = await apps.deleteApp(id)
  if (res?.status?.toUpperCase?.() === 'SUCCESS') {
    getData()
    toast.add({ title: 'Deleted', description: 'App removed', color: 'success' })
  } else {
    toast.add({ title: 'Deleted', description: 'App removed failed', color: 'warning' })
  }


}
</script>

<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-3xl font-bold text-slate-900 dark:text-white">Apps</h1>
      <UButton to="/dashboard/apps/create" icon="i-heroicons-plus" color="success" size="md">
        Add App
      </UButton>
    </div>

    <!-- Search -->
    <div class="w-full flex justify-end mb-4">
      <UInput
          v-model="q"
          placeholder="Search apps..."
          icon="i-heroicons-magnifying-glass"
          class="rounded border-slate-300 dark:border-slate-600 text-primary-600 focus:ring-primary-500"
      />
    </div>

    <!-- Loading state -->
    <div v-if="loading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
      <UCard v-for="i in 6" :key="i" class="animate-pulse">
        <div class="flex items-start space-x-4">
          <div class="w-14 h-14 rounded-xl bg-slate-200 dark:bg-slate-700" />
          <div class="flex-1 space-y-2">
            <div class="h-4 w-1/2 bg-slate-200 dark:bg-slate-700 rounded" />
            <div class="h-3 w-3/4 bg-slate-200 dark:bg-slate-700 rounded" />
            <div class="h-3 w-1/3 bg-slate-200 dark:bg-slate-700 rounded" />
          </div>
        </div>
        <div class="flex justify-end space-x-2 mt-4">
          <div class="h-7 w-16 bg-slate-200 dark:bg-slate-700 rounded" />
          <div class="h-7 w-16 bg-slate-200 dark:bg-slate-700 rounded" />
        </div>
      </UCard>
    </div>

    <!-- Empty state -->
    <div v-else-if="!filtered.length" class="text-center py-16">
      <p class="text-slate-600 dark:text-slate-300">Tidak ada app yang cocok.</p>
    </div>

    <!-- Grid -->
    <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
      <UCard
          v-for="app in paginated"
          :key="app.id"
          class="hover:shadow-indigo-500/40 transition-all duration-300"
      >
        <div class="flex items-start space-x-4">
          <img
              :src="app.image || '/placeholder.svg'"
              alt="app icon"
              class="w-14 h-14 rounded-xl flex-shrink-0 object-cover"
              loading="lazy"
          />
          <div class="flex-1">
            <h3 class="text-lg font-semibold text-slate-800 dark:text-white">
              {{ app.name }}
            </h3>
            <p class="text-sm text-slate-500 dark:text-slate-400 mt-1 line-clamp-2">
              {{ app.description }}
            </p>
            <UBadge :color="app.isActive ? 'primary' : 'warning'" variant="subtle" size="xs" class="mt-2">
              {{ app.isActive ? 'Active' : 'Inactive' }}
            </UBadge>
          </div>
        </div>

        <div class="flex justify-end space-x-2 mt-4">
          <UButton
              :to="`/dashboard/apps/${app.id}`"
              icon="i-heroicons-pencil-square"
              color="gray"
              variant="ghost"
              size="xs"
          >
            Edit
          </UButton>
          <UButton
              icon="i-heroicons-trash"
              color="error"
              variant="ghost"
              size="xs"
              @click="remove(app.id || '')"
          >
            Delete
          </UButton>
        </div>
      </UCard>
    </div>

    <!-- Pagination -->
    <div class="flex justify-end mt-8" v-if="filtered.length">
      <UPagination
          v-model:page="page"
          :items-per-page="pageSize"
          :total="filtered.length"
      />
    </div>
  </div>
</template>
