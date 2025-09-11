<script setup lang="ts">
import * as v from 'valibot'
import type { FormSubmitEvent } from '@nuxt/ui'

definePageMeta({ layout: 'dashboard' })

const toast  = useToast()
const router = useRouter()
const route  = useRoute()
const store  = useUserPortfolioStore()

const projectId     = computed(() => route.params.id as string | undefined)
const loadingDetail = ref(false)
const submitting    = ref(false)

/* ============== Validation (Valibot) ============== */
const urlStr = v.pipe(v.string(), v.url('Must be a valid URL'))
const numberLike = v.union([
  v.number(),
  v.pipe(v.string(), v.transform((s) => Number(s)), v.number('Invalid number'))
])

const schema = v.object({
  title: v.pipe(v.string(), v.nonEmpty('Title is required')),
  imageUrl: v.optional(urlStr),
  githubUrl: v.optional(urlStr),
  demoUrl: v.optional(urlStr),
  description: v.optional(v.string()),
  technologiesStr: v.optional(v.string()),
  dateCreated: v.optional(v.string()),     // ← cukup string, prefill di state
  userRequest: v.optional(v.pipe(numberLike, v.minValue(0, 'Min 0'))),
  rating: v.optional(v.pipe(numberLike, v.minValue(0, 'Min 0'), v.maxValue(5, 'Max 5'))),
  releseVersion: v.optional(v.string())
})
type Schema = v.InferOutput<typeof schema>

/* ============== Date helpers ============== */
const pad2 = (n: number) => (n < 10 ? `0${n}` : `${n}`)
function toDateInput(v?: string | Date | null): string {
  if (!v) return ''
  try {
    if (typeof v === 'string') {
      const s = v.trim()
      if (/^\d{4}-\d{2}-\d{2}$/.test(s)) return s
      const d = new Date(s)
      if (isNaN(d.getTime())) return ''
      return `${d.getFullYear()}-${pad2(d.getMonth()+1)}-${pad2(d.getDate())}`
    }
    const d = v as Date
    if (isNaN(d.getTime())) return ''
    return `${d.getFullYear()}-${pad2(d.getMonth()+1)}-${pad2(d.getDate())}`
  } catch { return '' }
}
function toLocalDateTimeISO(input?: string | null): string | null {
  const s = (input ?? '').trim()
  if (!s) return null
  if (/^\d{4}-\d{2}-\d{2}$/.test(s)) return `${s}T00:00:00`
  if (/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/.test(s)) return `${s}:00`
  return s
}

/* ============== State ============== */
const form = reactive<Schema>({
  title: '',
  imageUrl: '',
  githubUrl: '',
  demoUrl: '',
  description: '',
  technologiesStr: '',
  dateCreated: '',
  userRequest: 0,
  rating: 0,
  releseVersion: ''
})

/* ============== Helpers ============== */
const splitComma = (s?: string) => (s || '').split(',').map(t => t.trim()).filter(Boolean)
const techList   = computed(() => splitComma(form.technologiesStr))
const clampRating = (n: number) => Math.max(0, Math.min(5, Math.round((Number(n)||0) * 2) / 2))
function setRating(n: number) { form.rating = clampRating(n) }

// Image preview (with default) — deklarasi dulu sebelum watch
const DEFAULT_IMAGE = 'https://images.unsplash.com/photo-1498050108023-c5249f4df085?q=80&w=1200&auto=format&fit=crop'
const preview   = ref<string>(DEFAULT_IMAGE)
const imgLoaded = ref(false)
const imgError  = ref(false)
const imgSize   = ref<{ w: number; h: number } | null>(null)

watch(() => form.imageUrl, (val) => {
  preview.value   = val || DEFAULT_IMAGE
  imgLoaded.value = false
  imgError.value  = false
  imgSize.value   = null
}, { immediate: true })

function onImgLoad(e: Event) {
  const el = e.target as HTMLImageElement
  imgLoaded.value = true
  imgError.value  = false
  imgSize.value   = { w: el.naturalWidth, h: el.naturalHeight }
}
function onImgError() { imgLoaded.value = false; imgError.value = true }
function clearImage() { form.imageUrl = ''; preview.value = DEFAULT_IMAGE }
const imageHost = computed(() => { try { return preview.value ? new URL(preview.value).host : '' } catch { return '' } })

/* ============== Load Detail ============== */
async function loadDetail() {
  if (!projectId.value) return
  try {
    loadingDetail.value = true
    const res = await store.getPortfolioProjectById?.(projectId.value)
    const d   = res?.data || res

    form.title           = d?.title ?? ''
    form.imageUrl        = d?.imageUrl ?? ''
    form.githubUrl       = d?.githubUrl ?? ''
    form.demoUrl         = d?.demoUrl ?? ''
    form.description     = d?.description ?? ''
    form.technologiesStr = (d?.technologies ?? []).join(', ')
    form.dateCreated     = toDateInput(d?.dateCreated as any) // prefill aman
    form.userRequest     = Number(d?.userRequest ?? 0)
    form.rating          = clampRating(Number(d?.rating ?? 0))
    form.releseVersion   = d?.releseVersion ?? ''
  } catch (e:any) {
    toast.add({ color:'error', title:'Load failed', description: e?.message || 'Gagal memuat project' })
  } finally {
    loadingDetail.value = false
  }
}
onMounted(loadDetail)

/* ============== Submit (UPDATE) ============== */
async function onSubmit(_e: FormSubmitEvent<Schema>) {
  if (!projectId.value) return
  try {
    submitting.value = true
    const payload = {
      title: form.title,
      imageUrl: form.imageUrl || '',
      githubUrl: form.githubUrl || '',
      demoUrl: form.demoUrl || '',
      description: form.description || '',
      technologies: techList.value,
      dateCreated: toLocalDateTimeISO(form.dateCreated), // <== penting untuk LocalDateTime backend
      userRequest: form.userRequest ?? null,
      rating: form.rating ?? null,
      releseVersion: form.releseVersion || ''
    }
    const res = await store.updatePortfolioProject?.(projectId.value, payload) // <== gunakan UPDATE
    const ok  = (res?.status ?? 'OK') === 'OK'
    if (ok) {
      toast.add({ title: 'Updated', description: 'Project updated.' })
      router.push('/dashboard/portfolio/project')
    } else {
      toast.add({ color: 'error', title: 'Failed', description: res?.message?.[0] || 'Update failed' })
    }
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="space-y-4">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
      <h1 class="text-2xl font-semibold text-slate-800 dark:text-white">Edit Project</h1>
      <UButton to="/dashboard/portfolio/project" as="nuxt-link" variant="ghost" icon="i-heroicons-arrow-left">Back</UButton>
    </div>

    <!-- Sticky actions -->
    <UCard class="sticky top-2 z-10 backdrop-blur bg-white/70 dark:bg-gray-900/70 border border-gray-200/60 dark:border-gray-800/60">
      <div class="flex items-center justify-between gap-3">
        <div class="text-sm text-slate-500 dark:text-slate-400">Ubah data proyek lalu simpan.</div>
        <div class="flex gap-2">
          <UButton type="submit" form="project-edit-form" :loading="submitting || loadingDetail" icon="i-heroicons-check-circle">Update</UButton>
          <UButton to="/dashboard/portfolio/project" as="nuxt-link" variant="outline" color="gray" icon="i-heroicons-x-mark">Cancel</UButton>
        </div>
      </div>
    </UCard>

    <!-- Form -->
    <UForm id="project-edit-form" :schema="schema" :state="form" class="space-y-4" @submit="onSubmit">
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <!-- LEFT -->
        <UCard class="space-y-4">
          <div class="grid grid-cols-1 gap-4">
            <UFormField label="Title" name="title">
              <UInput v-model="form.title" :disabled="loadingDetail" placeholder="BatamFast App" class="w-full"
                      :ui="{ base:'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:text-white' }"/>
            </UFormField>

            <UFormField label="GitHub URL" name="githubUrl">
              <UInput v-model="form.githubUrl" :disabled="loadingDetail" type="url" placeholder="https://github.com/username/repo" class="w-full"
                      :ui="{ base:'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:text-white' }"/>
            </UFormField>

            <UFormField label="Demo URL" name="demoUrl">
              <UInput v-model="form.demoUrl" :disabled="loadingDetail" type="url" placeholder="https://your-demo.com" class="w-full"
                      :ui="{ base:'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:text-white' }"/>
            </UFormField>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <UFormField label="Release Version" name="releseVersion">
                <UInput v-model="form.releseVersion" :disabled="loadingDetail" placeholder="4.3.1" class="w-full"
                        :ui="{ base:'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:text-white' }"/>
              </UFormField>
              <UFormField label="Date Created" name="dateCreated">
                <UInput v-model="form.dateCreated" :disabled="loadingDetail" type="date" class="w-full"
                        :ui="{ base:'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:text-white' }"/>
              </UFormField>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <UFormField label="User Request" name="userRequest">
                <UInput v-model="form.userRequest" :disabled="loadingDetail" type="number" min="0" step="1" class="w-full"
                        :ui="{ base:'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:text-white' }"/>
              </UFormField>

              <UFormField label="Rating (0–5)" name="rating">
                <div class="space-y-2">
                  <div class="flex items-center gap-1.5">
                    <button v-for="i in 5" :key="i" type="button" :disabled="loadingDetail"
                            class="p-1 rounded hover:bg-gray-100 dark:hover:bg-gray-800"
                            @click="setRating(i)">
                      <UIcon :name="(Number(form.rating) >= i) ? 'i-heroicons-star-20-solid' : 'i-heroicons-star'"
                             class="w-5 h-5"
                             :class="Number(form.rating) >= i ? 'text-amber-500' : 'text-gray-400'"/>
                    </button>
                    <UBadge variant="subtle">{{ Number(form.rating ?? 0).toFixed(1) }}</UBadge>
                  </div>
                  <input v-model="form.rating" :disabled="loadingDetail" type="range" min="0" max="5" step="0.5"
                         @input="form.rating = clampRating(Number(form.rating))"
                         class="w-full h-2 rounded-lg cursor-pointer bg-gray-200 dark:bg-gray-800"
                         style="accent-color: var(--color-amber-500, #f59e0b);" />
                </div>
              </UFormField>
            </div>
          </div>
        </UCard>

        <!-- RIGHT -->
        <UCard class="space-y-4">
          <!-- Image preview -->
          <div>
            <div class="flex items-center justify-between px-4 py-3 bg-gray-50 dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-t-xl">
              <div class="flex items-center gap-2 text-sm text-gray-700 dark:text-gray-300">
                <UIcon name="i-heroicons-photo" class="w-4 h-4" />
                <span>Preview</span>
                <span v-if="imageHost" class="text-xs text-gray-500">• {{ imageHost }}</span>
                <span v-if="imgSize" class="text-xs text-gray-500">• {{ imgSize.w }}×{{ imgSize.h }}px</span>
              </div>
              <UButton size="xs" color="red" variant="ghost" icon="i-heroicons-x-mark" :disabled="loadingDetail" @click="clearImage" />
            </div>
            <div class="relative w-full h-56 bg-gray-100 dark:bg-gray-900 border-x border-b border-gray-200 dark:border-gray-700 rounded-b-xl overflow-hidden">
              <div v-if="!imgLoaded && !imgError" class="absolute inset-0 animate-pulse bg-gray-200 dark:bg-gray-800" />
              <div v-if="imgError" class="absolute inset-0 flex items-center justify-center text-gray-500">Failed to load</div>
              <img v-show="!imgError" :src="preview" class="absolute inset-0 w-full h-full object-cover" @load="onImgLoad" @error="onImgError" />
            </div>

            <UFormField label="Image URL" name="imageUrl" class="mt-3">
              <div class="flex gap-2">
                <UInput v-model="form.imageUrl" :disabled="loadingDetail" placeholder="https://…" type="url" class="w-full"
                        :ui="{ base:'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:text-white' }"/>
                <UButton v-if="form.imageUrl" color="gray" variant="soft" icon="i-heroicons-arrow-top-right-on-square" :disabled="loadingDetail"
                         @click="form.imageUrl && window.open(form.imageUrl, '_blank')" />
              </div>
            </UFormField>
          </div>

          <UFormField label="Description" name="description">
            <UTextarea v-model="form.description" :disabled="loadingDetail" :rows="6" class="w-full"
                       placeholder="End-to-end booking & e-ticketing app for BatamFast ferries."
                       :ui="{ base:'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:text-white' }"/>
          </UFormField>

          <UFormField label="Technologies (comma separated)" name="technologiesStr">
            <div>
              <UInput v-model="form.technologiesStr" :disabled="loadingDetail" class="w-full"
                      placeholder="Flutter, Dart, REST API, Firebase, Maps, Geolocator"
                      :ui="{ base:'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:text-white' }"/>
              <div class="mt-2 flex flex-wrap gap-1.5">
                <UBadge v-for="(t,i) in techList" :key="i" variant="subtle">{{ t }}</UBadge>
              </div>
            </div>
          </UFormField>
        </UCard>
      </div>
    </UForm>
  </div>
</template>
