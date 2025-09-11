<script setup lang="ts">
import * as v from 'valibot'
import type { FormSubmitEvent } from '@nuxt/ui'
import type {UserPortfolioExperienceDataModel} from "~/types/models/UserPortfolioModel";

definePageMeta({ layout: 'dashboard' })

const toast  = useToast()
const router = useRouter()
const route  = useRoute()
const store  = useUserPortfolioStore()

/* ====== Edit mode ====== */
const expId  = computed(() => (route.params?.id as string | undefined) || undefined)
const isEdit = computed(() => !!expId.value)
const loadingDetail = ref(false)

/* ====== Validation (Valibot) ====== */
const schema = v.object({
  title:       v.pipe(v.string(), v.nonEmpty('Title is required')),
  company:     v.pipe(v.string(), v.nonEmpty('Company is required')),
  location:    v.optional(v.string()),
  startDate:   v.pipe(v.string(), v.nonEmpty('Start date is required')),
  endDate:     v.optional(v.string()),
  description: v.optional(v.string()),
  technologiesStr: v.optional(v.string()), // input tunggal, comma separated
  highlights:  v.pipe(v.array(v.pipe(v.string(), v.trim())), v.minLength(1, 'Add at least 1 highlight'))
})
type Schema = v.InferOutput<typeof schema>

/* ====== State ====== */
const form = reactive<Schema>({
  title: '',
  company: '',
  location: '',
  startDate: '',
  endDate: '',
  description: '',
  technologiesStr: '',
  highlights: ['']
})
const submitting = ref(false)

/* ====== Helpers ====== */
const splitComma = (s?: string) =>
    (s || '')
        .split(',')
        .map(t => t.trim())
        .filter(Boolean)

function addHighlight() {
  form.highlights.push('')
  // auto scroll ke bawah jika diperlukan
}
function removeHighlight(i: number) {
  if (form.highlights.length <= 1) return
  form.highlights.splice(i, 1)
}
function moveHighlight(i: number, dir: -1 | 1) {
  const j = i + dir
  if (j < 0 || j >= form.highlights.length) return
      ;[form.highlights[i], form.highlights[j]] = [form.highlights[j], form.highlights[i]]
}

/* ====== Prefill (EDIT) ====== */
async function loadDetail() {
  if (!isEdit.value || !expId.value) return
  try {
    loadingDetail.value = true
    const res  = await store.getPortfolioExperienceById(expId.value)
    const data = res?.data || res as UserPortfolioExperienceDataModel

    form.title       = data?.title ?? ''
    form.company     = data?.company ?? ''
    form.location    = data?.location ?? ''
    form.startDate   = data?.startDate ?? ''
    form.endDate     = data?.endDate ?? ''
    form.description = data?.description ?? ''
    form.technologiesStr = (data?.technologies ?? []).join(', ')
    form.highlights  = (Array.isArray(data?.highlights) && data?.highlights?.length)
        ? [...data.highlights]
        : ['']
  } catch (e: any) {
    toast.add({ color: 'error', title: 'Load failed', description: e?.message || 'Gagal memuat detail' })
  } finally {
    loadingDetail.value = false
  }
}
onMounted(loadDetail)

/* ====== Submit ====== */
async function onSubmit(_evt: FormSubmitEvent<Schema>) {
  try {
    submitting.value = true
    const payload = {
      title: form.title,
      company: form.company,
      location: form.location || '',
      startDate: form.startDate,
      endDate: form.endDate || '',
      description: form.description || '',
      technologies: splitComma(form.technologiesStr),
      highlights: form.highlights.map(h => h.trim()).filter(Boolean)
    }

    let res: any
    if (isEdit.value && expId.value) {
      // konsisten: (data, id)
      res = await store.updatePortfolioExperience( expId.value,payload)
    } else {
      res = await store.addPortfolioExperience(payload)
    }

    const ok = (res?.status ?? 'OK') === 'OK' || res?.success === true
    if (ok) {
      toast.add({
        title: isEdit.value ? 'Updated' : 'Saved',
        description: isEdit.value ? 'Experience updated successfully.' : 'Experience created successfully.'
      })
      router.push('/dashboard/portfolio/experience')
    } else {
      toast.add({
        color: 'error',
        title: 'Failed',
        description: res?.message?.[0] || res?.message || 'Operation failed'
      })
    }
  } catch (e: any) {
    toast.add({ color: 'error', title: 'Error', description: e?.message || 'Operation failed' })
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="space-y-4">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
      <h1 class="text-2xl font-semibold text-slate-800 dark:text-white">
        {{ isEdit ? 'Edit Experience' : 'Create Experience' }}
      </h1>
      <div class="flex items-center gap-2">
        <UButton to="/dashboard/portfolio/experience" as="nuxt-link" variant="ghost" icon="i-heroicons-arrow-left">
          Back
        </UButton>
      </div>
    </div>

    <!-- Form -->
    <UForm :schema="schema" :state="form" class="space-y-4" @submit="onSubmit">
      <!-- Actions -->
      <div class="lg:col-span-2 flex gap-3 w-full">
        <UButton type="submit" :loading="submitting || loadingDetail" icon="i-heroicons-check-circle">
          {{ isEdit ? 'Update' : 'Save' }}
        </UButton>
        <UButton to="/dashboard/portfolio/experience" as="nuxt-link" variant="outline" color="gray" icon="i-heroicons-x-mark">
          Cancel
        </UButton>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <!-- KIRI -->
        <UCard class="space-y-4">
          <div class="grid grid-cols-1 gap-4">
            <UFormField label="Title" name="title">
              <UInput
                  v-model="form.title"
                  placeholder="Senior Mobile Developer · FerryTech"
                  class="w-full"
                  :disabled="loadingDetail"
                  :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
              />
            </UFormField>

            <UFormField label="Company" name="company">
              <UInput
                  v-model="form.company"
                  placeholder="Batam Fast"
                  class="w-full"
                  :disabled="loadingDetail"
                  :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
              />
            </UFormField>

            <UFormField label="Location" name="location">
              <UInput
                  v-model="form.location"
                  placeholder="Batam / Remote"
                  class="w-full"
                  :disabled="loadingDetail"
                  :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
              />
            </UFormField>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <UFormField label="Start Date" name="startDate">
                <UInput
                    v-model="form.startDate"
                    placeholder="2024"
                    class="w-full"
                    :disabled="loadingDetail"
                    :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
                />
              </UFormField>
              <UFormField label="End Date" name="endDate">
                <UInput
                    v-model="form.endDate"
                    placeholder="Present"
                    class="w-full"
                    :disabled="loadingDetail"
                    :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
                />
              </UFormField>
            </div>
          </div>
        </UCard>

        <!-- KANAN -->
        <UCard class="space-y-4">
          <UFormField label="Description" name="description">
            <UTextarea
                v-model="form.description"
                :rows="6"
                class="w-full"
                :disabled="loadingDetail"
                placeholder="Led Flutter migration with modular architecture, 30% faster build times"
                :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
            />
          </UFormField>

          <!-- Technologies (comma separated + chips preview) -->
          <UFormField label="Technologies (comma separated)" name="technologiesStr">
            <div>
              <UInput
                  v-model="form.technologiesStr"
                  placeholder="Flutter, Dart, GetX, Firebase, Play Console, Fastlane"
                  class="w-full"
                  :disabled="loadingDetail"
                  :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
              />
              <div class="mt-2 flex flex-wrap gap-1.5">
                <UBadge v-for="(t,i) in (form.technologiesStr || '').split(',').map(s => s.trim()).filter(Boolean)" :key="i" variant="subtle">
                  {{ t }}
                </UBadge>
              </div>
            </div>
          </UFormField>

          <!-- Highlights (dynamic rows) -->
          <div class="space-y-3">
            <div class="flex items-center justify-between">
              <label class="text-sm font-medium">Highlights</label>
              <UButton size="xs" variant="outline" icon="i-heroicons-plus" :disabled="loadingDetail" @click="addHighlight">
                Add
              </UButton>
            </div>

            <div v-if="form.highlights.length === 0" class="text-xs text-gray-500">
              Belum ada highlight. Klik “Add” untuk menambah.
            </div>

            <div
                v-for="(h, i) in form.highlights"
                :key="i"
                class="relative group rounded-xl border border-gray-200/60 dark:border-gray-700/60 bg-white/60 dark:bg-gray-900/60 px-3 py-3"
            >
              <div class="absolute top-2 right-2 flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                <UButton size="xs" variant="ghost" icon="i-heroicons-arrow-up"    :disabled="i===0 || loadingDetail" @click="moveHighlight(i,-1)" />
                <UButton size="xs" variant="ghost" icon="i-heroicons-arrow-down" :disabled="i===form.highlights.length-1 || loadingDetail" @click="moveHighlight(i,1)" />
                <UButton size="xs" color="red" variant="ghost" icon="i-heroicons-trash"
                         :disabled="form.highlights.length <= 1 || loadingDetail" @click="removeHighlight(i)" />
              </div>

              <UFormField :label="`Highlight #${i+1}`" :name="`highlights.${i}`">
                <UInput
                    v-model="form.highlights[i]"
                    placeholder="Led Flutter migration with modular architecture, 30% faster build times"
                    class="w-full"
                    :disabled="loadingDetail"
                    :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
                />
              </UFormField>
            </div>
          </div>
        </UCard>
      </div>
    </UForm>
  </div>
</template>
