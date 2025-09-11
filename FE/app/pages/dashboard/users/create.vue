<script setup lang="ts">
import { z } from 'zod'
import { useUserStore } from '~/stores/user'

definePageMeta({ layout: 'dashboard' })

const userStore = useUserStore()
const toast = useToast()

/** 1) State */
const form = reactive({
  userName: '',
  email: '',
  password: '',
  confirmPassword: '',
  role: ['ADMIN'] as string[],
  status: 'INACTIVE',
  isVerified: false
})

/** 2) Opsi select */
const roles = ['USER', 'ADMIN', 'MANAGER', 'STAFF']
const statuses = ['ACTIVE', 'INACTIVE']

/** 3) Validasi (Zod) */
const schema = z.object({
  userName: z.string().min(3, 'Username minimal 3 karakter'),
  email: z.string().email('Format email tidak valid'),
  password: z.string().min(6, 'Password minimal 6 karakter'),
  confirmPassword: z.string(),
  role: z.array(z.string()).nonempty('Pilih minimal 1 role'),
  status: z.enum(['ACTIVE', 'INACTIVE']),
  isVerified: z.boolean()
}).refine(v => v.password === v.confirmPassword, {
  path: ['confirmPassword'],
  message: 'Password tidak cocok'
})

/** 4) Helper error konfirmasi */
const passwordMismatch = computed(
    () => !!form.password && !!form.confirmPassword && form.password !== form.confirmPassword
)

/** 5) Submit */
const loading = ref(false)

async function onSubmit () {
  try {
    loading.value = true
    // Validasi manual (jaga-jaga jika user tidak memakai validasi UForm)
    const parsed = schema.safeParse(form)
    if (!parsed.success) {
      // Tampilkan error pertama yang paling berarti
      const first = parsed.error.errors[0]
      toast.add({ title: 'Validasi gagal', description: first?.message || 'Periksa kembali form', color: 'error' })
      return
    }

    const res = await userStore.addUser({
      userName: form.userName,
      email: form.email,
      password: form.password,
      confirmPassword: form.confirmPassword,
      role: form.role,
      status: form.status,
      isVerified: form.isVerified
    })

    if (res?.status === 'SUCCESS') {
      toast.add({ title: 'Berhasil', description: res?.message?.[0] || 'User berhasil dibuat', color: 'success' })
      navigateTo('/dashboard/users')
    } else {
      toast.add({ title: 'Gagal', description: res?.message?.[0] || 'Proses gagal', color: 'error' })
    }
  } catch (e: unknown) {
    const error = e instanceof Error ? e.message : 'Gagal memproses data'
    toast.add({ title: 'Error', description: error, color: 'error' })
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div>
    <h2 class="text-2xl font-bold mb-4 text-slate-800 dark:text-white">
      Create New User
    </h2>

    <!-- Layout responsif: 1 kolom di mobile, 2 kolom mulai lg -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">
      <UForm :state="form" :schema="schema" @submit="onSubmit">
        <UCard class="space-y-4">
          <!-- grid internal: 1 kolom di mobile, 2 kolom di sm+ -->
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <!-- Username (full width) -->
            <UFormField
                label="Username"
                name="userName"
                class="sm:col-span-2"
            >
              <UInput
                  v-model="form.userName"
                  placeholder="Input username"
                  type="text"
                  class="w-full"
                  :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
              />
            </UFormField>

            <!-- Email (full width) -->
            <UFormField
                label="Email"
                name="email"
                class="sm:col-span-2"
            >
              <UInput
                  v-model="form.email"
                  placeholder="name@example.com"
                  type="email"
                  class="w-full"
                  :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
              />
            </UFormField>

            <!-- Password (setengah di sm+, full di mobile) -->
            <UFormField
                label="Password"
                name="password"
                class="sm:col-span-1"
            >
              <UInput
                  v-model="form.password"
                  placeholder="••••••••"
                  type="password"
                  class="w-full"
                  :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
              />
            </UFormField>

            <!-- Confirm Password -->
            <UFormField
                label="Confirm Password"
                name="confirmPassword"
                :error="passwordMismatch && 'Password tidak cocok'"
                class="sm:col-span-1"
            >
              <UInput
                  v-model="form.confirmPassword"
                  placeholder="••••••••"
                  type="password"
                  class="w-full"
                  :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
              />
            </UFormField>

            <!-- Role (full width) -->
            <UFormField
                label="Role"
                name="role"
                class="sm:col-span-2"
            >
              <USelect
                  v-model="form.role"
                  multiple
                  :items="roles"
                  class="w-full"
                  :ui="{ base: 'text-left bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
              />
            </UFormField>

            <!-- Status (full width) -->
            <UFormField
                label="Status"
                name="status"
                class="sm:col-span-2"
            >
              <USelect
                  v-model="form.status"
                  :items="statuses"
                  class="w-full"
                  :ui="{ base: 'text-left bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
              />
            </UFormField>

            <!-- Verified (full width) -->
            <div class="sm:col-span-2">
              <UCheckbox
                  v-model="form.isVerified"
                  label="Verified"
                  name="isVerified"
                  class="text-sm font-medium text-gray-900 dark:text-white"
              />
            </div>

            <!-- Actions (full width) -->
            <div class="sm:col-span-2 mt-2 flex flex-col sm:flex-row gap-3">
              <UButton
                  type="submit"
                  label="Save"
                  :loading="loading"
                  :disabled="loading"
              />
              <UButton
                  to="/dashboard/users"
                  label="Cancel"
                  color="tertiary"
                  variant="outline"
              />
            </div>
          </div>
        </UCard>
      </UForm>

      <!-- Preview: tampilkan di lg ke atas, tetap ada di mobile namun di bawah -->
      <div class="relative mx-0 lg:mx-6">
        <div class="w-full min-h-40 p-6 bg-gray-100 dark:bg-gray-700 rounded flex items-center justify-center">
          <span class="text-gray-400 dark:text-gray-300">Preview area</span>
        </div>
      </div>
    </div>
  </div>
</template>
