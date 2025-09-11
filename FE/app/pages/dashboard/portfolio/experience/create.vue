<script setup lang="ts">
import * as v from 'valibot'
import type { FormSubmitEvent } from '@nuxt/ui'

definePageMeta({ layout: 'dashboard' })

const toast  = useToast()
const router = useRouter()
const store  = useUserPortfolioStore()

/* ================= Validation (Valibot) ================= */
const schema = v.object({
  title:       v.pipe(v.string(), v.nonEmpty('Title is required')),
  company:     v.pipe(v.string(), v.nonEmpty('Company is required')),
  location:    v.optional(v.string()),
  startDate:   v.pipe(v.string(), v.nonEmpty('Start date is required')),
  endDate:     v.optional(v.string()),
  description: v.optional(v.string()),
  technologiesStr: v.optional(v.string()),
  highlights:  v.pipe(
      v.array(v.pipe(v.string(), v.transform(s => s.trim()))),
      v.minLength(1, 'Add at least 1 highlight')
  )
})
type Schema = v.InferOutput<typeof schema>

/* ================= State ================= */
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

/* ================= Helpers ================= */
const splitComma = (s?: string) =>
    (s || '')
        .split(',')
        .map(t => t.trim())
        .filter(Boolean)

function addHighlight() {
  form.highlights.push('')
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

const techPreview = computed(() => splitComma(form.technologiesStr))

/* ================= Submit (CREATE) ================= */
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
      technologies: techPreview.value,
      highlights: form.highlights.map(h => h.trim()).filter(Boolean)
    }

    // pastikan store punya method ini
    const res = await store.addPortfolioExperience(payload)

    const ok = (res?.status ?? 'OK') === 'OK'

    if (ok) {
      toast.add({ title: 'Saved', description: 'Experience created successfully.' })
      router.push('/dashboard/portfolio/experience')
    } else {
      toast.add({ color: 'error', title: 'Failed', description: res?.message?.[0] || res?.message || 'Create failed' })
    }
  } catch (e: any) {
    toast.add({ color: 'error', title: 'Error', description: e?.message || 'Create failed' })
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="space-y-4">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
      <h1 class="text-2xl font-semibold text-slate-800 dark:text-white">Create Experience</h1>
      <div class="flex items-center gap-2">
        <UButton to="/dashboard/portfolio/experience" as="nuxt-link" variant="ghost" icon="i-heroicons-arrow-left">
          Back
        </UButton>
      </div>
    </div>

    <!-- Form -->
    <UForm :schema="schema" :state="form" class="space-y-4" @submit="onSubmit">
      <!-- Sticky actions -->
      <UCard class="sticky top-2 z-10 backdrop-blur bg-white/70 dark:bg-gray-900/70 border border-gray-200/60 dark:border-gray-800/60">
        <div class="flex items-center justify-between gap-3">
          <div class="text-sm text-slate-500 dark:text-slate-400">
            Fill the form then save. Fields marked required.
          </div>
          <div class="flex gap-2">
            <UButton type="submit" :loading="submitting" icon="i-heroicons-check-circle">Save</UButton>
            <UButton to="/dashboard/portfolio/experience" as="nuxt-link" variant="outline" color="gray" icon="i-heroicons-x-mark">
              Cancel
            </UButton>
          </div>
        </div>
      </UCard>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <!-- KIRI -->
        <UCard class="space-y-4">
          <div class="grid grid-cols-1 gap-4">
            <UFormField label="Title" name="title">
              <UInput v-model="form.title" placeholder="Senior Mobile Developer · FerryTech" class="w-full"
                      :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }" />
            </UFormField>

            <UFormField label="Company" name="company">
              <UInput v-model="form.company" placeholder="Batam Fast" class="w-full"
                      :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }" />
            </UFormField>

            <UFormField label="Location" name="location">
              <UInput v-model="form.location" placeholder="Batam / Remote" class="w-full"
                      :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }" />
            </UFormField>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <UFormField label="Start Date" name="startDate">
                <UInput v-model="form.startDate" placeholder="2024" class="w-full"
                        :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }" />
              </UFormField>
              <UFormField label="End Date" name="endDate">
                <UInput v-model="form.endDate" placeholder="Present" class="w-full"
                        :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }" />
              </UFormField>
            </div>
          </div>
        </UCard>

        <!-- KANAN -->
        <UCard class="space-y-4">
          <UFormField label="Description" name="description">
            <UTextarea v-model="form.description" :rows="6" class="w-full"
                       placeholder="Led Flutter migration with modular architecture, 30% faster build times"
                       :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }" />
          </UFormField>

          <UFormField label="Technologies (comma separated)" name="technologiesStr">
            <div>
              <UInput v-model="form.technologiesStr" class="w-full"
                      placeholder="Flutter, Dart, GetX, Firebase, Play Console, Fastlane"
                      :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }" />
              <div class="mt-2 flex flex-wrap gap-1.5">
                <UBadge v-for="(t,i) in techPreview" :key="i" variant="subtle">{{ t }}</UBadge>
              </div>
            </div>
          </UFormField>

          <!-- Highlights -->
          <div class="space-y-3">
            <div class="flex items-center justify-between">
              <label class="text-sm font-medium">Highlights</label>
              <UButton size="xs" variant="outline" icon="i-heroicons-plus" @click="addHighlight">Add</UButton>
            </div>

            <div v-for="(h, i) in form.highlights" :key="i"
                 class="relative group rounded-xl border border-gray-200/60 dark:border-gray-700/60 bg-white/60 dark:bg-gray-900/60 backdrop-blur px-3 py-3">
              <div class="absolute top-2 right-2 flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                <UButton size="xs" variant="ghost" icon="i-heroicons-arrow-up"  :disabled="i===0" @click="moveHighlight(i,-1)" />
                <UButton size="xs" variant="ghost" icon="i-heroicons-arrow-down" :disabled="i===form.highlights.length-1" @click="moveHighlight(i,1)" />
                <UButton size="xs" color="red" variant="ghost" icon="i-heroicons-trash" :disabled="form.highlights.length<=1" @click="removeHighlight(i)" />
              </div>
              <UInput v-model="form.highlights[i]" placeholder="e.g. Introduced CI/CD (GH Actions + Fastlane) for automated releases"
                      class="w-full"
                      :ui="{ base: 'mt-8 bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"/>
            </div>
          </div>
        </UCard>

      </div>
    </UForm>
  </div>
</template>
