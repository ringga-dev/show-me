<script setup lang="ts">
import * as v from 'valibot'
import type { FormSubmitEvent } from '@nuxt/ui'

definePageMeta({ layout: 'dashboard' })

const toast   = useToast()
const router  = useRouter()
const skillStore = useUserPortfolioStore()

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
  icon: '',
  skillData: [{ name: '', percentage: 50 }]
})

/** Stable keys untuk v-for agar input per baris selalu responsive */
const keys = ref<string[]>([newKey()])

/** ref ke input name per baris supaya bisa auto-focus saat tambah */
const nameRefs = ref<Record<string, HTMLInputElement | null>>({})
function setNameRef(key: string, el: any) {
  if (el) nameRefs.value[key] = el?.$el ? el.$el.querySelector('input') : el
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
  form.skillData.unshift({ name: '', percentage: 50 }) // tambah di ATAS
  keys.value.unshift(k)
  await nextTick()
  // auto-focus input nama baris baru
  nameRefs.value[k]?.focus?.()
}
function removeSkill(i: number) {
  if (form.skillData.length <= 1) return
  form.skillData.splice(i, 1)
  keys.value.splice(i, 1)
}

/* ============ Submit ============ */
async function onSubmit(_evt: FormSubmitEvent<Schema>) {
  try {
    submitting.value = true
    const payload = {
      title: form.title,
      description: form.description || '',
      icon: form.icon,
      skillData: form.skillData.map(s => ({
        name: (s.name || '').trim(),
        percentage: clamp(s.percentage)
      }))
    }
    const res = await skillStore.addPortfolioSkills(payload) // <== pake endpoint bener
    const ok = (res?.status ?? 'OK') === 'OK'

    if (ok) {
      toast.add({ title: 'Saved', description: 'Category created successfully.' })
      router.push('/dashboard/portfolio/skill')
    } else {
      toast.add({ color: 'error', title: 'Failed', description: res?.message?.[0] || 'Create failed' })
    }
  } catch (e: any) {
    toast.add({ color: 'error', title: 'Error', description: e?.message || 'Create failed' })
  } finally {
    submitting.value = false
  }
}

/* ============ Ring progress helpers ============ */
const RING_R = 26
const RING_C = 2 * Math.PI * RING_R
const ringDashOffset = (p: number) => ((100 - clamp(p)) / 100) * RING_C
const ringStrokeColor = (p: number) => {
  const v = clamp(p)
  if (v >= 80) return '#10b981'
  if (v >= 50) return '#f59e0b'
  return '#ef4444'
}
</script>

<template>
  <div class="space-y-4">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
      <h1 class="text-2xl font-semibold text-slate-800 dark:text-white">Create Skill Category</h1>
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
        <UButton type="submit" :loading="submitting" icon="i-heroicons-check-circle">Save</UButton>
        <UButton to="/dashboard/portfolio/skill" as="nuxt-link" variant="outline" color="gray" icon="i-heroicons-x-mark">
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
                  placeholder="Mis. Mobile, Backend, Frontend"
                  class="w-full"
                  :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
              />
            </UFormField>

            <UFormField label="Description" name="description">
              <UTextarea
                  v-model="form.description"
                  :rows="6"
                  class="w-full"
                  :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
                  placeholder="Deskripsi singkat kategori (opsional)"
              />
            </UFormField>

            <UFormField label="Icon" name="icon">
              <UTextarea
                  v-model="form.icon"
                  class="w-full"
                  :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
                  placeholder="i-heroicons-device-phone-mobile"
              />
            </UFormField>
          </div>
        </UCard>

        <!-- KANAN -->
        <UCard class="space-y-4">
          <!-- Header -->
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-2">
              <UIcon name="i-heroicons-sparkles" class="w-5 h-5 text-amber-500" />
              <div class="text-sm font-medium text-slate-800 dark:text-slate-200">Skills</div>
              <UBadge variant="subtle">{{ form.skillData.length }} item</UBadge>
            </div>
            <UButton size="xs" variant="outline" icon="i-heroicons-plus" @click="addSkill">Add Skill</UButton>
          </div>

          <div v-if="form.skillData.length === 0" class="text-xs text-gray-500">
            Belum ada skill. Klik “Add Skill” untuk menambah.
          </div>

          <!-- Items -->
          <div
              v-for="(s, i) in form.skillData"
              :key="keys[i]"
              class="relative group rounded-2xl border border-gray-200/70 dark:border-gray-700/70 bg-white/60 dark:bg-gray-900/60 backdrop-blur px-4 py-4 shadow-sm hover:shadow-md transition-shadow mt-6"
          >
            <!-- Reorder & Remove -->
            <div class="absolute top-2 right-2 flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
              <UButton size="xs" variant="ghost" icon="i-heroicons-arrow-up"  :disabled="i===0" @click="moveSkill(i,-1)" />
              <UButton size="xs" variant="ghost" icon="i-heroicons-arrow-down" :disabled="i===form.skillData.length-1" @click="moveSkill(i,1)" />
              <UButton size="xs" color="error" variant="ghost" icon="i-heroicons-trash"
                       :disabled="form.skillData.length <= 1" @click="removeSkill(i)" />
            </div>

            <div class="grid grid-cols-1 md:grid-cols-12 gap-4 items-center">
              <!-- Ring progress -->
              <div class="md:col-span-3 flex justify-center md:justify-start">
                <div class="relative w-[72px] h-[72px]">
                  <svg viewBox="0 0 64 64" class="w-[72px] h-[72px] -rotate-90">
                    <circle cx="32" cy="32" :r="RING_R" stroke-width="8" fill="none"
                            class="text-gray-200 dark:text-gray-800" stroke="currentColor" />
                    <circle cx="32" cy="32" :r="RING_R" stroke-width="8" stroke-linecap="round" fill="none"
                            class="transition-[stroke-dashoffset] duration-500"
                            :style="{ strokeDasharray: RING_C + 'px', strokeDashoffset: ringDashOffset(s.percentage) + 'px', stroke: ringStrokeColor(s.percentage) }" />
                  </svg>
                  <div class="absolute inset-0 flex items-center justify-center">
                    <span class="text-sm font-semibold text-slate-700 dark:text-slate-200">
                      {{ clamp(Number(s.percentage)) }}%
                    </span>
                  </div>
                </div>
              </div>

              <!-- Inputs -->
              <div class="md:col-span-9 space-y-3">
                <div>
                  <label class="block text-xs font-medium text-slate-600 dark:text-slate-300 mb-1">Skill Name</label>
                  <UInput
                      :ref="el => setNameRef(keys[i], el)"
                      v-model="s.name"
                      placeholder="Mis. Android, Flutter, Kotlin"
                      class="w-full"
                      :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
                  />
                  <div v-if="isDuplicateName(i)" class="mt-1 text-[11px] text-rose-500">
                    Nama skill duplikat dalam kategori ini.
                  </div>
                </div>

                <div>
                  <label class="block text-xs font-medium text-slate-600 dark:text-slate-300 mb-1">Percentage</label>
                  <div class="flex items-center gap-2">
                    <div class="relative flex-1">
                      <UInput
                          v-model="s.percentage"
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
                    <UButton size="xs" variant="soft" icon="i-heroicons-minus-small" @click="s.percentage = clamp((Number(s.percentage) || 0) - 10)" />
                    <UButton size="xs" variant="soft" icon="i-heroicons-plus-small"  @click="s.percentage = clamp((Number(s.percentage) || 0) + 10)" />
                  </div>

                  <div class="mt-3">
                    <input
                        v-model="s.percentage"
                        type="range"
                        min="0"
                        max="100"
                        step="1"
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

                  <div class="mt-3 h-3 w-full rounded-full bg-gray-200/70 dark:bg-gray-800/70 overflow-hidden">
                    <div
                        class="h-3 rounded-full transition-all duration-500"
                        :class="barClass(clamp(Number(s.percentage)))"
                        :style="{ width: clamp(Number(s.percentage)) + '%', background: gradientFor(s.percentage) }"
                    />
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
