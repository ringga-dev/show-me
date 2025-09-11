<script setup lang="ts">
definePageMeta({ layout: 'dashboard' })

const form = reactive({
  name: '',
  sku: '',
  price: 0,
  stock: 0,
  image: ""
})


/* -------------------------------------------------
   Image upload helpers
-------------------------------------------------- */
const imageInput = ref<HTMLInputElement>()
const preview     = ref<string>()      // untuk pratinjau

function openFilePicker () {
  imageInput.value?.click()
}

function onFileChange (e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return

  const reader = new FileReader()
  reader.onload = () => {
    preview.value   = reader.result as string
    form.image      = preview.value
  }
  reader.readAsDataURL(file)
}

function removeImage () {
  preview.value = undefined
  form.image    = ''
  if (imageInput.value) imageInput.value.value = ''
}

function onSubmit () {
  console.log('create product', form)
}
</script>

<template>
  <div>
    <h2 class="text-2xl font-bold mb-4 text-slate-800 dark:text-white">Create Product</h2>
    <div class="grid grid-cols-2 h-screen gap-4">
      <UForm :state="form" @submit="onSubmit" class="">
        <UCard class="space-y-4">

          <div class="grid grid-cols-1 md:col-span-2 gap-4">

            <!-- Image -->
            <UFormField label="Image" name="image">
              <div class="flex items-start gap-4">
                <!-- Tombol pilih file -->
                <UButton type="button" variant="outline" @click="openFilePicker">
                  <UIcon name="i-heroicons-arrow-up-tray" class="w-4 h-4 mr-2" />
                  Choose file
                </UButton>

                <!-- Input tersembunyi -->
                <input
                    ref="imageInput"
                    type="file"
                    accept="image/*"
                    class="hidden"
                    @change="onFileChange"
                >


              </div>
            </UFormField>

            <UFormField label="Name" name="name"
                        class="block mb-2 text-sm font-medium text-gray-900 dark:text-white md:col-span-2">
              <UInput
                  v-model="form.name"
                  placeholder="Input name"
                  class="w-full"
                  type="text"
                  :ui="{
                  base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500'
                }"
              />
            </UFormField>


            <UFormField label="Price" name="price"
                        class="block mb-2 text-sm font-medium text-gray-900 dark:text-white md:col-span-2">
              <UInput
                  v-model="form.price"
                  placeholder="Input price"
                  class="w-full"
                  type="number"
                  :ui="{
                  base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500'
                }"
              />
            </UFormField>

            <UFormField label="Stock" name="stock"
                        class="block mb-2 text-sm font-medium text-gray-900 dark:text-white md:col-span-2">
              <UInput
                  v-model="form.stock"
                  placeholder="Input stock"
                  class="w-full"
                  type="number"
                  :ui="{
                  base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500'
                }"
              />
            </UFormField>

            <UFormField label="Sku" name="sku"
                        class="block mb-2 text-sm font-medium text-gray-900 dark:text-white md:col-span-2">
              <UInput
                  v-model="form.sku"
                  placeholder="Input stock"
                  class="w-full"
                  type="text"
                  :ui="{
                  base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500'
                }"
              />
            </UFormField>

            <div class="mt-6 flex gap-3">
              <UButton type="submit" label="Save" />
              <UButton to="/products/list" label="Cancel" color="gray" variant="outline" />
            </div>
          </div>

        </UCard>
      </UForm>

      <!-- Pratinjau -->
      <div v-if="preview" class="relative mx-6">
        <img :src="preview" class="w-full rounded object-cover">
        <UButton
            icon="i-heroicons-x-mark"
            color="red"
            variant="ghost"
            class="absolute -top-2 -right-2 bg-red-300/50 hover:bg-red-500 focus:bg-red-500 rounded-full"
            @click="removeImage"
        />
      </div>
    </div>

  </div>
</template>