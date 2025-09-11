<script setup lang="ts">
import * as v from 'valibot'
import type { FormSubmitEvent } from '@nuxt/ui'
import type {UserPortfolioSkillModel} from "~/types/models/UserPortfolioModel";

definePageMeta({ layout: 'dashboard' })

const toast      = useToast()
const router     = useRouter()
const route      = useRoute()
const skillStore = useUserPortfolioStore()

/* ============ Edit mode ============ */
const skillId       = computed(() => (route.params?.id as string | undefined) || undefined)
const isEdit        = computed(() => !!skillId.value)
const loadingDetail = ref(false)

/* ============ Validation (Valibot) ============ */
const numberLike = v.union([
  v.number(),
  v.pipe(v.string(), v.transform((s) => Number(s)), v.number('Invalid number'))
])

const skillItemSchema = v.object({
  name: v.pipe(v.string(), v.nonEmpty('Skill name is required')),
  percentage: v.pipe(numberLike, v.minValue(0, 'Min 0'), v.maxValue(100, 'Max 100'))
})

const schema = v.object({
  title: v.pipe(v.string(), v.nonEmpty('Title is required')),
  description: v.optional(v.string()),
  skillData: v.pipe(v.array(skillItemSchema), v.minLength(1, 'Add at least 1 skill'))
})
type Schema = v.InferOutput<typeof schema>

/* ============ State ============ */
const form = reactive<Schema>({
  title: '',
  description: '',
  skillData: [{ name: '', percentage: 50 }]
})

/** Stable keys untuk tiap baris agar input tidak “lompat” */
const keys = ref<string[]>([newKey()])

/** Refs untuk auto-focus input name baris baru */
const nameRefs = ref<Record<string, HTMLInputElement | null>>({})
function setNameRef(key: string, el: any) {
  // Nuxt UI UInput membungkus input; ambil native input di dalamnya
  if (!el) return
  nameRefs.value[key] = el?.$el ? el.$el.querySelector('input') : (el as HTMLInputElement)
}

const submitting = ref(false)

/* ============ Helpers ============ */
function newKey() {
  return `${Date.now()}-${Math.random().toString(36).slice(2)}`
}

function clamp(n: number) {
  return Math.max(0, Math.min(100, Math.round(Number(n) || 0)))
}
const barClass = (p: number) => {
  if (p >= 80) return 'bg-emerald-500 dark:bg-emerald-400'
  if (p >= 50) return 'bg-amber-500 dark:bg-amber-400'
  return 'bg-rose-500 dark:bg-rose-400'
}
function gradientFor(p: number) {
  const v = clamp(p)
  if (v >= 80) return 'linear-gradient(90deg,#34d399,#06b6d4)'
  if (v >= 50) return 'linear-gradient(90deg,#f59e0b,#f97316)'
  return 'linear-gradient(90deg,#ef4444,#f43f5e)'
}
function isDuplicateName(idx: number) {
  const name = (form.skillData[idx]?.name || '').trim().toLowerCase()
  if (!name) return false
  return form.skillData.some((s, i) => i !== idx && (s.name || '').trim().toLowerCase() === name)
}
function moveSkill(i: number, dir: -1 | 1) {
  const j = i + dir
  if (j < 0 || j >= form.skillData.length) return
      ;[form.skillData[i], form.skillData[j]] = [form.skillData[j], form.skillData[i]]
  ;[keys.value[i], keys.value[j]] = [keys.value[j], keys.value[i]]
}

async function addSkill() {
  const k = newKey()
  form.skillData.unshift({ name: '', percentage: 50 }) // tambahkan di ATAS
  keys.value.unshift(k)
  await nextTick()
  nameRefs.value[k]?.focus?.() // auto-focus ke nama skill
}

function removeSkill(i: number) {
  if (form.skillData.length <= 1) return
  form.skillData.splice(i, 1)
  keys.value.splice(i, 1)
}

/* === Progress ring helpers === */
const RING_R = 26
const RING_C = 2 * Math.PI * RING_R
const ringDashOffset = (p: number) => ((100 - clamp(p)) / 100) * RING_C
const ringStrokeColor = (p: number) => {
  const v = clamp(p)
  if (v >= 80) return '#10b981' // emerald-500
  if (v >= 50) return '#f59e0b' // amber-500
  return '#ef4444'              // red-500
}

/* ============ Prefill (EDIT) ============ */
async function loadDetail() {
  if (!isEdit.value || !skillId.value) return
  try {
    loadingDetail.value = true
    const res  = await skillStore.getPortfolioSkillById(skillId.value)
    const data = res?.data || res as UserPortfolioSkillModel

    form.title       = data?.title ?? ''
    form.description = data?.description ?? ''

    const rows = Array.isArray(data?.skillData) ? data.skillData : []
    form.skillData   = rows.map((s: any) => ({
      name: (s?.name ?? '').toString(),
      percentage: clamp(Number(s?.percentage ?? 0))
    }))

    if (form.skillData.length === 0) {
      form.skillData = [{ name: '', percentage: 50 }]
    }
    // sinkronkan keys
    keys.value = form.skillData.map(() => newKey())
  } catch (e: any) {
    toast.add({ color: 'error', title: 'Load failed', description: e?.message || 'Gagal memuat detail' })
  } finally {
    loadingDetail.value = false
  }
}
onMounted(loadDetail)

/* ============ Submit ============ */
async function onSubmit(_evt: FormSubmitEvent<Schema>) {
  try {
    submitting.value = true
    const payload = {
      title: form.title,
      description: form.description || '',
      skillData: form.skillData.map(s => ({
        name: (s.name || '').trim(),
        percentage: clamp(s.percentage)
      }))
    }

    let res: any
    if (isEdit.value && skillId.value) {
      // ⚠️ Store kamu mendefinisikan updatePortfolioSkills(data, id) → data dulu, baru id
      res = await skillStore.updatePortfolioSkills(payload, skillId.value)
    } else {
      res = await skillStore.addPortfolioSkills(payload)
    }

    const ok = (res?.status ?? 'OK') === 'OK' || res?.success === true
    if (ok) {
      toast.add({
        title: isEdit.value ? 'Updated' : 'Saved',
        description: isEdit.value ? 'Category updated successfully.' : 'Category created successfully.'
      })
      router.push('/dashboard/portfolio/skill')
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
        {{ isEdit ? 'Edit Skill Category' : 'Create Skill Category' }}
      </h1>
      <div class="flex items-center gap-2">
        <UButton to="/dashboard/portfolio/skill" as="nuxt-link" variant="ghost" icon="i-heroicons-arrow-left">
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
        <UButton to="/dashboard/portfolio/skill" as="nuxt-link" variant="outline" color="gray" icon="i-heroicons-x-mark">
          Cancel
        </UButton>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <!-- KIRI: Info Kategori -->
        <UCard class="space-y-4">
          <div class="grid grid-cols-1 gap-4">
            <UFormField label="Title" name="title">
              <UInput
                  v-model="form.title"
                  placeholder="Mis. Mobile, Backend, Frontend"
                  class="w-full"
                  :disabled="loadingDetail"
                  :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
              />
            </UFormField>

            <UFormField label="Description" name="description">
              <UTextarea
                  v-model="form.description"
                  :rows="6"
                  :disabled="loadingDetail"
                  class="w-full"
                  :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
                  placeholder="Deskripsi singkat kategori (opsional)"
              />
            </UFormField>
          </div>
        </UCard>

        <!-- KANAN: Skill Items -->
        <UCard class="space-y-4">
          <!-- Header -->
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-2">
              <UIcon name="i-heroicons-sparkles" class="w-5 h-5 text-amber-500" />
              <div class="text-sm font-medium text-slate-800 dark:text-slate-200">Skills</div>
              <UBadge variant="subtle">{{ form.skillData.length }} item</UBadge>
            </div>
            <UButton size="xs" variant="outline" icon="i-heroicons-plus" :disabled="loadingDetail" @click="addSkill">
              Add Skill
            </UButton>
          </div>

          <!-- Empty -->
          <div v-if="form.skillData.length === 0" class="text-xs text-gray-500">
            Belum ada skill. Klik “Add Skill” untuk menambah.
          </div>

          <!-- Items -->
          <div
              v-for="(s, i) in form.skillData"
              :key="keys[i]"
              class="relative group rounded-2xl border border-gray-200/70 dark:border-gray-700/70 bg-white/60 dark:bg-gray-900/60 backdrop-blur px-4 py-4 shadow-sm hover:shadow-md transition-shadow mt-6"
          >
            <!-- Reorder & Remove (hover) -->
            <div class="absolute top-2 right-2 flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
              <UButton size="xs" variant="ghost" icon="i-heroicons-arrow-up"    :disabled="i===0 || loadingDetail" @click="moveSkill(i,-1)" />
              <UButton size="xs" variant="ghost" icon="i-heroicons-arrow-down" :disabled="i===form.skillData.length-1 || loadingDetail" @click="moveSkill(i,1)" />
              <UButton size="xs" color="error" variant="ghost" icon="i-heroicons-trash"
                       :disabled="form.skillData.length <= 1 || loadingDetail" @click="removeSkill(i)" />
            </div>

            <div class="grid grid-cols-1 md:grid-cols-12 gap-4 items-center">
              <!-- Ring progress -->
              <div class="md:col-span-3 flex justify-center md:justify-start">
                <div class="relative w-[72px] h-[72px]">
                  <svg viewBox="0 0 64 64" class="w-[72px] h-[72px] -rotate-90">
                    <!-- track -->
                    <circle cx="32" cy="32" :r="RING_R" stroke-width="8" fill="none"
                            class="text-gray-200 dark:text-gray-800" stroke="currentColor" />
                    <!-- progress -->
                    <circle cx="32" cy="32" :r="RING_R" stroke-width="8" stroke-linecap="round" fill="none"
                            class="transition-[stroke-dashoffset] duration-500 will-change-auto"
                            :style="{
                              strokeDasharray: RING_C + 'px',
                              strokeDashoffset: ringDashOffset(s.percentage) + 'px',
                              stroke: ringStrokeColor(s.percentage)
                            }" />
                  </svg>
                  <div class="absolute inset-0 rotate-90 flex items-center justify-center">
                    <span class="text-sm font-semibold text-slate-700 dark:text-slate-200">
                      {{ clamp(Number(s.percentage)) }}%
                    </span>
                  </div>
                </div>
              </div>

              <!-- Inputs & bars -->
              <div class="md:col-span-9 space-y-3">
                <!-- Nama + Duplikat -->
                <div>
                  <label class="block text-xs font-medium text-slate-600 dark:text-slate-300 mb-1">Skill Name</label>
                  <div class="relative">

                    <UInput
                        :ref="el => setNameRef(keys[i], el)"
                        v-model="s.name"
                        :disabled="loadingDetail"
                        placeholder="Mis. Android, Flutter, Kotlin"
                        class="w-full"
                        :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
                    />
                    <div v-if="isDuplicateName(i)" class="absolute -bottom-5 left-0 text-[11px] text-rose-500">
                      Nama skill duplikat dalam kategori ini.
                    </div>
                  </div>
                </div>

                <!-- Percentage + quick actions -->
                <div>
                  <label class="block text-xs font-medium text-slate-600 dark:text-slate-300 mb-1">Percentage</label>
                  <div class="flex items-center gap-2">
                    <div class="relative flex-1">
                      <UInput
                          v-model="s.percentage"
                          :disabled="loadingDetail"
                          type="number"
                          min="0"
                          max="100"
                          step="1"
                          class="w-full pr-10"
                          :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
                          @change="s.percentage = clamp(Number(s.percentage))"
                      />
                      <span class="absolute inset-y-0 right-0 pr-3 flex items-center text-xs text-gray-500">%</span>
                    </div>

                    <UButton
                        size="xs"
                        variant="soft"
                        icon="i-heroicons-minus-small"
                        aria-label="Kurangi 10"
                        :disabled="loadingDetail"
                        @click="s.percentage = clamp((Number(s.percentage) || 0) - 10)"
                    />
                    <UButton
                        size="xs"
                        variant="soft"
                        icon="i-heroicons-plus-small"
                        aria-label="Tambah 10"
                        :disabled="loadingDetail"
                        @click="s.percentage = clamp((Number(s.percentage) || 0) + 10)"
                    />
                  </div>

                  <!-- slider -->
                  <div class="mt-3">
                    <input
                        v-model="s.percentage"
                        type="range"
                        min="0"
                        max="100"
                        step="1"
                        :disabled="loadingDetail"
                        @input="s.percentage = clamp(Number(s.percentage))"
                        class="w-full h-2 rounded-lg appearance-none cursor-pointer bg-gray-200 dark:bg-gray-800
                             [--thumb:theme(colors.primary.500)] dark:[--thumb:theme(colors.primary.400)]
                             [--track:theme(colors.gray.200)] dark:[--track:theme(colors.gray.800)]"
                        style="accent-color: var(--thumb);"
                    />
                    <div class="mt-1 flex items-center justify-between text-[11px] text-gray-500 dark:text-gray-400">
                      <span>0%</span><span>50%</span><span>100%</span>
                    </div>
                  </div>

                  <!-- progress preview (gradient) -->
                  <div class="mt-3 h-3 w-full rounded-full bg-gray-200/70 dark:bg-gray-800/70 overflow-hidden">
                    <div
                        class="h-3 rounded-full transition-all duration-500"
                        :class="barClass(clamp(Number(s.percentage)))"
                        :style="{ width: clamp(Number(s.percentage)) + '%', background: gradientFor(s.percentage) }"
                        role="progressbar"
                        :aria-valuenow="clamp(Number(s.percentage))"
                        aria-valuemin="0"
                        aria-valuemax="100"
                    />
                  </div>
                  <div class="mt-1 text-[11px] text-gray-500 dark:text-gray-400">
                    {{ clamp(Number(s.percentage)) }}%
                  </div>
                </div>
              </div>
            </div>

            <!-- Index chip -->
            <div class="absolute -top-2 -left-2">
              <span class="inline-flex items-center justify-center rounded-full text-[10px] font-bold px-2 py-0.5 bg-primary-100 text-primary-700 dark:bg-primary-900/40 dark:text-primary-300">
                #{{ i + 1 }}
              </span>
            </div>
          </div>
        </UCard>
      </div>
    </UForm>
  </div>
</template>
