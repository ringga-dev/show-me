<script setup>
const colorMode = useColorMode();

const isDark = computed({
  get() {
    return colorMode.value === "dark";
  },
  set() {
    colorMode.preference = colorMode.value === "dark" ? "light" : "dark";
  },
});
</script>

<template>
  <UTooltip text="Toggle theme" :ui="{ popper: { strategy: 'absolute' } }">
    <UButton
        :variant="isDark ? 'outline' : 'solid'"
        class="relative px-2 py-2 flex items-center justify-center
           transition hover:text-primary-500 dark:hover:text-primary-400 ring-3 rounded-lg focus:text-white"
        @click="isDark = !isDark"
    >
      <Icon
          aria-hidden="true"
          :name="isDark ? 'solar:sun-2-outline' : 'solar:moon-outline'"
          :color="isDark ?  'black' : 'white'"
          class="w-5 h-5"
      />
      <span class="sr-only">Toggle theme</span>
    </UButton>
  </UTooltip>
</template>