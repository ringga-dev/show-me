<script setup lang="ts">
import { reactive, ref, computed, watch, onMounted } from 'vue'
import type { FormSubmitEvent } from '@nuxt/ui'
import { useToast } from '#imports'
import { useUserStore } from '~/stores/user'
import type { BaseResponse } from '~/types/models/BaseResponse'
import type { UserModel } from '~/types/models/UserModel'

definePageMeta({ layout: 'dashboard' })

const toast = useToast()
const userStore = useUserStore()

/* ============ Helpers tanggal ============ */
function toDateInput(iso?: string | null): string {
  if (!iso) return ''
  const d = new Date(iso)
  if (isNaN(d.getTime())) return ''
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
}
function fromDateInput(ymd?: string): string | null {
  if (!ymd) return null
  const d = new Date(ymd + 'T00:00:00')
  return isNaN(d.getTime()) ? null : d.toISOString()
}

/* ============ Form state ============ */
const form = reactive({
  fullName: '',
  email: '',
  phone: '',
  dateOfBirth: '',           // YYYY-MM-DD untuk <input type="date">
  gender: 'MALE',            // backend uppercase
  address: '',
  city: '',
  province: '',
  postalCode: '',
  profilePictureUrl: '',
  bio: ''
})

/* ============ Prefill dari API ============ */
const FALLBACK_AVA = 'https://i.pravatar.cc/300?img=5'
const previewUrl = ref<string>(FALLBACK_AVA)
const previewBroken = ref(false)

onMounted(async () => {
  const me = await userStore.fetchProfile()
  if (me) {
    form.fullName = me.fullName ?? ''
    form.email = me.email ?? ''
    form.phone = me.phone ?? ''
    form.dateOfBirth = toDateInput(me?.dateOfBirth)
    form.gender = (me.gender as string) ?? 'MALE'
    form.address = me.address ?? ''
    form.city = me.city ?? ''
    form.province = me.province ?? ''
    form.postalCode = me.postalCode ?? ''
    form.profilePictureUrl = me.profilePictureUrl ?? ''
    form.bio = me.bio ?? ''
  }
})

/* Sinkron preview avatar */
watch(
    () => form.profilePictureUrl,
    (v) => {
      previewBroken.value = false
      previewUrl.value = v && v.trim() ? v.trim() : FALLBACK_AVA
    },
    { immediate: true }
)
function onImgError() {
  previewBroken.value = true
  previewUrl.value = FALLBACK_AVA
}

/* ============ Derived kecil ============ */
const ageText = computed(() => {
  if (!form.dateOfBirth) return '—'
  const dob = new Date(form.dateOfBirth)
  if (isNaN(dob.getTime())) return '—'
  const diff = Date.now() - dob.getTime()
  const age = new Date(diff).getUTCFullYear() - 1970
  return `${age} yrs`
})
const saving = ref(false)

/* ============ Validasi ringan ============ */
function validate(): string | null {
  if (!form.fullName.trim()) return 'Full name wajib diisi.'
  if (!form.email.trim()) return 'Email wajib diisi.'
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) return 'Format email tidak valid.'
  if (form.postalCode && !/^\d{4,10}$/.test(form.postalCode)) return 'Kode pos 4–10 digit.'
  if (form.profilePictureUrl && !/^https?:\/\//i.test(form.profilePictureUrl)) return 'URL avatar harus diawali http/https.'
  return null
}

/* ============ Submit (updateProfile TIDAK diubah) ============ */
async function onSubmit(_evt: FormSubmitEvent<typeof form>) {
  const err = validate()
  if (err) {
    toast.add({ title: 'Validasi gagal', description: err, color: 'error' })
    return
  }
  try {
    saving.value = true
    const payload = {
      ...form,
      dateOfBirth: fromDateInput(form.dateOfBirth)
    }
    const res = await userStore.updateProfile(JSON.stringify(payload)) as BaseResponse<UserModel> | null
    if (res?.code === 200 && res.data) {
      toast.add({ title: 'Tersimpan', description: 'Profil berhasil diperbarui.' })
      await userStore.fetchProfile()
    } else {
      throw new Error(res?.message?.[0] || 'Gagal update profile')
    }
  } catch (e: any) {
    toast.add({ title: 'Error', description: e?.message || 'Gagal update profile', color: 'error' })
  } finally {
    saving.value = false
  }
}
</script>

<template>
 <div>
   <!-- Background gradient halus -->
   <div class="fixed inset-0 -z-10 bg-gradient-to-br from-slate-50 via-white to-indigo-50 dark:from-slate-950 dark:via-slate-900 dark:to-indigo-950" />

   <div class="mx-auto max-w-6xl px-4 py-10 sm:px-6 lg:px-8">
     <!-- Header sticky -->
     <div class="sticky top-0 z-20 -mx-4 mb-8 border-b bg-white/75 px-4 py-4 backdrop-blur dark:border-slate-800 dark:bg-slate-900/75 sm:mx-0 sm:rounded-xl">
       <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
         <div>
           <h1 class="text-2xl font-bold tracking-tight text-slate-900 dark:text-white">Edit Profile</h1>
           <p class="text-sm text-slate-600 dark:text-slate-400">Perbarui informasi akun & foto profil (via URL)</p>
         </div>
         <div class="flex items-center gap-2">
           <button
               type="button"
               class="inline-flex items-center gap-2 rounded-lg border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700 shadow-sm hover:bg-slate-50 dark:border-slate-700 dark:bg-slate-800 dark:text-slate-200 dark:hover:bg-slate-700"
               @click="$router.back()"
           >
             <span class="i-heroicons-arrow-uturn-left-20-solid" />
             Back
           </button>
           <button
               form="profileForm"
               type="submit"
               :disabled="saving"
               class="inline-flex items-center gap-2 rounded-lg bg-indigo-600 px-5 py-2 text-sm font-semibold text-white shadow hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-60"
           >
             <span class="i-heroicons-check-20-solid" />
             <span v-if="!saving">Save</span>
             <span v-else>Saving…</span>
           </button>
         </div>
       </div>
     </div>

     <!-- Grid konsisten 12 kolom -->
     <div class="grid grid-cols-1 gap-8 md:grid-cols-12">
       <!-- Preview (4 kolom) -->
       <aside class="md:col-span-4">
         <div class="sticky top-24">
           <div class="rounded-2xl border border-slate-200/70 bg-white/70 p-6 shadow-xl backdrop-blur dark:border-slate-800/70 dark:bg-slate-900/60">
             <!-- Background -->
             <div class="relative mb-6 h-28 w-full overflow-hidden rounded-xl">
               <div
                   class="absolute inset-0 -z-10 bg-gradient-to-br from-indigo-300/40 via-fuchsia-300/30 to-emerald-300/40 blur-2xl"
               ></div>
               <div
                   class="absolute inset-0 -z-10 bg-[radial-gradient(circle_at_20%_10%,rgba(99,102,241,.25),transparent_40%),radial-gradient(circle_at_80%_20%,rgba(244,114,182,.2),transparent_45%)]"
               ></div>
             </div>

             <!-- Avatar -->
             <div class="-mt-25 mb-4 flex justify-center relative z-10">
               <img
                   :src="previewUrl"
                   @error="onImgError"
                   alt="Avatar"
                   class="h-48 w-48 rounded-2xl border border-white/70 object-cover shadow-xl ring-1 ring-black/5 dark:border-slate-800/70"
               />
             </div>


             <div class="text-center">
               <h2 class="truncate text-lg font-semibold text-slate-800 dark:text-white">{{ form.fullName || '—' }}</h2>
               <p class="mt-1 line-clamp-3 text-sm text-slate-500 dark:text-slate-400">{{ form.bio || 'Tambahkan bio singkat.' }}</p>
             </div>


             <!-- STATS TILES -->
             <div class="grid grid-cols-3 gap-3 mt-6">
               <div
                   class="group rounded-2xl border border-slate-200/70 bg-white/70 p-3 text-center shadow-[0_4px_18px_-8px_rgba(15,23,42,.25)] transition hover:-translate-y-0.5 hover:shadow-[0_10px_25px_-10px_rgba(99,102,241,.35)] dark:border-slate-700/70 dark:bg-slate-900/60"
               >
                 <p class="text-[11px] uppercase tracking-wide text-slate-500 dark:text-slate-400">Age</p>
                 <p class="mt-0.5 text-sm font-semibold text-slate-900 dark:text-slate-100">{{ ageText }}</p>
               </div>
               <div
                   class="group rounded-2xl border border-slate-200/70 bg-white/70 p-3 text-center shadow-[0_4px_18px_-8px_rgba(15,23,42,.25)] transition hover:-translate-y-0.5 hover:shadow-[0_10px_25px_-10px_rgba(236,72,153,.35)] dark:border-slate-700/70 dark:bg-slate-900/60"
               >
                 <p class="text-[11px] uppercase tracking-wide text-slate-500 dark:text-slate-400">Gender</p>
                 <p class="mt-0.5 text-sm font-semibold capitalize text-slate-900 dark:text-slate-100">{{ form.gender || '—' }}</p>
               </div>
               <div
                   class="group rounded-2xl border border-slate-200/70 bg-white/70 p-3 text-center shadow-[0_4px_18px_-8px_rgba(15,23,42,.25)] transition hover:-translate-y-0.5 hover:shadow-[0_10px_25px_-10px_rgba(16,185,129,.35)] dark:border-slate-700/70 dark:bg-slate-900/60"
               >
                 <p class="text-[11px] uppercase tracking-wide text-slate-500 dark:text-slate-400">City</p>
                 <p class="mt-0.5 text-sm font-semibold text-slate-900 dark:text-slate-100">{{ form.city || '—' }}</p>
               </div>
             </div>

             <!-- LOCATION / ADDRESS compact -->
             <div class="mt-5 rounded-2xl border border-slate-200/70 bg-slate-50/70 p-3 text-xs text-slate-600 ring-1 ring-white/40 dark:border-slate-700/70 dark:bg-slate-800/60 dark:text-slate-300">
               <div class="flex items-start gap-2">
                 <span class="i-heroicons-map-pin-20-solid mt-0.5 text-slate-400"></span>
                 <p class="line-clamp-2">
                   {{ form.address || 'Alamat belum diisi.' }}
                 </p>
               </div>
             </div>

             <!-- BROKEN URL WARNING -->
             <div
                 v-if="previewBroken"
                 class="mt-4 rounded-xl bg-rose-50 px-3 py-2 text-xs text-rose-700 ring-1 ring-rose-200/70 dark:bg-rose-900/30 dark:text-rose-300 dark:ring-rose-800/50"
             >
               URL gambar gagal dimuat. Menampilkan placeholder.
             </div>

             <div v-if="previewBroken" class="mt-4 rounded-lg bg-rose-50 px-3 py-2 text-xs text-rose-700 dark:bg-rose-900/30 dark:text-rose-300">
               URL gambar gagal dimuat. Menampilkan placeholder.
             </div>
           </div>
         </div>
       </aside>

       <!-- Form (8 kolom) -->
       <main class="md:col-span-8">
         <form id="profileForm" @submit.prevent="onSubmit" class="rounded-2xl border border-slate-200/70 bg-white/70 p-6 shadow-xl backdrop-blur dark:border-slate-800/70 dark:bg-slate-900/60">
           <div class="mb-6">
             <h3 class="text-lg font-semibold text-slate-900 dark:text-white">Profile Details</h3>
             <p class="text-sm text-slate-500 dark:text-slate-400">Semua kolom di bawah konsisten lebar penuh.</p>
           </div>

           <!-- 2 kolom responsif, semua input w-full -->
           <div class="grid grid-cols-1 gap-5 sm:grid-cols-2">
             <!-- Full Name -->
             <div class="sm:col-span-1">
               <label class="mb-1 block text-sm font-medium text-slate-700 dark:text-slate-200">Full Name <span class="text-rose-500">*</span></label>
               <input
                   v-model="form.fullName"
                   type="text"
                   placeholder="John Doe"
                   class="block w-full rounded-xl border border-slate-300 bg-white px-3 py-2 text-slate-900 shadow-sm outline-none ring-0 transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/50 dark:border-slate-700 dark:bg-slate-800 dark:text-slate-100"
               />
             </div>

             <!-- Email -->
             <div class="sm:col-span-1">
               <label class="mb-1 block text-sm font-medium text-slate-700 dark:text-slate-200">Email <span class="text-rose-500">*</span></label>
               <input
                   v-model="form.email"
                   type="email"
                   disabled
                   placeholder="john@company.com"
                   class="block w-full rounded-xl border border-slate-300 bg-white px-3 py-2 text-slate-900 shadow-sm outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/50 dark:border-slate-700 dark:bg-slate-800 dark:text-slate-100"
               />
             </div>

             <!-- Phone -->
             <div class="sm:col-span-1">
               <label class="mb-1 block text-sm font-medium text-slate-700 dark:text-slate-200">Phone</label>
               <input
                   v-model="form.phone"
                   type="tel"
                   placeholder="+62…"
                   class="block w-full rounded-xl border border-slate-300 bg-white px-3 py-2 text-slate-900 shadow-sm outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/50 dark:border-slate-700 dark:bg-slate-800 dark:text-slate-100"
               />
             </div>

             <!-- Birth Date -->
             <div class="sm:col-span-1">
               <label class="mb-1 block text-sm font-medium text-slate-700 dark:text-slate-200">Birth Date</label>
               <input
                   v-model="form.dateOfBirth"
                   type="date"
                   class="block w-full rounded-xl border border-slate-300 bg-white px-3 py-2 text-slate-900 shadow-sm outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/50 dark:border-slate-700 dark:bg-slate-800 dark:text-slate-100"
               />
             </div>

             <!-- Gender -->
             <div class="sm:col-span-1">
               <label class="mb-1 block text-sm font-medium text-slate-700 dark:text-slate-200">Gender</label>
               <select
                   v-model="form.gender"
                   class="block w-full appearance-none rounded-xl border border-slate-300 bg-white px-3 py-2 text-slate-900 shadow-sm outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/50 dark:border-slate-700 dark:bg-slate-800 dark:text-slate-100"
               >
                 <option value="MALE">Male</option>
                 <option value="FEMALE">Female</option>
                 <option value="OTHER">Other</option>
               </select>
             </div>

             <!-- Profile Picture URL -->
             <div class="sm:col-span-1">
               <label class="mb-1 block text-sm font-medium text-slate-700 dark:text-slate-200">Profile Picture URL</label>
               <input
                   v-model="form.profilePictureUrl"
                   type="url"
                   placeholder="https://example.com/avatar.jpg"
                   class="block w-full rounded-xl border border-slate-300 bg-white px-3 py-2 text-slate-900 shadow-sm outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/50 dark:border-slate-700 dark:bg-slate-800 dark:text-slate-100"
               />
               <p class="mt-1 text-xs text-slate-500 dark:text-slate-400">Masukkan URL gambar (http/https). Preview akan otomatis.</p>
             </div>

             <!-- Address (full width) -->
             <div class="sm:col-span-2">
               <label class="mb-1 block text-sm font-medium text-slate-700 dark:text-slate-200">Address</label>
               <textarea
                   v-model="form.address"
                   rows="3"
                   placeholder="Street, number…"
                   class="block w-full resize-y rounded-xl border border-slate-300 bg-white px-3 py-2 text-slate-900 shadow-sm outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/50 dark:border-slate-700 dark:bg-slate-800 dark:text-slate-100"
               />
             </div>

             <div class="sm:col-span-1">
               <label class="mb-1 block text-sm font-medium text-slate-700 dark:text-slate-200">City</label>
               <input
                   v-model="form.city"
                   type="text"
                   placeholder="Batam"
                   class="block w-full rounded-xl border border-slate-300 bg-white px-3 py-2 text-slate-900 shadow-sm outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/50 dark:border-slate-700 dark:bg-slate-800 dark:text-slate-100"
               />
             </div>

             <div class="sm:col-span-1">
               <label class="mb-1 block text-sm font-medium text-slate-700 dark:text-slate-200">Province/State</label>
               <input
                   v-model="form.province"
                   type="text"
                   placeholder="Kepulauan Riau"
                   class="block w-full rounded-xl border border-slate-300 bg-white px-3 py-2 text-slate-900 shadow-sm outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/50 dark:border-slate-700 dark:bg-slate-800 dark:text-slate-100"
               />
             </div>

             <div class="sm:col-span-1">
               <label class="mb-1 block text-sm font-medium text-slate-700 dark:text-slate-200">Postal Code</label>
               <input
                   v-model="form.postalCode"
                   type="text"
                   placeholder="231312"
                   class="block w-full rounded-xl border border-slate-300 bg-white px-3 py-2 text-slate-900 shadow-sm outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/50 dark:border-slate-700 dark:bg-slate-800 dark:text-slate-100"
               />
             </div>

             <!-- Bio (full width) -->
             <div class="sm:col-span-2">
               <label class="mb-1 block text-sm font-medium text-slate-700 dark:text-slate-200">Bio</label>
               <textarea
                   v-model="form.bio"
                   rows="4"
                   placeholder="Tell us a bit about yourself…"
                   class="block w-full resize-y rounded-xl border border-slate-300 bg-white px-3 py-2 text-slate-900 shadow-sm outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/50 dark:border-slate-700 dark:bg-slate-800 dark:text-slate-100"
               />
               <div class="mt-1 text-right text-[11px] text-slate-500">{{ (form.bio || '').length }}</div>
             </div>
           </div>

           <!-- Actions bottom -->
           <div class="mt-6 flex justify-end gap-3">
<!--             <button-->
<!--                 type="button"-->
<!--                 class="inline-flex items-center gap-2 rounded-lg border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700 shadow-sm hover:bg-slate-50 dark:border-slate-700 dark:bg-slate-800 dark:text-slate-200 dark:hover:bg-slate-700"-->
<!--                 @click="$router.back()"-->
<!--             >-->
<!--               Cancel-->
<!--             </button>-->
<!--             <button-->
<!--                 type="submit"-->
<!--                 :disabled="saving"-->
<!--                 class="inline-flex items-center gap-2 rounded-lg bg-indigo-600 px-5 py-2 text-sm font-semibold text-white shadow hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-60"-->
<!--             >-->
<!--               <span v-if="!saving">Save Changes</span>-->
<!--               <span v-else>Saving…</span>-->
<!--             </button>-->
           </div>
         </form>
       </main>
     </div>
   </div>
 </div>
</template>
