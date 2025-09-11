<script setup lang="ts">
definePageMeta({layout: 'dashboard'})

/* ---------- dummy data (10 items) ---------- */
const products = ref([
  {id: 1, name: 'MacBook Pro 14"', sku: 'MBP14-2023', price: 2999, stock: 12, image: 'https://placehold.co/80x80'},
  {id: 2, name: 'iPhone 15 Pro', sku: 'IP15P-128', price: 1199, stock: 34, image: 'https://placehold.co/80x80'},
  {id: 3, name: 'AirPods Pro', sku: 'APP-2ND', price: 249, stock: 0, image: 'https://placehold.co/80x80'},
  {id: 4, name: 'iPad Air', sku: 'IPA-5TH', price: 599, stock: 8, image: 'https://placehold.co/80x80'},
  {id: 5, name: 'Apple Watch', sku: 'AW-SE', price: 279, stock: 15, image: 'https://placehold.co/80x80'},
  {id: 6, name: 'Magic Mouse', sku: 'MM-3RD', price: 99, stock: 25, image: 'https://placehold.co/80x80'},
  {id: 7, name: 'AirTag', sku: 'AT-4PK', price: 99, stock: 50, image: 'https://placehold.co/80x80'},
  {id: 8, name: 'HomePod mini', sku: 'HPM-WHT', price: 99, stock: 20, image: 'https://placehold.co/80x80'},
  {id: 9, name: 'USB-C Cable', sku: 'USBC-2M', price: 19, stock: 100, image: 'https://placehold.co/80x80'},
  {id: 10, name: 'MagSafe Charger', sku: 'MS-CHRG', price: 39, stock: 60, image: 'https://placehold.co/80x80'}, {
    id: 11,
    name: 'MacBook Pro 14"',
    sku: 'MBP14-2023',
    price: 2999,
    stock: 12,
    image: 'https://placehold.co/80x80'
  },
  {id: 12, name: 'iPhone 15 Pro', sku: 'IP15P-128', price: 1199, stock: 34, image: 'https://placehold.co/80x80'},
  {id: 13, name: 'AirPods Pro', sku: 'APP-2ND', price: 249, stock: 0, image: 'https://placehold.co/80x80'},
  {id: 14, name: 'iPad Air', sku: 'IPA-5TH', price: 599, stock: 8, image: 'https://placehold.co/80x80'},
  {id: 15, name: 'Apple Watch', sku: 'AW-SE', price: 279, stock: 15, image: 'https://placehold.co/80x80'},
  {id: 16, name: 'Magic Mouse', sku: 'MM-3RD', price: 99, stock: 25, image: 'https://placehold.co/80x80'},
  {id: 17, name: 'AirTag', sku: 'AT-4PK', price: 99, stock: 50, image: 'https://placehold.co/80x80'},
  {id: 18, name: 'HomePod mini', sku: 'HPM-WHT', price: 99, stock: 20, image: 'https://placehold.co/80x80'},
  {id: 19, name: 'USB-C Cable', sku: 'USBC-2M', price: 19, stock: 100, image: 'https://placehold.co/80x80'},
  {id: 20, name: 'MagSafe Charger', sku: 'MS-CHRG', price: 39, stock: 60, image: 'https://placehold.co/80x80'},
  {id: 21, name: 'MacBook Pro 14"', sku: 'MBP14-2023', price: 2999, stock: 12, image: 'https://placehold.co/80x80'},
  {id: 22, name: 'iPhone 15 Pro', sku: 'IP15P-128', price: 1199, stock: 34, image: 'https://placehold.co/80x80'},
  {id: 23, name: 'AirPods Pro', sku: 'APP-2ND', price: 249, stock: 0, image: 'https://placehold.co/80x80'},
  {id: 24, name: 'iPad Air', sku: 'IPA-5TH', price: 599, stock: 8, image: 'https://placehold.co/80x80'},
  {id: 25, name: 'Apple Watch', sku: 'AW-SE', price: 279, stock: 15, image: 'https://placehold.co/80x80'},
  {id: 26, name: 'Magic Mouse', sku: 'MM-3RD', price: 99, stock: 25, image: 'https://placehold.co/80x80'},
  {id: 27, name: 'AirTag', sku: 'AT-4PK', price: 99, stock: 50, image: 'https://placehold.co/80x80'},
  {id: 28, name: 'HomePod mini', sku: 'HPM-WHT', price: 99, stock: 20, image: 'https://placehold.co/80x80'},
  {id: 29, name: 'USB-C Cable', sku: 'USBC-2M', price: 19, stock: 100, image: 'https://placehold.co/80x80'},
  {id: 30, name: 'MagSafe Charger', sku: 'MS-CHRG', price: 39, stock: 60, image: 'https://placehold.co/80x80'}, {
    id: 31,
    name: 'MacBook Pro 14"',
    sku: 'MBP14-2023',
    price: 2999,
    stock: 12,
    image: 'https://placehold.co/80x80'
  },
  {id: 32, name: 'iPhone 15 Pro', sku: 'IP15P-128', price: 1199, stock: 34, image: 'https://placehold.co/80x80'},
  {id: 33, name: 'AirPods Pro', sku: 'APP-2ND', price: 249, stock: 0, image: 'https://placehold.co/80x80'},
  {id: 34, name: 'iPad Air', sku: 'IPA-5TH', price: 599, stock: 8, image: 'https://placehold.co/80x80'},
  {id: 35, name: 'Apple Watch', sku: 'AW-SE', price: 279, stock: 15, image: 'https://placehold.co/80x80'},
  {id: 36, name: 'Magic Mouse', sku: 'MM-3RD', price: 99, stock: 25, image: 'https://placehold.co/80x80'},
  {id: 37, name: 'AirTag', sku: 'AT-4PK', price: 99, stock: 50, image: 'https://placehold.co/80x80'},
  {id: 38, name: 'HomePod mini', sku: 'HPM-WHT', price: 99, stock: 20, image: 'https://placehold.co/80x80'},
  {id: 39, name: 'USB-C Cable', sku: 'USBC-2M', price: 19, stock: 100, image: 'https://placehold.co/80x80'},
  {id: 40, name: 'MagSafe Charger', sku: 'MS-CHRG', price: 39, stock: 60, image: 'https://placehold.co/80x80'}, {
    id: 41,
    name: 'MacBook Pro 14"',
    sku: 'MBP14-2023',
    price: 2999,
    stock: 12,
    image: 'https://placehold.co/80x80'
  },
  {id: 42, name: 'iPhone 15 Pro', sku: 'IP15P-128', price: 1199, stock: 34, image: 'https://placehold.co/80x80'},
  {id: 43, name: 'AirPods Pro', sku: 'APP-2ND', price: 249, stock: 0, image: 'https://placehold.co/80x80'},
  {id: 44, name: 'iPad Air', sku: 'IPA-5TH', price: 599, stock: 8, image: 'https://placehold.co/80x80'},
  {id: 45, name: 'Apple Watch', sku: 'AW-SE', price: 279, stock: 15, image: 'https://placehold.co/80x80'},
  {id: 46, name: 'Magic Mouse', sku: 'MM-3RD', price: 99, stock: 25, image: 'https://placehold.co/80x80'},
  {id: 47, name: 'AirTag', sku: 'AT-4PK', price: 99, stock: 50, image: 'https://placehold.co/80x80'},
  {id: 48, name: 'HomePod mini', sku: 'HPM-WHT', price: 99, stock: 20, image: 'https://placehold.co/80x80'},
  {id: 49, name: 'USB-C Cable', sku: 'USBC-2M', price: 19, stock: 100, image: 'https://placehold.co/80x80'},
  {id: 50, name: 'MagSafe Charger', sku: 'MS-CHRG', price: 39, stock: 60, image: 'https://placehold.co/80x80'}, {
    id: 51,
    name: 'MacBook Pro 14"',
    sku: 'MBP14-2023',
    price: 2999,
    stock: 12,
    image: 'https://placehold.co/80x80'
  },
  {id: 52, name: 'iPhone 15 Pro', sku: 'IP15P-128', price: 1199, stock: 34, image: 'https://placehold.co/80x80'},
  {id: 53, name: 'AirPods Pro', sku: 'APP-2ND', price: 249, stock: 0, image: 'https://placehold.co/80x80'},
  {id: 54, name: 'iPad Air', sku: 'IPA-5TH', price: 599, stock: 8, image: 'https://placehold.co/80x80'},
  {id: 55, name: 'Apple Watch', sku: 'AW-SE', price: 279, stock: 15, image: 'https://placehold.co/80x80'},
  {id: 56, name: 'Magic Mouse', sku: 'MM-3RD', price: 99, stock: 25, image: 'https://placehold.co/80x80'},
  {id: 57, name: 'AirTag', sku: 'AT-4PK', price: 99, stock: 50, image: 'https://placehold.co/80x80'},
  {id: 58, name: 'HomePod mini', sku: 'HPM-WHT', price: 99, stock: 20, image: 'https://placehold.co/80x80'},
  {id: 59, name: 'USB-C Cable', sku: 'USBC-2M', price: 19, stock: 100, image: 'https://placehold.co/80x80'},
  {id: 60, name: 'MagSafe Charger', sku: 'MS-CHRG', price: 39, stock: 60, image: 'https://placehold.co/80x80'}, {
    id: 61,
    name: 'MacBook Pro 14"',
    sku: 'MBP14-2023',
    price: 2999,
    stock: 12,
    image: 'https://placehold.co/80x80'
  },
  {id: 62, name: 'iPhone 15 Pro', sku: 'IP15P-128', price: 1199, stock: 34, image: 'https://placehold.co/80x80'},
  {id: 63, name: 'AirPods Pro', sku: 'APP-2ND', price: 249, stock: 0, image: 'https://placehold.co/80x80'},
  {id: 64, name: 'iPad Air', sku: 'IPA-5TH', price: 599, stock: 8, image: 'https://placehold.co/80x80'},
  {id: 65, name: 'Apple Watch', sku: 'AW-SE', price: 279, stock: 15, image: 'https://placehold.co/80x80'},
  {id: 66, name: 'Magic Mouse', sku: 'MM-3RD', price: 99, stock: 25, image: 'https://placehold.co/80x80'},
  {id: 67, name: 'AirTag', sku: 'AT-4PK', price: 99, stock: 50, image: 'https://placehold.co/80x80'},
  {id: 68, name: 'HomePod mini', sku: 'HPM-WHT', price: 99, stock: 20, image: 'https://placehold.co/80x80'},
  {id: 69, name: 'USB-C Cable', sku: 'USBC-2M', price: 19, stock: 100, image: 'https://placehold.co/80x80'},
])

/* ---------- search & pagination ---------- */
const page = ref(1)
const pageSize = 6
const q = ref('')

const filtered = computed(() =>
    products.value.filter(p =>
        p.name.toLowerCase().includes(q.value.toLowerCase()) ||
        p.sku.toLowerCase().includes(q.value.toLowerCase())
    )
)

const paginated = computed(() => {
  const start = (page.value - 1) * pageSize
  return filtered.value.slice(start, start + pageSize)
})

watch(q, () => {
  page.value = 1
})
</script>

<template>
  <div>
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-slate-800 dark:text-white">Products</h1>
      <UButton
          to="/dashboard/products/create"
          icon="i-heroicons-plus"
          color="indigo"
          as="nuxt-link"
      >
        Add Product
      </UButton>
    </div>

    <!-- Search -->
    <UInput
        v-model="q"
        placeholder="Search products..."
        icon="i-heroicons-magnifying-glass"
        class="mb-4 max-w-xs"
    />

    <!-- Product Table -->
    <div class="overflow-x-auto">
      <div class="hidden md:block">
        <UCard :ui="{ body: { padding: '' } }">
          <table class="min-w-full text-sm">
            <thead class="border-b border-slate-200 dark:border-slate-700">
            <tr>
              <th class="text-left p-4">Product</th>
              <th class="text-left p-4">SKU</th>
              <th class="text-left p-4">Price</th>
              <th class="text-left p-4">Stock</th>
              <th class="text-right p-4">Actions</th>
            </tr>
            </thead>
            <tbody>
            <tr
                v-for="p in paginated"
                :key="p.id"
                class="border-b border-slate-100 dark:border-slate-800 hover:bg-slate-50 dark:hover:bg-slate-800/50 transition"
            >
              <td class="p-4 flex items-center gap-3">
                <img :src="p.image" class="w-10 h-10 rounded object-cover"/>
                <span class="font-medium text-slate-800 dark:text-white">{{ p.name }}</span>
              </td>
              <td class="p-4 text-slate-600 dark:text-slate-300">{{ p.sku }}</td>
              <td class="p-4">${{ p.price }}</td>
              <td class="p-4">
                <UBadge :color="p.stock ? 'green' : 'red'" variant="subtle">
                  {{ p.stock }} left
                </UBadge>
              </td>
              <td class="p-4 text-right">
                <UButton
                    :to="`/dashboard/products/${p.id}`"
                    icon="i-heroicons-pencil-square"
                    color="gray"
                    variant="ghost"
                    size="sm"
                    as="nuxt-link"
                />
              </td>
            </tr>
            </tbody>
            <tfoot v-if="paginated.length === 0">
            <tr>
              <td colspan="5" class="p-8 text-center text-slate-500 dark:text-slate-400">
                No products found.
              </td>
            </tr>
            </tfoot>
          </table>
        </UCard>
      </div>

      <!-- Mobile Cards -->
      <div class="md:hidden space-y-3">
        <UCard v-for="p in paginated" :key="p.id" class="p-4">
          <div class="flex items-center gap-3 mb-3">
            <img :src="p.image" class="w-12 h-12 rounded object-cover"/>
            <div>
              <div class="font-medium text-slate-800 dark:text-white">{{ p.name }}</div>
              <div class="text-xs text-slate-500 dark:text-slate-400">{{ p.sku }}</div>
            </div>
          </div>

          <div class="grid grid-cols-2 gap-2 text-sm">
            <div>
              <span class="text-slate-500">Price:</span>
              <span class="ml-1">${{ p.price }}</span>
            </div>
            <div>
              <span class="text-slate-500">Stock:</span>
              <UBadge :color="p.stock ? 'green' : 'red'" variant="subtle" size="xs" class="ml-1">
                {{ p.stock }} left
              </UBadge>
            </div>
          </div>

          <div class="flex justify-end mt-3">
            <UButton
                :to="`/dashboard/products/edit/${p.id}`"
                icon="i-heroicons-pencil-square"
                color="gray"
                size="sm"
                as="nuxt-link"
            >
              Edit
            </UButton>
          </div>
        </UCard>

        <!-- No products -->
        <UCard v-if="paginated.length === 0" class="text-center py-6 text-slate-500 dark:text-slate-400">
          No products found.
        </UCard>
      </div>

      <!-- Pagination -->
      <div class="flex justify-end p-4 gap-3">

        <UPagination
            v-model:page="page" :items-per-page="pageSize" :total="filtered.length"
        />

      </div>
    </div>
  </div>
</template>
