<script setup lang="ts">
import { reactive, ref, watch, onMounted } from 'vue'
import { useHead, navigateTo } from '#app'
import { useToast, useRoute } from '#imports'
import type { FeatureModel, AppModel } from '~/types/models/AppModel'
import { useAppsStore } from '~/stores/apps'

definePageMeta({ layout: 'dashboard' })

const route = useRoute()
const appId = String(route.params.id || '')

const apps = useAppsStore()
const toast = useToast()

/* ---------------- Utils ---------------- */
const deepClone = <T>(v: T): T => JSON.parse(JSON.stringify(v))

const slugify = (s: string) =>
    s?.toString()
        .trim()
        .toLowerCase()
        .replace(/[^a-z0-9]+/g, '-')
        .replace(/^-+|-+$/g, '')

/* ---------------- Local UI types ---------------- */
/** Template memakai title/image/description/isActive untuk Ads */
type UiAd = {
  title?: string | null
  image?: string | null
  description?: string | null
  isActive: boolean
}

/* ---------------- Factories ---------------- */
const makeFeature = (p: Partial<FeatureModel> = {}): FeatureModel => ({
  title: null,
  description: null,
  isActive: true,
  ...p
})

const makeAd = (p: Partial<UiAd> = {}): UiAd => ({
  title: '',
  image: '',
  description: '',
  isActive: true,
  ...p
})

/* ---------------- Source form ---------------- */
const appForm = reactive<any>({
  id: '',
  name: '',
  slug: '',
  image: '',
  description: '',
  isActive: true,
  features: [] as FeatureModel[],
  ads: [] as UiAd[]
}) as unknown as AppModel

// snapshot untuk reset (akan di-refresh setelah fetch)
let initialApp = deepClone(appForm)

/* ---------------- Editable lists (detached) ---------------- */
const features = ref<FeatureModel[]>(deepClone(appForm.features || []))
const adsList = ref<UiAd[]>(deepClone((appForm as any).ads || []))

/* ---------------- CRUD helpers ---------------- */
const addFeature = () =>
    features.value.push(makeFeature({ title: `Feature ${features.value.length + 1}` }))

const removeFeature = (i: number) => features.value.splice(i, 1)

const addAd = () =>
    adsList.value.push(makeAd({ title: `Ad ${adsList.value.length + 1}` }))

const removeAd = (i: number) => adsList.value.splice(i, 1)

/* ---------------- Submit & reset ---------------- */
const handleSubmit = async () => {
  // sinkronkan list ke form
  appForm.features = deepClone(features.value) as any
  ;(appForm as any).ads = deepClone(adsList.value)

  try {
    const res = await apps.updateApp(appId, appForm)
    if (res?.status?.toUpperCase?.() === 'SUCCESS') {
      await navigateTo('/dashboard/apps/list')
      toast.add?.({ title: 'Saved', description: 'Changes have been saved.', color: 'success' })
    } else {
      const error = res?.message ?? ['Save failed']
      toast.add?.({ title: 'Warning', description: error[0], color: 'warning' })
    }
  } catch (e: any) {
    console.error(e)
    toast.add?.({ title: 'Failed', description: e?.message || 'Save failed', color: 'error' })
  }
}

const handleReset = () => {
  Object.assign(appForm, deepClone(initialApp))
  features.value = deepClone(initialApp.features || [])
  adsList.value = deepClone((initialApp as any).ads || [])
}

const handleCancel = () => navigateTo('/dashboard/apps')

/* ---------------- Auto slug ---------------- */
watch(
    () => (appForm as any).name,
    (val) => {
      if (!val) return
          ;(appForm as any).slug = slugify(val)
    }
)

/* ------------- Fetch ------------- */
async function getData(id: string) {
  if (!id) return
  try {
    const res = await apps.getAppsById(id)
    // res.data bisa object atau array — kita normalkan
    const raw = Array.isArray(res?.data) ? res.data[0] : res?.data
    if (!raw) {
      toast.add?.({ title: 'Not found', description: 'Data aplikasi tidak ditemukan', color: 'warning' })
      return
    }

    const normalized = {
      id: raw.id ?? id,
      name: raw.name ?? '',
      slug: raw.slug ?? '',
      image: raw.image ?? '',
      description: raw.description ?? '',
      isActive: raw.isActive ?? true,
      features: Array.isArray(raw.features) ? raw.features : [],
      ads: Array.isArray(raw.ads) ? raw.ads : []
    }

    Object.assign(appForm, normalized)
    features.value = deepClone(appForm.features || [])
    adsList.value = deepClone((appForm as any).ads || [])
    initialApp = deepClone(appForm)
  } catch (e: unknown) {
    const msg = e instanceof Error ? e.message : 'Gagal memuat data'
    toast.add?.({ title: 'Error', description: msg, color: 'error' })
  }
}

onMounted(() => getData(appId))

/* ---------------- SEO ---------------- */
useHead(() => ({
  title: `Edit ${(appForm as any).name || 'App'} – Dashboard`,
  meta: [{ name: 'description', content: (appForm as any).description || 'Edit your application.' }]
}))
</script>

<template>
  <div
      class="min-h-screen bg-gradient-to-br from-primary-50 via-white to-purple-50 dark:from-slate-900 dark:via-slate-800 dark:to-slate-900">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-10 items-start">
        <!-- ========== FORM ========== -->
        <div class="lg:col-span-2 space-y-8">
          <div>
            <h1 class="text-3xl font-bold text-slate-900 dark:text-white">Edit App: {{ appForm.name }}</h1>
            <p class="mt-1 text-slate-600 dark:text-slate-400">Update your application details and features.</p>
          </div>

          <!-- App Details -->
          <div class="bg-white dark:bg-slate-800 rounded-2xl shadow-lg p-6 space-y-6">
            <h2 class="text-xl font-semibold text-slate-800 dark:text-white border-b pb-2">App Details</h2>

            <div>
              <label class="block text-sm font-medium mb-1">Name <span class="text-error-500">*</span></label>
              <input
                  v-model="appForm.name"
                  required
                  class="w-full px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-lg bg-white dark:bg-slate-700 focus:ring-2 focus:ring-primary-500"
              />
            </div>

            <div>
              <label class="block text-sm font-medium mb-1">Slug</label>
              <input
                  v-model="appForm.slug"
                  class="w-full px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-lg bg-white dark:bg-slate-700 focus:ring-2 focus:ring-primary-500"
              />
            </div>

            <div>
              <label class="block text-sm font-medium mb-1">Image URL</label>
              <input
                  v-model="appForm.image"
                  class="w-full px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-lg bg-white dark:bg-slate-700 focus:ring-2 focus:ring-primary-500"
              />
            </div>

            <div>
              <label class="block text-sm font-medium mb-1">Description</label>
              <textarea
                  v-model="appForm.description"
                  rows="3"
                  class="w-full px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-lg bg-white dark:bg-slate-700 focus:ring-2 focus:ring-primary-500 resize-none"
              />
            </div>

            <div class="flex items-center gap-3">
              <label class="text-sm font-medium">Active?</label>
              <USwitch v-model="appForm.isActive"/>
            </div>
          </div>

          <!-- Features -->
          <div class="bg-white dark:bg-slate-800 rounded-2xl shadow-lg p-6">
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-xl font-semibold text-slate-800 dark:text-white">Features</h2>
              <button
                  @click="addFeature"
                  type="button"
                  class="text-sm flex items-center gap-1 px-3 py-1 rounded-md bg-primary-100 dark:bg-primary-900 text-primary-700 dark:text-primary-300"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
                </svg>
                Add
              </button>
            </div>

            <div v-if="!features.length" class="text-sm text-slate-500 dark:text-slate-400 text-center py-4">
              No features yet.
            </div>

            <div v-else class="space-y-4">
              <div
                  v-for="(feat, i) in features"
                  :key="`feat-${i}`"
                  class="p-4 border border-slate-200 dark:border-slate-700 rounded-lg space-y-3"
              >
                <div>
                  <label class="text-xs font-medium">Title</label>
                  <input v-model="feat.title" class="w-full px-2 py-1 border rounded bg-white dark:bg-slate-700"/>
                </div>
                <div>
                  <label class="text-xs font-medium">Description</label>
                  <textarea
                      v-model="feat.description"
                      rows="2"
                      class="w-full px-2 py-1 border rounded bg-white dark:bg-slate-700 resize-none"
                  />
                </div>
                <div class="flex justify-between items-center">
                  <div class="flex items-center gap-2">
                    <label class="text-xs font-medium">Active</label>
                    <USwitch v-model="feat.isActive"/>
                  </div>
                  <button @click="removeFeature(i)" type="button" class="text-xs text-error-600 hover:underline">
                    Remove
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- Ads -->
          <div class="bg-white dark:bg-slate-800 rounded-2xl shadow-lg p-6">
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-xl font-semibold text-slate-800 dark:text-white">Ads</h2>
              <button
                  @click="addAd"
                  type="button"
                  class="text-sm flex items-center gap-1 px-3 py-1 rounded-md bg-primary-100 dark:bg-primary-900 text-primary-700 dark:text-primary-300"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
                </svg>
                Add
              </button>
            </div>

            <div v-if="!adsList.length" class="text-sm text-slate-500 dark:text-slate-400 text-center py-4">
              No ads yet.
            </div>

            <div v-else class="space-y-4">
              <div
                  v-for="(ad, i) in adsList"
                  :key="`ad-${i}`"
                  class="p-4 border border-slate-200 dark:border-slate-700 rounded-lg space-y-3"
              >
                <div>
                  <label class="text-xs font-medium">Name</label>
                  <input v-model="ad.title" class="w-full px-2 py-1 border rounded bg-white dark:bg-slate-700"/>
                </div>
                <div>
                  <label class="text-xs font-medium">Image URL</label>
                  <input v-model="ad.image" class="w-full px-2 py-1 border rounded bg-white dark:bg-slate-700"/>
                </div>
                <div>
                  <label class="text-xs font-medium">Description</label>
                  <textarea
                      v-model="ad.description"
                      rows="2"
                      class="w-full px-2 py-1 border rounded bg-white dark:bg-slate-700 resize-none"
                  />
                </div>
                <div class="flex justify-between items-center">
                  <div class="flex items-center gap-2">
                    <label class="text-xs font-medium">Active</label>
                    <USwitch v-model="ad.isActive"/>
                  </div>
                  <button @click="removeAd(i)" type="button" class="text-xs text-error-600 hover:underline">
                    Remove
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- Actions -->
          <div class="flex justify-end gap-4">
            <button
                @click="handleCancel"
                type="button"
                class="px-4 py-2 rounded-lg bg-slate-200 dark:bg-slate-700 text-slate-700 dark:text-slate-200 hover:bg-slate-300 dark:hover:bg-slate-600"
            >
              Cancel
            </button>
            <button @click="handleReset" type="button"
                    class="px-4 py-2 rounded-lg bg-amber-500 text-white hover:bg-amber-600">
              Reset
            </button>
            <button @click="handleSubmit" type="submit"
                    class="px-6 py-2 rounded-lg bg-primary-600 text-white hover:bg-primary-700">
              Save Changes
            </button>
          </div>
        </div>

        <!-- ========== PREVIEW CARD ========== -->
        <aside class="lg:col-span-1">
          <div class="sticky top-8">
            <h3 class="text-lg font-semibold text-slate-800 dark:text-white mb-4">Live Preview</h3>
            <div class="bg-white dark:bg-slate-800 rounded-2xl shadow-lg p-4 space-y-4">
              <img
                  :src="appForm.image || 'https://placehold.co/400x300'"
                  alt="Preview"
                  class="w-full h-40 object-cover rounded-lg"
                  @error="(e:any)=>{ e.target.src='https://placehold.co/400x300' }"
                  loading="lazy"
              />
              <div>
                <h4 class="font-bold text-lg text-slate-900 dark:text-white">{{ appForm.name }}</h4>
                <p class="text-sm text-slate-600 dark:text-slate-400 line-clamp-2">{{ appForm.description }}</p>
                <div class="mt-2">
                  <span
                      :class="appForm.isActive ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
                      class="text-xs font-medium px-2.5 py-0.5 rounded-full"
                  >
                    {{ appForm.isActive ? 'Active' : 'Inactive' }}
                  </span>
                </div>
              </div>

              <div v-if="features.length" class="border-t pt-3">
                <h5 class="font-semibold text-sm text-slate-700 dark:text-slate-300 mb-2">
                  Features ({{ features.length }})
                </h5>
                <ul class="space-y-1 text-xs text-slate-600 dark:text-slate-400">
                  <li v-for="(f, i) in features" :key="`pf-${i}`" class="flex items-center gap-1">
                    <svg v-if="f.isActive" class="w-3 h-3 text-green-500" fill="currentColor" viewBox="0 0 20 20">
                      <path
                          fill-rule="evenodd"
                          d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                          clip-rule="evenodd"
                      />
                    </svg>
                    <svg v-else class="w-3 h-3 text-slate-400" fill="currentColor" viewBox="0 0 20 20">
                      <path
                          fill-rule="evenodd"
                          d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                          clip-rule="evenodd"
                      />
                    </svg>
                    {{ f.title || 'Untitled feature' }}
                  </li>
                </ul>
              </div>

              <div v-if="adsList.length" class="border-t pt-3">
                <h5 class="font-semibold text-sm text-slate-700 dark:text-slate-300 mb-2">
                  Ads ({{ adsList.length }})
                </h5>
                <ul class="space-y-1 text-xs text-slate-600 dark:text-slate-400">
                  <li v-for="(a, i) in adsList" :key="`pa-${i}`">
                    {{ a.title || 'Untitled ad' }} <span v-if="!a.isActive" class="text-red-500">(disabled)</span>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </aside>
      </div>
    </div>
  </div>
</template>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
