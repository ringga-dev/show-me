<template>
  <div
      class="flex h-screen bg-gradient-to-br
           from-slate-50 via-slate-100 to-slate-50
           dark:from-slate-900 dark:via-slate-800 dark:to-slate-900"
  >
    <!-- Mobile overlay -->
    <Transition name="fade">
      <div
          v-if="sidebarOpen"
          class="fixed inset-0 bg-black/50 z-20 md:hidden"
          @click="sidebarOpen = false"
      />
    </Transition>

    <!-- Sidebar -->
    <aside
        class="fixed md:sticky top-0 left-0 h-full w-64
             bg-white/60 dark:bg-slate-900/60 backdrop-blur-xl
             border-r border-slate-200/50 dark:border-slate-700/50
             flex flex-col transition-transform duration-300 z-30
             md:translate-x-0"
        :class="sidebarOpen ? 'translate-x-0' : '-translate-x-full'"
    >
      <!-- Logo + close (mobile) -->
      <div class="p-4 flex items-center justify-between">
        <h1 class="text-xl font-bold text-slate-800 dark:text-white">
          Ringga<span class="text-primary-500">.dev</span>
        </h1>
        <UButton
            icon="i-heroicons-x-mark"
            variant="ghost"
            class="md:hidden text-gray-700"
            @click="sidebarOpen = false"
        />
      </div>

      <!-- Nav -->
      <nav class="flex-1 px-3 space-y-1 text-sm overflow-y-auto">
        <!-- Dashboard -->
        <NuxtLink
            to="/dashboard"
            :class="linkClass('/dashboard', ['/dashboard/users', '/dashboard/products'])"
        >
          <UIcon name="i-heroicons-home" class="w-5 h-5" />
          Dashboard
        </NuxtLink>

        <!-- Users -->
        <NuxtLink
            to="/dashboard/users"
            :class="linkClass('/dashboard/users')"
        >
          <UIcon name="i-heroicons-users" class="w-5 h-5" />
          Users
        </NuxtLink>

        <!-- Products accordion -->
        <div>
          <button
              @click="openPortfolio = !openPortfolio"
              :class="[
              'w-full flex items-center justify-between gap-3 px-3 py-2 rounded-lg',
              isActive('/dashboard/portfolio')
                ? 'bg-primary-500 text-white shadow'
                : 'text-slate-600 dark:text-slate-300 hover:bg-primary-500/10'
            ]"
          >
            <div class="flex items-center gap-3">
              <UIcon name="i-heroicons-archive-box" class="w-5 h-5" />
              Portfolio
            </div>
            <UIcon
                :name="openPortfolio ? 'i-heroicons-chevron-up' : 'i-heroicons-chevron-down'"
                class="w-4 h-4"
            />
          </button>

          <Transition name="accordion">
            <div v-show="openPortfolio" class="pl-6 mt-1 space-y-1">
              <NuxtLink
                  to="/dashboard/portfolio/details"
                  :class="subLinkClass('/dashboard/portfolio/details')"
              >
                Details
              </NuxtLink>
              <NuxtLink
                  to="/dashboard/portfolio/experience"
                  :class="subLinkClass('/dashboard/portfolio/experience')"
              >
                Experience
              </NuxtLink>

              <NuxtLink
                  to="/dashboard/portfolio/project"
                  :class="subLinkClass('/dashboard/portfolio/project')"
              >
                Project
              </NuxtLink>

              <NuxtLink
                  to="/dashboard/portfolio/skill"
                  :class="subLinkClass('/dashboard/portfolio/skill')"
              >
                Skill
              </NuxtLink>
            </div>
          </Transition>
        </div>

        <!-- Products accordion -->
<!--        <div>-->
<!--          <button-->
<!--              @click="openProducts = !openProducts"-->
<!--              :class="[-->
<!--              'w-full flex items-center justify-between gap-3 px-3 py-2 rounded-lg',-->
<!--              isActive('/dashboard/products')-->
<!--                ? 'bg-primary-500 text-white shadow'-->
<!--                : 'text-slate-600 dark:text-slate-300 hover:bg-primary-500/10'-->
<!--            ]"-->
<!--          >-->
<!--            <div class="flex items-center gap-3">-->
<!--              <UIcon name="i-heroicons-cube" class="w-5 h-5" />-->
<!--              Products-->
<!--            </div>-->
<!--            <UIcon-->
<!--                :name="openProducts ? 'i-heroicons-chevron-up' : 'i-heroicons-chevron-down'"-->
<!--                class="w-4 h-4"-->
<!--            />-->
<!--          </button>-->
<!--          <Transition name="accordion">-->
<!--            <div v-show="openProducts" class="pl-6 mt-1 space-y-1">-->
<!--              <NuxtLink-->
<!--                  to="/dashboard/products/list"-->
<!--                  :class="subLinkClass('/dashboard/products/list')"-->
<!--              >-->
<!--                List-->
<!--              </NuxtLink>-->
<!--              <NuxtLink-->
<!--                  to="/dashboard/products/create"-->
<!--                  :class="subLinkClass('/dashboard/products/create')"-->
<!--              >-->
<!--                Create-->
<!--              </NuxtLink>-->
<!--            </div>-->
<!--          </Transition>-->
<!--        </div>-->

        <!-- Blog -->
        <div>
          <button
              @click="openBlogs = !openBlogs"
              :class="[
              'w-full flex items-center justify-between gap-3 px-3 py-2 rounded-lg',
              isActive('/dashboard/blogs')
                ? 'bg-primary-500 text-white shadow'
                : 'text-slate-600 dark:text-slate-300 hover:bg-primary-500/10'
            ]"
          >
            <div class="flex items-center gap-3">
              <UIcon name="cib:blogger-b" class="w-5 h-5" />
              Blogs
            </div>
            <UIcon
                :name="openBlogs ? 'i-heroicons-chevron-up' : 'i-heroicons-chevron-down'"
                class="w-4 h-4"
            />
          </button>
          <Transition name="accordion">
            <div v-show="openBlogs" class="pl-6 mt-1 space-y-1">
              <NuxtLink
                  to="/dashboard/blogs/list"
                  :class="subLinkClass('/dashboard/blogs/list')"
              >
                List
              </NuxtLink>
              <NuxtLink
                  to="/dashboard/blogs/create"
                  :class="subLinkClass('/dashboard/blogs/create')"
              >
                Create
              </NuxtLink>
            </div>
          </Transition>
        </div>

        <!-- Category -->
<!--        <div>-->
<!--          <button-->
<!--              @click="openCategory = !openCategory"-->
<!--              :class="[-->
<!--              'w-full flex items-center justify-between gap-3 px-3 py-2 rounded-lg',-->
<!--              isActive('/dashboard/categories')-->
<!--                ? 'bg-primary-500 text-white shadow'-->
<!--                : 'text-slate-600 dark:text-slate-300 hover:bg-primary-500/10'-->
<!--            ]"-->
<!--          >-->
<!--            <div class="flex items-center gap-3">-->
<!--              <UIcon name="line-md:grid-3-twotone" class="w-5 h-5" />-->
<!--              Categories-->
<!--            </div>-->
<!--            <UIcon-->
<!--                :name="openCategory ? 'i-heroicons-chevron-up' : 'i-heroicons-chevron-down'"-->
<!--                class="w-4 h-4"-->
<!--            />-->
<!--          </button>-->
<!--          <Transition name="accordion">-->
<!--            <div v-show="openCategory" class="pl-6 mt-1 space-y-1">-->
<!--              <NuxtLink-->
<!--                  to="/dashboard/categories/list"-->
<!--                  :class="subLinkClass('/dashboard/categories/list')"-->
<!--              >-->
<!--                List-->
<!--              </NuxtLink>-->
<!--              <NuxtLink-->
<!--                  to="/dashboard/categories/create"-->
<!--                  :class="subLinkClass('/dashboard/categories/create')"-->
<!--              >-->
<!--                Create-->
<!--              </NuxtLink>-->
<!--            </div>-->
<!--          </Transition>-->
<!--        </div>-->

        <!-- Apps -->
        <div>
          <button
              @click="openApps = !openApps"
              :class="[
              'w-full flex items-center justify-between gap-3 px-3 py-2 rounded-lg',
              isActive('/dashboard/apps')
                ? 'bg-primary-500 text-white shadow'
                : 'text-slate-600 dark:text-slate-300 hover:bg-primary-500/10'
            ]"
          >
            <div class="flex items-center gap-3">
              <UIcon name="i-heroicons-archive-box-x-mark" class="w-5 h-5" />
              Apps Management
            </div>
            <UIcon
                :name="openApps ? 'i-heroicons-chevron-up' : 'i-heroicons-chevron-down'"
                class="w-4 h-4"
            />
          </button>
          <Transition name="accordion">
            <div v-show="openApps" class="pl-6 mt-1 space-y-1">
              <NuxtLink
                  to="/dashboard/apps/list"
                  :class="subLinkClass('/dashboard/apps/list')"
              >
                List
              </NuxtLink>
              <NuxtLink
                  to="/dashboard/apps/create"
                  :class="subLinkClass('/dashboard/apps/create')"
              >
                Create
              </NuxtLink>
            </div>
          </Transition>
        </div>

        <!-- Token Management -->
        <div>
          <button
              @click="openToken = !openToken"
              :class="[
              'w-full flex items-center justify-between gap-3 px-3 py-2 rounded-lg',
              isActive('/dashboard/token')
                ? 'bg-primary-500 text-white shadow'
                : 'text-slate-600 dark:text-slate-300 hover:bg-primary-500/10'
            ]"
          >
            <div class="flex items-center gap-3">
              <UIcon name="i-heroicons-key" class="w-5 h-5" />
              Token Management
            </div>
            <UIcon
                :name="openToken ? 'i-heroicons-chevron-up' : 'i-heroicons-chevron-down'"
                class="w-4 h-4"
            />
          </button>
          <Transition name="accordion">
            <div v-show="openToken" class="pl-6 mt-1 space-y-1">
              <NuxtLink
                  to="/dashboard/token/list"
                  :class="subLinkClass('/dashboard/token/list')"
              >
                List
              </NuxtLink>
              <NuxtLink
                  to="/dashboard/token/create"
                  :class="subLinkClass('/dashboard/token/create')"
              >
                Create
              </NuxtLink>
            </div>
          </Transition>
        </div>
      </nav>

      <!-- Profile menu (sticky bottom) -->
      <div class="border-t border-slate-200/50 dark:border-slate-700/50 p-3">
        <div class="relative">
          <button
              @click="profileOpen = !profileOpen"
              class="w-full flex items-center gap-3 p-2 rounded-xl
                   hover:bg-slate-100 dark:hover:bg-slate-800"
          >
            <UAvatar
                src="https://i.pravatar.cc/32"
                :alt="user?.fullName || ''"
                size="sm"
            />
            <div class="text-left flex-1">
              <p class="text-sm font-semibold text-slate-800 dark:text-white">
                {{ user?.fullName || '—' }}
              </p>
              <p class="text-xs text-slate-500 dark:text-slate-400">
                {{ user?.email || '—' }}
              </p>
            </div>
            <UIcon
                name="i-heroicons-chevron-up"
                class="w-4 h-4 text-slate-400 transition-transform"
                :class="profileOpen ? 'rotate-180' : ''"
            />
          </button>

          <Transition name="dropdown">
            <div
                v-if="profileOpen"
                class="absolute mb-2 bottom-full left-0 right-0
                     bg-white dark:bg-slate-800
                     border border-slate-200 dark:border-slate-700
                     rounded-lg shadow-xl z-20"
            >
              <NuxtLink
                  to="/dashboard/profile/edit"
                  class="block px-3 py-2 text-sm text-slate-700
                       dark:text-slate-200 hover:bg-primary-50 dark:hover:bg-primary-500/20"
              >
                Edit Profile
              </NuxtLink>
              <NuxtLink
                  to="/dashboard/settings"
                  class="block px-3 py-2 text-sm text-slate-700
                       dark:text-slate-200 hover:bg-primary-50 dark:hover:bg-primary-500/20"
              >
                Settings
              </NuxtLink>
              <hr class="border-slate-200 dark:border-slate-700">
              <button
                  @click="logout"
                  class="w-full text-left px-3 py-2 text-sm text-red-600
                       dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-500/20"
              >
                Logout
              </button>
            </div>
          </Transition>
        </div>
      </div>
    </aside>

    <!-- Main Content -->
    <div class="flex-1 flex flex-col">
      <!-- Header -->
      <header
          class="sticky top-0 z-20 bg-white/70 dark:bg-slate-900/70
               backdrop-blur-lg border-b border-slate-200/50 dark:border-slate-700/50
               px-4 h-16 flex items-center justify-between"
      >
        <UButton
            icon="i-heroicons-bars-3"
            variant="ghost"
            class="md:hidden text-gray-700"
            @click="sidebarOpen = true"
        />
        <h2 class="text-lg font-semibold text-slate-800 dark:text-white md:ml-0">
          <div>
            <h1 class="text-lg font-bold text-slate-800 dark:text-white">{{ route.path.replace('/dashboard/', '').replace('/dashboard', '').toUpperCase().split("/")[0] || 'DASHBOARD' }}</h1>
            <p class="text-sm text-slate-500 dark:text-slate-400">{{route.path}}</p>
          </div>

        </h2>
        <ThemeToggle />
      </header>

      <!-- Page -->
      <main class="flex-1 p-4 overflow-y-auto">
        <slot />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { useAuthStore } from '~/stores/auth'
import { useUserStore } from '~/stores/user'

/* ---------- stores ---------- */
const authStore = useAuthStore()
const userStore = useUserStore()

/* reactive user */
const { user } = storeToRefs(userStore)

/* ---------- layout state ---------- */
const sidebarOpen  = ref(false)
const openProducts = ref(false)
const openPortfolio =ref(false)
const openBlogs    = ref(false)
const openCategory = ref(false)
const openApps     = ref(false)
const openToken    = ref(false)
const profileOpen  = ref(false)

const route = useRoute()

/* ---------- helpers ---------- */
const isActive = (path: string) => route.path.startsWith(path)

function linkClass(path: string, exclude: string[] = []) {
  const active = exclude.length
      ? route.path === path
      : isActive(path)

  return [
    'flex items-center gap-3 px-3 py-2 rounded-lg transition-colors',
    active
        ? 'bg-primary-500 text-white shadow'
        : 'text-slate-600 dark:text-slate-300 hover:bg-primary-500/10'
  ]
}

function subLinkClass(path: string) {
  return [
    'block px-3 py-2 text-xs rounded transition-colors',
    route.path === path
        ? 'text-primary-600 dark:text-primary-400 font-semibold'
        : 'text-slate-500 dark:text-slate-400 hover:text-primary-500 dark:hover:text-primary-300'
  ]
}

/* ---------- logout ---------- */
async function logout() {
  await authStore.logout()
  userStore.clear()
}

/* ---------- auto-close sidebar on route change ---------- */
watchEffect(() => {
  sidebarOpen.value = false
})

/* ---------- hydrate user on first load ---------- */
onMounted(() => {
  if (authStore.isLoggedIn) userStore.fetchProfile()
})

</script>

<style scoped>
.fade-enter-active, .fade-leave-active       { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to             { opacity: 0; }

.accordion-enter-active, .accordion-leave-active { transition: all 0.2s ease; }
.accordion-enter-from, .accordion-leave-to       { opacity: 0; transform: translateY(-6px); }

.dropdown-enter-active, .dropdown-leave-active { transition: all 0.15s ease; }
.dropdown-enter-from, .dropdown-leave-to       { opacity: 0; transform: scale(0.95); }
</style>