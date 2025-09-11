<script setup lang="ts">
import {CalendarDate} from '@internationalized/date';
const toast = useToast()
definePageMeta({ layout: 'dashboard' })

/* ------------------ Dummy Data ------------------ */
const stats = [
  { label: 'Revenue', value: 12845, icon: 'i-heroicons-banknotes', goal: 20000 },
  { label: 'Orders', value: 1324, icon: 'i-heroicons-shopping-cart', goal: 2000 },
  { label: 'Users', value: 657, icon: 'i-heroicons-users', goal: 1000 },
  { label: 'Page Views', value: 98123, icon: 'i-heroicons-eye', goal: 150000 }
]



const recentUsers = [
  { id: 1, name: 'Alice', email: 'alice@mail.com', avatar: 'https://i.pravatar.cc/40?u=1' },
  { id: 2, name: 'Bob', email: 'bob@mail.com', avatar: 'https://i.pravatar.cc/40?u=2' },
  { id: 3, name: 'Charlie', email: 'charlie@mail.com', avatar: 'https://i.pravatar.cc/40?u=3' }
]

const blogPosts = [
  { id: 1, title: 'How to scale with Nuxt', views: 3245 },
  { id: 2, title: 'Understanding SSR vs CSR', views: 1892 },
  { id: 3, title: 'Top 10 VS Code Extensions', views: 4533 }
]

const apiTokens = [
  { id: 1, name: 'Production', lastUsed: '2h ago', status: 'Active' },
  { id: 2, name: 'Staging', lastUsed: '1d ago', status: 'Active' },
  { id: 3, name: 'Legacy', lastUsed: '2w ago', status: 'Expired' }
]

const value = ref([
  new CalendarDate(2022, 2, 4),
  new CalendarDate(2022, 2, 6),
  new CalendarDate(2022, 2, 8)
])

// onMounted(() => {
//   const toast = useToast()
//   toast.add({
//     title: 'Success',
//     description: 'Welcome back!\nringga.dev.',
//     color: 'success'
//   })
// })
</script>

<template>
  <div >



    <!-- Stat Cards -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
      <UCard v-for="stat in stats" :key="stat.label"
             class="bg-white/60 dark:bg-slate-800/60 backdrop-blur-xl border-slate-200/50 dark:border-slate-700/50">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-slate-500 dark:text-slate-400">{{ stat.label }}</p>
            <p class="text-2xl font-bold text-slate-800 dark:text-white mt-1">
              {{ stat.label === 'Revenue' ? '$' : '' }}{{ stat.value.toLocaleString() }}
            </p>
          </div>
          <div class="p-3 rounded-xl bg-green-200 dark:bg-green-600 flex items-center justify-center">
            <UIcon :name="stat.icon" class="w-6 h-6 text-gray-400 dark:text-white"/>
          </div>
        </div>
        <UProgress v-model:="stat.value" :max="stat.goal" color="primary" class="mt-3" />
        <p class="text-xs text-white mt-1">{{ Math.round((stat.value/stat.goal)*100) }}% of goal</p>
      </UCard>
    </div>

    <!-- Grid 2 kolom utama -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Kolom Kiri (lebih luas) -->
      <div class="lg:col-span-2 space-y-6">

        <!-- Blog Posts -->
        <UCard>
          <template #header>
            <div class="flex justify-between items-center">
              <h3 class="font-semibold text-slate-800 dark:text-white">Recent Blog Posts</h3>
              <UButton label="Write" icon="i-heroicons-pencil-square" size="xs" />
            </div>
          </template>
          <ul class="space-y-3">
            <li v-for="post in blogPosts" :key="post.id"
                class="flex justify-between items-center text-sm">
              <span class="text-slate-700 dark:text-slate-200">{{ post.title }}</span>
              <span class="text-slate-500 dark:text-slate-400">{{ post.views }} views</span>
            </li>
          </ul>
        </UCard>
      </div>

      <!-- Kolom Kanan -->
      <div class="space-y-6">
        <!-- Calendar Mini -->
        <UCard>
          <template #header>
            <h3 class="font-semibold text-slate-800 dark:text-white">Calendar</h3>
          </template>
          <UCalendar multiple :v-model="value" />
        </UCard>

        <!-- Recent Users -->
        <UCard>
          <template #header>
            <h3 class="font-semibold text-slate-800 dark:text-white">Recent Users</h3>
          </template>
          <ul class="space-y-3">
            <li v-for="u in recentUsers" :key="u.id" class="flex items-center gap-3">
              <UAvatar :src="u.avatar" size="xs" />
              <div>
                <p class="text-sm font-medium text-slate-700 dark:text-slate-200">{{ u.name }}</p>
                <p class="text-xs text-slate-500 dark:text-slate-400">{{ u.email }}</p>
              </div>
            </li>
          </ul>
        </UCard>

        <!-- API Tokens -->
        <UCard>
          <template #header>
            <h3 class="font-semibold text-slate-800 dark:text-white">API Tokens</h3>
          </template>
          <ul class="space-y-2">
            <li v-for="token in apiTokens" :key="token.id"
                class="flex justify-between items-center text-sm">
              <span>{{ token.name }}</span>
              <UBadge :color="token.status === 'Active' ? 'green' : 'gray'" size="xs">
                {{ token.status }}
              </UBadge>
            </li>
          </ul>
        </UCard>

        <!-- Mini Chart -->
        <UCard>
          <template #header>
            <h3 class="font-semibold text-slate-800 dark:text-white">Page Views Trend</h3>
          </template>
          <div class="h-32">
            <svg viewBox="0 0 100 40" preserveAspectRatio="none" class="w-full h-full">
              <polyline
                  points="0,40 10,35 20,20 30,25 40,15 50,10 60,12 70,5 80,8 90,3 100,6"
                  class="fill-none stroke-indigo-500 stroke-2"/>
            </svg>
          </div>
        </UCard>
      </div>
    </div>
  </div>
</template>