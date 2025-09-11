<template>
  <header
      class="bg-white/75 dark:bg-gray-900/70 backdrop-blur-none border-b -mb-px sticky top-0 z-50 border-gray-200 dark:border-gray-800"
  >
    <UContainer class="flex flex-wrap items-center justify-between h-14">
      <div class="flex items-center gap-x-4">
        <ULink
            class="text-xl md:text-2xl text-primary font-bold flex items-center gap-x-2"
            to="/"
        >
          <UIcon name="i-lucide-lightbulb" class="size-10" /> Ringga.Dev
        </ULink>
        <UTooltip v-if="!hideClear" text="Clear chat">
          <UButton
              color="gray"
              icon="i-heroicons-trash"
              @click="$emit('clear')"
          />
        </UTooltip>
      </div>
      <div class="flex items-center gap-x-4">
        <ThemeToggle />
        <UButton
            icon="i-simple-icons-github"
            color="gray"
            variant="ghost"
            to="https://ringga-dev.github.io/"
            target="_blank"
        />
        <UButton to="/auth/login">
          Log In
        </UButton>
        <UButton variant="outline" to="/auth/register">
          Register
        </UButton>
      </div>
    </UContainer>
  </header>
</template>

<script setup lang="ts">

defineEmits(['clear']);

defineProps({
  hideClear: {
    type: Boolean,
    default: true,
  },
});
</script>