<script setup lang="ts">
import { z } from 'zod'
import { useUserStore } from '~/stores/user'

definePageMeta({ layout: 'dashboard' })

const route = useRoute()
const toast = useToast()
const userStore = useUserStore()

const id = route.params.id as string

/** Opsi dropdown */
const roleOptions = ['USER', 'ADMIN', 'MANAGER', 'STAFF']
const statusOptions = ['ACTIVE', 'INACTIVE']

/** Skema validasi */
const schema = z.object({
  userName: z.string().min(3, 'Username minimal 3 karakter'),
  email: z.string().email('Format email tidak valid'),
  role: z.array(z.string()).nonempty('Pilih minimal 1 role'),
  status: z.enum(['ACTIVE', 'INACTIVE']),
  isVerified: z.boolean()
})

/** State */
const form = reactive({
  userName: '',
  email: '',
  role: ['USER'] as string[],
  status: 'ACTIVE',
  isVerified: false
})

const loading = ref(false)
const fetching = ref(true)

/** Ambil data user by id */
onMounted(async () => {
  try {
    fetching.value = true
    const res = await userStore.getUserById(id)
    if (!res?.data) throw new Error('User tidak ditemukan')
    Object.assign(form, {
      userName: res.data.userName ?? '',
      email: res.data.email ?? '',
      role: Array.isArray(res.data.role) ? res.data.role : [res.data.role ?? 'USER'],
      status: res.data.status ?? 'ACTIVE',
      isVerified: !!res.data.isVerified
    })
  } catch (e: unknown) {
    const msg = e instanceof Error ? e.message : 'Gagal memuat user'
    toast.add({ title: 'Error', description: msg, color: 'red' })
    navigateTo('/dashboard/users')
  } finally {
    fetching.value = false
  }
})

/** Submit */
async function onSubmit () {
  loading.value = true
  try {
    const parsed = schema.safeParse(form)
    if (!parsed.success) {
      const first = parsed.error.errors[0]
      toast.add({ title: 'Validasi gagal', description: first?.message || 'Periksa kembali form', color: 'error' })
      return
    }

    const res = await userStore.updateUser(id, form)
    if (res?.status === 'SUCCESS') {
      toast.add({ title: 'Tersimpan', description: res?.message?.[0] || 'Perubahan berhasil' })
      navigateTo('/dashboard/users')
    } else {
      toast.add({ title: 'Gagal', description: res?.message?.[0] || 'Tidak dapat menyimpan', color: 'error' })
    }
  } catch (e: unknown) {
    const msg = e instanceof Error ? e.message : 'Gagal menyimpan data'
    toast.add({ title: 'Error', description: msg, color: 'error' })
  } finally {
    loading.value = false
  }
}

/** style helper */
const inputUi = {
  base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 ' +
      'focus:border-primary-600 block w-full p-2.5 ' +
      'dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 ' +
      'dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500'
}
</script>

<template>
  <div>
    <h2 class="mb-4 text-2xl font-bold text-slate-800 dark:text-white">
      Edit User #{{ id }}
    </h2>

    <UCard v-if="!fetching">
      <UForm :state="form" :schema="schema" @submit="onSubmit">
        <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <!-- Username -->
          <UFormField label="Username" name="userName" class="sm:col-span-2">
            <UInput v-model="form.userName" placeholder="Input username" type="text" class="w-full" :ui="inputUi" />
          </UFormField>

          <!-- Email -->
          <UFormField label="Email" name="email" class="sm:col-span-2">
            <UInput v-model="form.email" placeholder="name@example.com" type="email" class="w-full" :ui="inputUi" />
          </UFormField>

          <!-- Role -->
          <UFormField label="Role" name="role" class="sm:col-span-2">
            <USelect v-model="form.role" multiple :items="roleOptions" class="w-full" :ui="inputUi" />
          </UFormField>

          <!-- Status -->
          <UFormField label="Status" name="status" class="sm:col-span-2">
            <USelect v-model="form.status" :items="statusOptions" class="w-full" :ui="inputUi" />
          </UFormField>

          <!-- Verified -->
          <div class="sm:col-span-2">
            <UCheckbox v-model="form.isVerified" label="Verified" name="isVerified" />
          </div>

          <!-- Actions -->
          <div class="sm:col-span-2 mt-2 flex gap-3">
            <UButton type="submit" :loading="loading">Save changes</UButton>
            <UButton to="/dashboard/users" color="gray" variant="outline">Cancel</UButton>
          </div>
        </div>
      </UForm>
    </UCard>

    <UCard v-else class="p-6">
      <USkeleton class="h-6 w-1/3 mb-4" />
      <USkeleton class="h-10 w-full mb-3" />
      <USkeleton class="h-10 w-full mb-3" />
      <USkeleton class="h-10 w-full mb-3" />
      <USkeleton class="h-10 w-1/2" />
    </UCard>
  </div>
</template>
