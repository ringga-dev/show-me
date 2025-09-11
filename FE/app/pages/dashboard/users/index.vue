<script setup lang="ts">
import {useUserStore} from '~/stores/user'
import type {UsersModel} from '~/types/models/UserModel'
import {watchDebounced} from '@vueuse/core'
import {useToast} from '#imports'

definePageMeta({layout: 'dashboard'})

const toast = useToast()
const userStore = useUserStore()

/* ---------- state ---------- */
const users = ref<UsersModel[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

/* ---------- search, sort & pagination ---------- */
const q = ref('')
const debouncedQ = ref('')
const page = ref(1)
const pageSize = ref(6)
const pageSizeOptions = [
  {label: '6', value: 6},
  {label: '12', value: 12},
  {label: '18', value: 18},
  {label: '24', value: 24}
]
const sortKey = ref<'fullName' | 'phone'>('fullName')
const sortDir = ref<'asc' | 'desc'>('asc')

watchDebounced(
    q,
    (val) => {
      debouncedQ.value = val
      page.value = 1
    },
    {debounce: 250, maxWait: 500}
)

/* ---------- fetch ---------- */
async function getData() {
  loading.value = true
  error.value = null
  try {
    const res = await userStore.getAllUsers()
    users.value = res?.data ?? []
  } catch (e: unknown) {
    error.value = e instanceof Error ? e.message : 'Gagal memuat data'
  } finally {
    loading.value = false
  }
}

onMounted(getData)

/* ---------- utils ---------- */
const roleColor = (role?: string) => {
  switch ((role || '').toLowerCase()) {
    case 'admin':
      return 'primary'
    case 'manager':
      return 'warning'
    case 'staff':
      return 'emerald'
    default:
      return 'gray'
  }
}
const safeLower = (v?: string | number | null) => (v ?? '').toString().toLowerCase()

const filtered = computed(() => {
  if (!debouncedQ.value) return users.value
  const query = debouncedQ.value.toLowerCase()
  return users.value.filter(u =>
      safeLower(u.fullName).includes(query) ||
      safeLower(u.phone).includes(query) ||
      safeLower(u.email).includes(query)
  )
})

const sorted = computed(() => {
  const arr = [...filtered.value]
  const key = sortKey.value
  const dir = sortDir.value === 'asc' ? 1 : -1
  arr.sort((a, b) => {
    const va = safeLower((a as Record<string, unknown>)[key] as string)
    const vb = safeLower((b as Record<string, unknown>)[key] as string)
    if (va < vb) return -1 * dir
    if (va > vb) return 1 * dir
    return 0
  })
  return arr
})

const paginated = computed(() => {
  const start = (page.value - 1) * pageSize.value
  return sorted.value.slice(start, start + pageSize.value)
})

const total = computed(() => filtered.value.length)
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))
const showingRange = computed(() => {
  if (!total.value) return '0–0'
  const start = (page.value - 1) * pageSize.value + 1
  const end = Math.min(page.value * pageSize.value, total.value)
  return `${start}–${end}`
})

watch([sortKey, sortDir, pageSize], () => (page.value = 1))

/* ---------- delete (Modal sesuai pedoman Nuxt UI) ---------- */
const confirmOpen = ref(false)               // default HARUS false
const targetUser = ref<UsersModel | null>(null)

function askDelete(u: UsersModel) {
  targetUser.value = u
  confirmOpen.value = true
}

function closeConfirm() {
  confirmOpen.value = false
  targetUser.value = null
}

async function performDelete() {
  if (!targetUser.value?.userId) return closeConfirm()
  loading.value = true
  error.value = null
  try {
    const res = await userStore.deleteUser(targetUser.value.userId)
    const message =
        (Array.isArray(res?.message) ? res?.message[0] : res?.message) ?? 'User berhasil dihapus'
    toast.add({title: 'Berhasil', description: message})
  } catch (e: unknown) {
    const msg = e instanceof Error ? e.message : 'Gagal menghapus user'
    error.value = msg
    toast.add({title: 'Gagal', description: msg, color: 'error'})
  } finally {
    loading.value = false
    closeConfirm()
    await getData()
  }
}
</script>

<template>
  <div class="space-y-5">
    <!-- Header -->
    <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
      <div>
        <h1 class="text-2xl font-semibold text-slate-800 dark:text-white">Users</h1>
        <p class="text-sm text-slate-500 dark:text-slate-400">Kelola daftar pengguna aplikasi Anda.</p>
      </div>

      <div class="flex flex-wrap items-center gap-2">
        <USelect
            v-model="sortKey"
            :options="[
            { label: 'Nama', value: 'fullName' },
            { label: 'Telepon', value: 'phone' }
          ]"
            class="w-40"
            icon="i-heroicons-list-bullet"
            aria-label="Urutkan berdasarkan"
        />
        <UButton
            :icon="sortDir === 'asc' ? 'i-heroicons-arrow-up-20-solid' : 'i-heroicons-arrow-down-20-solid'"
            variant="soft"
            @click="sortDir = sortDir === 'asc' ? 'desc' : 'asc'"
            aria-label="Toggle urutan naik/turun"
        />
        <USelect
            v-model="pageSize"
            :options="pageSizeOptions"
            class="w-24"
            icon="i-heroicons-squares-2x2"
            aria-label="Jumlah item per halaman"
        />
        <UButton variant="soft" icon="i-heroicons-arrow-path" :loading="loading" @click="getData">
          Refresh
        </UButton>
        <UButton to="/dashboard/users/create" icon="i-heroicons-plus" as="nuxt-link">
          Add User
        </UButton>
      </div>
    </div>

    <!-- Filters -->
    <div class="flex flex-col gap-3 sm:flex-row sm:items-center">
      <UInput
          v-model="q"
          placeholder="Cari nama, email, atau telepon…"
          icon="i-heroicons-magnifying-glass"
          :trailing="!!q"
          class="w-full sm:max-w-md"
          aria-label="Cari user"
      >
        <template v-if="q" #trailing>
          <UButton
              variant="ghost"
              icon="i-heroicons-x-mark-20-solid"
              aria-label="Bersihkan pencarian"
              @click="q = ''"
          />
        </template>
      </UInput>

      <div v-if="!loading" class="text-sm text-slate-500 dark:text-slate-400">
        <span v-if="total">Menampilkan {{ showingRange }} dari {{ total }} hasil</span>
        <span v-else>Tidak ada hasil</span>
      </div>
    </div>

    <!-- Error -->
    <UAlert
        v-if="error"
        icon="i-heroicons-exclamation-triangle"
        color="error"
        variant="soft"
        title="Terjadi kesalahan"
        :description="error"
        class="border border-red-200/50 dark:border-red-900/40"
    />

    <!-- Content -->
    <div>
      <!-- Loading skeletons -->
      <div v-if="loading" class="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3">
        <UCard v-for="i in 6" :key="i" class="p-4">
          <div class="mb-3 flex items-center gap-3">
            <USkeleton class="h-12 w-12 rounded-full"/>
            <div class="flex-1 space-y-2">
              <USkeleton class="h-4 w-2/3"/>
              <USkeleton class="h-3 w-1/3"/>
            </div>
          </div>
          <div class="flex items-center justify-between">
            <USkeleton class="h-6 w-16 rounded-full"/>
            <div class="flex gap-2">
              <USkeleton class="h-8 w-8 rounded-lg"/>
              <USkeleton class="h-8 w-8 rounded-lg"/>
            </div>
          </div>
        </UCard>
      </div>

      <!-- Grid cards -->
      <div v-else-if="paginated.length" class="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3">
        <UCard v-for="u in paginated" :key="u.userId ?? u.email ?? u.fullName ?? ''" class="p-4">
          <div class="mb-3 flex items-center gap-3">
            <UAvatar
                size="lg"
                :src="u.profilePictureUrl || undefined"
                :alt="u.fullName || 'User avatar'"
                class="rounded-full"
            >
              <template #fallback>
                <span class="text-sm">
                  {{ (u.fullName || 'U').split(' ').map(p => p[0]).slice(0, 2).join('').toUpperCase() }}
                </span>
              </template>
            </UAvatar>

            <div class="min-w-0">
              <div class="truncate font-medium text-slate-800 dark:text-white">
                {{ u.fullName || '—' }}
              </div>
              <div class="text-xs text-slate-500 dark:text-slate-400">
                {{ u.email || '—' }}
              </div>
            </div>
          </div>

          <div class="space-y-1 text-sm text-slate-600 dark:text-slate-300">
            <div class="flex items-center gap-2">
              <UIcon name="i-heroicons-phone" class="shrink-0"/>
              <span class="truncate">{{ u.phone || '—' }}</span>
            </div>
            <div class="flex items-center gap-2">
              <UIcon name="i-heroicons-map-pin" class="shrink-0"/>
              <span class="truncate">{{ u.address || '—' }}</span>
            </div>
          </div>

          <div class="mt-4 flex items-center justify-between">
            <div class="flex flex-wrap gap-2">
              <UBadge
                  v-for="(role, index) in (Array.isArray(u.role) ? u.role : [u.role || 'User'])"
                  :key="index"
                  :color="roleColor(role)"
                  variant="subtle"
              >
                {{ role || 'User' }}
              </UBadge>
            </div>

            <div class="flex gap-1">
              <UButton
                  icon="i-heroicons-pencil-square"
                  size="xs"
                  variant="soft"
                  aria-label="Edit user"
                  :to="`/dashboard/users/${u.userId}`"
                  as="nuxt-link"
              />

              <UModal v-model:open="confirmOpen">
                <UButton
                    icon="i-heroicons-trash"
                    size="xs"
                    color="error"
                    variant="soft"
                    aria-label="Hapus user"
                    @click="askDelete(u)"
                />

                <template #content>
                  <UCard>
                    <template #header>
                      <div class="flex items-center gap-2">
                        <UIcon name="i-heroicons-trash"/>
                        <h3 class="text-base font-semibold">Hapus User</h3>
                      </div>
                    </template>

                    <p class="text-sm text-slate-600 dark:text-slate-300">
                      Anda yakin ingin menghapus
                      <span class="font-medium">{{ targetUser?.fullName ?? 'user ini' }}</span>?
                      Tindakan ini tidak bisa dibatalkan.
                    </p>

                    <template #footer>
                      <div class="flex justify-end gap-2">
                        <UButton variant="ghost" @click="closeConfirm()">Batal</UButton>
                        <UButton color="error" :loading="loading" icon="i-heroicons-trash" @click="performDelete">
                          Hapus
                        </UButton>
                      </div>
                    </template>
                  </UCard>
                </template>
              </UModal>


            </div>
          </div>
        </UCard>
      </div>

      <!-- Empty state -->
      <UCard v-else class="py-12 text-center">
        <div class="flex flex-col items-center gap-2">
          <UIcon name="i-heroicons-user-group" class="h-8 w-8 text-slate-400"/>
          <p class="text-slate-500 dark:text-slate-400">Tidak ada user yang cocok.</p>
          <div class="mt-2 flex items-center gap-2">
            <UButton to="/dashboard/users/create" as="nuxt-link" icon="i-heroicons-plus">Tambah User</UButton>
            <UButton variant="ghost" icon="i-heroicons-arrow-path" @click="q = ''">Reset Pencarian</UButton>
          </div>
        </div>
      </UCard>
    </div>

    <!-- Pagination -->
    <div class="flex flex-col gap-3 pt-2 sm:flex-row sm:items-center sm:justify-between">
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
