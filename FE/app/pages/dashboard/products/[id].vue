<script setup lang="ts">
definePageMeta({ layout: 'dashboard' })

const route = useRoute()
const productId = route.params.id

// Dummy existing product
const product = reactive({
  name: 'MacBook Pro 14"',
  sku: 'MBP14-2023',
  price: 2999,
  stock: 12,
  description: 'The ultimate pro laptop with M3 chip.',
  published: true
})

function save() {
  console.log('Saving product', product)
  navigateTo('/dashboard/products/list')
}
</script>

<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-slate-800 dark:text-white">Edit Product #{{ productId }}</h1>
      <UButton to="/dashboard/products/list" color="gray" variant="ghost">
        Cancel
      </UButton>
    </div>

    <UCard class="max-w-2xl">
      <UForm :state="product" @submit="save" class="space-y-6">
        <UFormGroup label="Product Name" name="name">
          <UInput v-model="product.name" size="lg" />
        </UFormGroup>

        <UFormGroup label="SKU" name="sku">
          <UInput v-model="product.sku" size="lg" />
        </UFormGroup>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <UFormGroup label="Price ($)" name="price">
            <UInput v-model.number="product.price" type="number" min="0" step="0.01" size="lg" />
          </UFormGroup>

          <UFormGroup label="Stock" name="stock">
            <UInput v-model.number="product.stock" type="number" min="0" size="lg" />
          </UFormGroup>
        </div>

        <UFormGroup label="Description" name="description">
          <UTextarea v-model="product.description" rows="4" />
        </UFormGroup>

        <UFormGroup label="Published" name="published">
          <UToggle v-model="product.published" />
        </UFormGroup>

        <div class="flex justify-end">
          <UButton type="submit" color="indigo" size="lg">Save Changes</UButton>
        </div>
      </UForm>
    </UCard>
  </div>
</template>