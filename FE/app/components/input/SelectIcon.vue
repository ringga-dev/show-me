<template>
  <div>
    <!-- Trigger button -->
    <button
        type="button"
        @click="openDialog"
        class="flex items-center gap-2 px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 w-full"
    >
      <!-- Ikon -->
      <UIcon
          :name="currentIcon"
          class="w-5 h-5"
          v-if="currentIcon"
      />
      <!-- Label -->
      <span class="truncate flex-1 text-left">
        {{ currentLabel || 'Pilih ikon' }}
      </span>
      <UIcon name="i-heroicons-chevron-down" class="w-4 h-4 text-gray-400" />
    </button>

    <!-- Dialog overlay -->
    <div
        v-if="isOpen"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm"
        @click.self="closeDialog"
    >
      <div
          class="bg-white dark:bg-gray-800 rounded-xl shadow-2xl w-full max-w-lg max-h-[80vh] flex flex-col"
      >
        <!-- Header -->
        <div
            class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700"
        >
          <h3 class="text-lg font-semibold">Pilih Ikon</h3>
          <UButton
              icon="i-heroicons-x-mark"
              color="gray"
              variant="ghost"
              @click="closeDialog"
          />
        </div>

        <!-- Search -->
        <div class="p-4 border-b border-gray-200 dark:border-gray-700">
          <UInput
              v-model="searchQuery"
              placeholder="Cari ikon..."
              icon="i-heroicons-magnifying-glass"
              size="sm"
          />
        </div>

        <!-- Icons grid -->
        <div class="flex-1 overflow-y-auto p-4">
          <div class="grid grid-cols-4 sm:grid-cols-6 gap-3">
            <button
                v-for="icon in filteredIcons"
                :key="icon.value"
                @click="selectIcon(icon)"
                class="flex flex-col items-center p-2 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition"
                :class="{
                'bg-indigo-100 dark:bg-indigo-900/30 ring-2 ring-indigo-500':
                  icon.value === currentIcon
              }"
            >
              <UIcon :name="icon.value" class="w-7 h-7 mb-1" />
              <span class="text-xs text-center truncate w-full">
                {{ icon.label }}
              </span>
            </button>
          </div>
        </div>

        <!-- Footer -->
        <div class="p-3 flex justify-end">
          <UButton label="Close" @click="closeDialog" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'

const props = defineProps({
  modelValue: { type: [String, Object], default: '' },
  emitLabel: { type: Boolean, default: false } // 👈 tambahan
})

const emit = defineEmits(['update:modelValue', 'update:label']) // 👈 tambahan

/* ---------------- state ---------------- */
const isOpen = ref(false)
const searchQuery = ref('')

/* ---------------- icon list ---------------- */
const icons = [
  { value: 'i-simple-icons-linkedin', label: 'LinkedIn' },
  { value: 'i-simple-icons-github', label: 'GitHub' },
  { value: 'i-simple-icons-twitter', label: 'Twitter' },
  { value: 'i-simple-icons-facebook', label: 'Facebook' },
  { value: 'i-simple-icons-instagram', label: 'Instagram' },
  { value: 'i-simple-icons-youtube', label: 'YouTube' },
  { value: 'i-simple-icons-tiktok', label: 'TikTok' },
  { value: 'i-simple-icons-discord', label: 'Discord' },
  { value: 'i-simple-icons-slack', label: 'Slack' },
  { value: 'i-simple-icons-reddit', label: 'Reddit' },
  { value: 'i-simple-icons-whatsapp', label: 'WhatsApp' },
  { value: 'i-simple-icons-telegram', label: 'Telegram' },
  { value: 'i-simple-icons-spotify', label: 'Spotify' },
  { value: 'i-simple-icons-apple', label: 'Apple' },
  { value: 'i-simple-icons-android', label: 'Android' },
  { value: 'i-simple-icons-windows', label: 'Windows' },
  { value: 'i-simple-icons-google', label: 'Google' },
  { value: 'i-simple-icons-amazon', label: 'Amazon' },
  { value: 'i-heroicons-globe-alt', label: 'Website' },
  { value: 'i-heroicons-envelope', label: 'Email' },
  { value: 'i-heroicons-phone', label: 'Phone' },
  { value: 'i-heroicons-map-pin', label: 'Location' },
  { value: 'i-heroicons-calendar', label: 'Calendar' },
  { value: 'i-heroicons-chat-bubble-left-ellipsis', label: 'Chat' }
]

/* ---------------- computed ---------------- */
const currentIcon = computed(() =>
    typeof props.modelValue === 'string' ? props.modelValue : props.modelValue?.value
)

const currentLabel = computed(() =>
    typeof props.modelValue === 'string'
        ? icons.find(i => i.value === props.modelValue)?.label
        : props.modelValue?.label
)

const filteredIcons = computed(() => {
  const q = searchQuery.value.toLowerCase()
  return icons.filter(
      i =>
          i.label.toLowerCase().includes(q) ||
          i.value.toLowerCase().includes(q)
  )
})

/* ---------------- methods ---------------- */
const openDialog = () => {
  isOpen.value = true
  searchQuery.value = ''
}

const closeDialog = () => {
  isOpen.value = false
}

const selectIcon = (icon) => {
  emit('update:modelValue', icon.value)
  if (props.emitLabel) emit('update:label', icon.label) // 👈 tambahan
  closeDialog()
}

/* ---------------- keyboard listener ---------------- */
const onKeyDown = (e) => {
  if (e.key === 'Escape') closeDialog()
}

onMounted(() => window.addEventListener('keydown', onKeyDown))
onBeforeUnmount(() => window.removeEventListener('keydown', onKeyDown))
</script>