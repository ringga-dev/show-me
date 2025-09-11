<script setup lang="ts">
import { reactive, ref, computed, watch, onMounted, onBeforeUnmount } from 'vue'
import { addHours, addDays, isAfter } from 'date-fns'
import { useHead, useToast, definePageMeta, navigateTo } from '#imports'
import { useRoute } from 'vue-router'
import type { TokenModel } from '~/types/models/TokenModel'
import type { BaseResponse } from '~/types/models/BaseResponse'
import { useTokenStore } from '~/stores/token'

definePageMeta({ layout: 'dashboard' })

const toast = useToast()
const route = useRoute()
const tokenStore = useTokenStore()
const id = String(route.params.id || '')

/* -------- helpers: datetime-local <-> Date -------- */
function toInputValue(d: Date): string {
  const pad = (n: number) => String(n).padStart(2, '0')
  const y = d.getFullYear()
  const m = pad(d.getMonth() + 1)
  const day = pad(d.getDate())
  const h = pad(d.getHours())
  const min = pad(d.getMinutes())
  return `${y}-${m}-${day}T${h}:${min}`
}
function fromInputValue(v: string): Date {
  const d = new Date(v)
  return isNaN(d.getTime()) ? new Date() : d
}

/* -------- form state -------- */
const loadingPage = ref(true)
const saving = ref(false)
const initialSnapshot = ref<string>('')

const tokenForm = reactive<TokenModel>({
  id: undefined,
  token: '',
  name: '',
  quota: 0,
  usageCount: 0,
  expiredAt: new Date(), // FE pakai Date; kirim ISO saat submit
  isActive: true,
  note: ''
})

const expiredAtInput = ref<string>('')

/* -------- derived -------- */
const leftQuota = computed(() =>
    Math.max(0, (tokenForm.quota ?? 0) - (tokenForm.usageCount ?? 0))
)

watch(
    () => tokenForm.usageCount,
    (v) => {
      if (v == null) tokenForm.usageCount = 0
      if ((tokenForm.usageCount ?? 0) < 0) tokenForm.usageCount = 0
      if ((tokenForm.quota ?? 0) < (tokenForm.usageCount ?? 0)) {
        tokenForm.usageCount = tokenForm.quota ?? 0
      }
    }
)

// sync 2-arah Date <-> input string
watch(expiredAtInput, (v) => { tokenForm.expiredAt = fromInputValue(v) })
watch(() => tokenForm.expiredAt, (d) => {
  expiredAtInput.value = toInputValue(d as unknown as Date)
})

/* -------- quick expiry -------- */
const quickExpiry = ref('')
function applyQuickExpiry() {
  const now = new Date()
  let next: Date | null = null
  switch (quickExpiry.value) {
    case '1h': next = addHours(now, 1); break
    case '1d': next = addDays(now, 1); break
    case '7d': next = addDays(now, 7); break
    case '30d': next = addDays(now, 30); break
    case 'never': next = new Date(3000, 11, 31, 23, 59); break
  }
  if (next) expiredAtInput.value = toInputValue(next)
}

/* -------- actions -------- */
async function copyToken() {
  try {
    await navigator.clipboard.writeText(tokenForm?.token ?? "")
    toast.add({ title: 'Disalin', description: 'Token disalin ke clipboard.' })
  } catch {
    toast.add({ title: 'Gagal menyalin', color: 'error' })
  }
}


/* -------- validation -------- */
function validate(): string | null {
  if (!tokenForm.name?.trim()) return 'Nama wajib diisi.'
  if (!tokenForm.token || tokenForm.token.length < 16) return 'Token minimal 16 karakter.'
  if (tokenForm.quota == null || tokenForm.quota < 0) return 'Quota tidak boleh negatif.'
  if (tokenForm.usageCount == null || tokenForm.usageCount < 0) return 'Usage tidak boleh negatif.'
  if ((tokenForm.usageCount ?? 0) > (tokenForm.quota ?? 0)) return 'Usage tidak boleh melebihi quota.'
  const NEVER_YEAR = 2999
  const exp = tokenForm.expiredAt as unknown as Date
  if (exp.getFullYear() <= NEVER_YEAR && !isAfter(exp, new Date())) {
    return 'Tanggal kedaluwarsa harus di masa depan.'
  }
  return null
}

/* -------- submit -------- */
async function handleSubmit() {
  const err = validate()
  if (err) {
    toast.add({ title: 'Validasi gagal', description: err, color: 'error' })
    return
  }
  try {
    saving.value = true
    // serialize ke JSON string karena store meneruskan body apa adanya
    const payload: TokenModel = {
      ...tokenForm,
      expiredAt: new Date(tokenForm.expiredAt as unknown as Date).toISOString() as unknown as any
    }
    const res = await tokenStore.updateToken(id, JSON.stringify(payload)) as BaseResponse<TokenModel> | null
    const saved = res?.data ?? null
    if (!saved) throw new Error('Gagal menyimpan perubahan.')

    // refresh snapshot dari data tersimpan
    initialSnapshot.value = JSON.stringify({
      ...saved,
      expiredAt: new Date(saved.expiredAt as unknown as string).toISOString()
    })

    toast.add({ title: 'Tersimpan', description: 'Perubahan token berhasil disimpan.' })
    navigateTo('/dashboard/token/list')
  } catch (e: unknown) {
    const msg = e instanceof Error ? e.message : 'Gagal menyimpan perubahan'
    toast.add({ title: 'Error', description: msg, color: 'error' })
  } finally {
    saving.value = false
  }
}

function handleCancel() {
  if (isDirty.value && !confirm('Perubahan belum disimpan. Tinggalkan halaman?')) return
  navigateTo('/dashboard/token/list')
}

/* -------- load data -------- */
onMounted(async () => {
  try {
    if (!id) throw new Error('ID tidak ditemukan di URL.')
    loadingPage.value = true

    const res = await tokenStore.getTokenById(id) as BaseResponse<TokenModel> | null
    const data = res?.data ?? null
    if (!data) throw new Error('Token tidak ditemukan.')

    tokenForm.id = data.id ?? tokenForm.id
    tokenForm.token = data.token ?? ''
    tokenForm.name = data.name ?? ''
    tokenForm.quota = Number(data.quota ?? 0)
    tokenForm.usageCount = Number(data.usageCount ?? 0)
    tokenForm.expiredAt = data.expiredAt ? new Date(data.expiredAt as unknown as string) : new Date()
    tokenForm.isActive = Boolean(data.isActive)
    tokenForm.note = data.note ?? ''
    expiredAtInput.value = toInputValue(tokenForm.expiredAt as unknown as Date)

    // snapshot awal
    initialSnapshot.value = JSON.stringify({
      ...tokenForm,
      expiredAt: new Date(tokenForm.expiredAt as unknown as Date).toISOString()
    })
  } catch (e: unknown) {
    const msg = e instanceof Error ? e.message : 'Gagal memuat data'
    toast.add({ title: 'Error', description: msg, color: 'error' })
    navigateTo('/dashboard/token/list')
  } finally {
    loadingPage.value = false
  }
})

/* -------- unsaved changes guard -------- */
const isDirty = computed(() => {
  const nowSnap = JSON.stringify({
    ...tokenForm,
    expiredAt: new Date(tokenForm.expiredAt as unknown as Date).toISOString()
  })
  return nowSnap !== initialSnapshot.value
})

function beforeUnloadHandler(e: BeforeUnloadEvent) {
  if (isDirty.value) {
    e.preventDefault()
    e.returnValue = ''
  }
}
if (process.client) {
  window.addEventListener('beforeunload', beforeUnloadHandler)
}
onBeforeUnmount(() => {
  if (process.client) {
    window.removeEventListener('beforeunload', beforeUnloadHandler)
  }
})

useHead({
  title: 'Update Token – Dashboard',
  meta: [{ name: 'description', content: 'Update an existing API token.' }]
})
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-primary-50 via-white to-purple-50 dark:from-slate-900 dark:via-slate-800 dark:to-slate-900">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div v-if="loadingPage" class="text-slate-600 dark:text-slate-300">Loading…</div>
      <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-10 items-start">

        <!-- ========== FORM ========== -->
        <div class="lg:col-span-2 space-y-8">
          <div>
            <h1 class="text-3xl font-bold text-slate-900 dark:text-white">Update Token</h1>
            <p class="mt-1 text-slate-600 dark:text-slate-400">Edit details and save your changes.</p>
          </div>

          <form class="bg-white dark:bg-slate-800 rounded-2xl shadow-lg p-6 space-y-6" @submit.prevent="handleSubmit" novalidate>
            <!-- Name -->
            <div>
              <label for="name" class="block text-sm font-medium mb-1">Name <span class="text-error-500">*</span></label>
              <input id="name" v-model.trim="tokenForm.name" required placeholder="My API Key"
                     class="w-full px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-lg bg-white dark:bg-slate-700 focus:ring-2 focus:ring-primary-500" />
            </div>

            <!-- Token -->
            <div>
              <label for="token" class="block text-sm font-medium mb-1">Token <span class="text-error-500">*</span></label>
              <div class="flex gap-2 items-center">
                <input id="token" v-model="tokenForm.token" readonly
                       class="flex-1 px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-lg bg-white dark:bg-slate-700 font-mono" />

                <button type="button" @click="copyToken"
                        class="px-3 py-2 text-sm rounded-md bg-slate-200 dark:bg-slate-700 text-slate-800 dark:text-slate-200 hover:bg-slate-300 dark:hover:bg-slate-600">
                  Copy
                </button>
              </div>
              <p class="text-xs text-slate-500 mt-1">Setelah regenerate, klik <b>Save</b> untuk menyimpan perubahan.</p>
            </div>

            <!-- Quota & Usage -->
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label for="quota" class="block text-sm font-medium mb-1">Quota</label>
                <input id="quota" v-model.number="tokenForm.quota" type="number" min="0" step="1"
                       class="w-full px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-lg bg-white dark:bg-slate-700" />
              </div>
              <div>
                <label for="usage" class="block text-sm font-medium mb-1">Usage</label>
                <input id="usage" v-model.number="tokenForm.usageCount" type="number" min="0" :max="tokenForm.quota ?? 0" step="1"
                       class="w-full px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-lg bg-white dark:bg-slate-700" />
              </div>
            </div>

            <!-- Expiry -->
            <div>
              <label class="block text-sm font-medium mb-1">Expires At</label>
              <div class="flex gap-2 items-center">
                <select v-model="quickExpiry" @change="applyQuickExpiry"
                        class="px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-lg bg-white dark:bg-slate-700">
                  <option disabled value="">Quick pick…</option>
                  <option value="1h">1 hour</option>
                  <option value="1d">1 day</option>
                  <option value="7d">7 days</option>
                  <option value="30d">30 days</option>
                  <option value="never">Never</option>
                </select>
                <input id="expiredAt" v-model="expiredAtInput" type="datetime-local"
                       class="flex-1 px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-lg bg-white dark:bg-slate-700" />
              </div>
            </div>

            <!-- Note -->
            <div>
              <label for="note" class="block text-sm font-medium mb-1">Note</label>
              <textarea id="note" v-model.trim="tokenForm.note" rows="3"
                        class="w-full px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-lg bg-white dark:bg-slate-700 resize-none" />
            </div>

            <!-- Active -->
            <div class="flex items-center gap-3">
              <label for="active" class="text-sm font-medium">Active?</label>
              <USwitch id="active" v-model="tokenForm.isActive" />
            </div>

            <!-- Actions -->
            <div class="flex justify-end gap-4 pt-2">
              <button type="button" @click="handleCancel"
                      class="px-4 py-2 rounded-lg bg-slate-200 dark:bg-slate-700 text-slate-700 dark:text-slate-200 hover:bg-slate-300 dark:hover:bg-slate-600">
                Cancel
              </button>
              <button type="submit" :disabled="saving"
                      class="px-6 py-2 rounded-lg bg-primary-600 text-white hover:bg-primary-700 disabled:opacity-60 disabled:cursor-not-allowed">
                <span v-if="!saving">Save Changes</span>
                <span v-else>Saving…</span>
              </button>
            </div>
          </form>
        </div>

        <!-- ========== PREVIEW CARD ========== -->
        <aside class="lg:col-span-1">
          <div class="sticky top-8">
            <h3 class="text-lg font-semibold text-slate-800 dark:text-white mb-4">Preview</h3>
            <div class="bg-white dark:bg-slate-800 rounded-2xl shadow-lg p-4 space-y-4">
              <div>
                <h4 class="font-bold text-lg text-slate-900 dark:text-white">{{ tokenForm.name || '—' }}</h4>
                <p class="font-mono text-sm text-slate-600 dark:text-slate-400 break-all">
                  {{ (tokenForm.token || '').slice(0, 20) }}…
                </p>
                <p class="text-sm text-slate-500 dark:text-slate-400 mt-1 whitespace-pre-line">{{ tokenForm.note }}</p>
              </div>

              <div class="grid grid-cols-2 gap-2 text-sm">
                <div><span class="font-medium">Quota:</span> {{ tokenForm.quota ?? 0 }}</div>
                <div><span class="font-medium">Used:</span> {{ tokenForm.usageCount ?? 0 }}</div>
                <div><span class="font-medium">Left:</span> {{ leftQuota }}</div>
                <div><span class="font-medium">Expires:</span> {{ new Date(tokenForm.expiredAt as Date).toLocaleString() }}</div>
              </div>

              <div>
                <span :class="tokenForm.isActive ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
                      class="text-xs font-medium px-2.5 py-0.5 rounded-full">
                  {{ tokenForm.isActive ? 'Active' : 'Inactive' }}
                </span>
              </div>
            </div>
          </div>
        </aside>

      </div>
    </div>
  </div>
</template>

<style scoped>
/* supaya icon calendar terlihat di dark mode */
input[type="datetime-local"]::-webkit-calendar-picker-indicator { filter: invert(0.5); }
</style>
