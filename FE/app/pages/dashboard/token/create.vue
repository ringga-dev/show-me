<script setup lang="ts">
import { reactive, ref, computed, watch } from 'vue'
import { nanoid } from 'nanoid'
import { addHours, addDays, isAfter } from 'date-fns'
import { useHead, useToast, navigateTo, definePageMeta } from '#imports'
import type { TokenModel } from '~/types/models/TokenModel'
import { useTokenStore } from '~/stores/token'

definePageMeta({ layout: 'dashboard' })

const toast = useToast()
const tokenStore = useTokenStore()

/** ---- helpers: datetime-local <-> Date ---- */
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

/** ---- form state ---- */
const initialExpiry = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000) // +7 hari
const tokenForm = reactive<TokenModel>({
  token: nanoid(32),
  name: '',
  quota: 1000,
  usageCount: 0,
  expiredAt: initialExpiry,
  isActive: true,
  note: ''
})

// pegangan string untuk <input type="datetime-local">
const expiredAtInput = ref<string>(toInputValue(tokenForm.expiredAt as Date))

watch(expiredAtInput, (v) => { tokenForm.expiredAt = fromInputValue(v) })
watch(() => tokenForm.expiredAt, (d) => { expiredAtInput.value = toInputValue(d as Date) })

/** ---- regenerate & copy ---- */
function regenerateToken() {
  tokenForm.token = nanoid(32)
}
async function copyToken() {
  try {
    await navigator.clipboard.writeText(tokenForm?.token ??"")
    toast.add({ title: 'Disalin', description: 'Token disalin ke clipboard.' })
  } catch {
    toast.add({ title: 'Gagal menyalin', color: 'error' })
  }
}

/** ---- quick expiry ---- */
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

/** ---- derived ---- */
const leftQuota = computed(() => Math.max(0, (tokenForm.quota ?? 0) - (tokenForm.usageCount ?? 0)))

watch(
    () => tokenForm.usageCount,
    (v) => {
      if (v == null) tokenForm.usageCount = 0
      if ((tokenForm.quota ?? 0) < (tokenForm.usageCount ?? 0)) {
        tokenForm.usageCount = tokenForm.quota ?? 0
      }
      if ((tokenForm.usageCount ?? 0) < 0) tokenForm.usageCount = 0
    }
)

/** ---- validation ---- */
function validate(): string | null {
  if (!tokenForm.name?.trim()) return 'Nama wajib diisi.'
  if (!tokenForm.token || tokenForm.token.length < 16) return 'Token minimal 16 karakter.'
  if (tokenForm.quota == null || tokenForm.quota < 0) return 'Quota tidak boleh negatif.'
  if (tokenForm.usageCount == null || tokenForm.usageCount < 0) return 'Usage tidak boleh negatif.'
  if ((tokenForm.usageCount ?? 0) > (tokenForm.quota ?? 0)) return 'Usage tidak boleh melebihi quota.'
  // expiry boleh "never" (tahun 3000). Kalau bukan "never", harus setelah sekarang.
  const NEVER_YEAR = 2999
  const exp = tokenForm.expiredAt as Date
  if (exp.getFullYear() <= NEVER_YEAR && !isAfter(exp, new Date())) {
    return 'Tanggal kedaluwarsa harus di masa depan.'
  }
  return null
}

/** ---- submit ---- */
const loading = ref(false)
async function handleSubmit() {
  const err = validate()
  if (err) {
    toast.add({ title: 'Validasi gagal', description: err, color: 'error' })
    return
  }
  try {
    loading.value = true
    const payload: TokenModel = {
      ...tokenForm,
      // kirim ISO string agar backend konsisten
      expiredAt: new Date(tokenForm.expiredAt as Date).toISOString() as unknown as any
    }
    await tokenStore.addToken(payload)
    toast.add({ title: 'Berhasil', description: 'Token berhasil dibuat.' })
    navigateTo('/dashboard/token/list')
  } catch (e: unknown) {
    const msg = e instanceof Error ? e.message : 'Gagal membuat token'
    toast.add({ title: 'Error', description: msg, color: 'error' })
  } finally {
    loading.value = false
  }
}

function handleCancel() {
  navigateTo('/dashboard/token/list')
}

useHead({
  title: 'Create Token – Dashboard',
  meta: [{ name: 'description', content: 'Generate a new secure token.' }]
})
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-primary-50 via-white to-purple-50 dark:from-slate-900 dark:via-slate-800 dark:to-slate-900">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-10 items-start">

        <!-- ========== FORM ========== -->
        <div class="lg:col-span-2 space-y-8">
          <div>
            <h1 class="text-3xl font-bold text-slate-900 dark:text-white">Create New Token</h1>
            <p class="mt-1 text-slate-600 dark:text-slate-400">Fill in the details below to generate a new secure token.</p>
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
                <button type="button" @click="regenerateToken"
                        class="px-3 py-2 text-sm rounded-md bg-indigo-600 text-white hover:bg-indigo-700">
                  Regenerate
                </button>
                <button type="button" @click="copyToken"
                        class="px-3 py-2 text-sm rounded-md bg-slate-200 dark:bg-slate-700 text-slate-800 dark:text-slate-200 hover:bg-slate-300 dark:hover:bg-slate-600">
                  Copy
                </button>
              </div>
            </div>

            <!-- Quota & Usage -->
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label for="quota" class="block text-sm font-medium mb-1">Quota</label>
                <input id="quota" v-model.number="tokenForm.quota" type="number" min="0" step="1"
                       class="w-full px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-lg bg-white dark:bg-slate-700" />
              </div>
              <div>
                <label for="usage" class="block text-sm font-medium mb-1">Initial Usage</label>
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
                <input
                    id="expiredAt"
                    v-model="expiredAtInput"
                    type="datetime-local"
                    class="flex-1 px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-lg bg-white dark:bg-slate-700"
                />
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
              <button type="submit" :disabled="loading"
                      class="px-6 py-2 rounded-lg bg-primary-600 text-white hover:bg-primary-700 disabled:opacity-60 disabled:cursor-not-allowed">
                <span v-if="!loading">Create Token</span>
                <span v-else>Processing…</span>
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
                <span
                    :class="tokenForm.isActive ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
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
/* biar icon calendar kelihatan di dark mode */
input[type="datetime-local"]::-webkit-calendar-picker-indicator { filter: invert(0.5); }
</style>

