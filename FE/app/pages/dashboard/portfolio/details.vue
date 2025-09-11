<script setup lang="ts">
import * as v from "valibot";
import type { FormSubmitEvent } from "@nuxt/ui";
import type { BaseResponse } from "~/types/models/BaseResponse";
import type { UserPortfolioModel } from "~/types/models/UserPortfolioModel";

definePageMeta({ layout: "dashboard" });

const toast = useToast();

/* ===== Pinia store & shared loading ===== */
const portfolioStore = useUserPortfolioStore();
// store menggunakan useState('profileLoading'), kita ikut pakai state yang sama:
const profileLoading = useState<boolean>("profileLoading", () => false);

/* ===== Types / Validation ===== */
type SocialMedia = { name: string; url: string; icon: string };

const urlStr = v.pipe(v.string(), v.url("Must be a valid URL"));
const socialSchema = v.object({
  name: v.pipe(v.string(), v.nonEmpty("Name is required")),
  url: urlStr,
  icon: v.pipe(v.string(), v.nonEmpty("Icon is required")),
});

const schema = v.object({
  name: v.pipe(v.string(), v.nonEmpty("Name is required")),
  email: v.pipe(v.string(), v.email("Invalid email"), v.nonEmpty("Email is required")),
  phone: v.optional(v.string()),
  address: v.optional(v.string()),
  titlesStr: v.optional(v.string()),         // comma-separated (UI)
  description: v.optional(v.string()),
  hashTagStr: v.optional(v.string()),        // comma-separated (UI)
  workedWithStr: v.optional(v.string()),     // comma-separated (UI)
  socialMedia: v.array(socialSchema),
  aboutMe: v.optional(v.string()),
  // hanya untuk preview UI, tidak ikut dikirim
  image: v.optional(urlStr),
});
type Schema = v.InferOutput<typeof schema>;

/* ===== Reactive form ===== */
const form = reactive<Schema>({
  name: "",
  email: "",
  phone: "",
  address: "",
  titlesStr: "",
  description: "",
  hashTagStr: "",
  workedWithStr: "",
  socialMedia: [],
  aboutMe: "",
  image: "", // UI only
});

/* ===== Preview (UI only) ===== */
const DEFAULT_IMAGE = "https://dummyimage.com/800x450/1f2937/ffffff&text=Portfolio+Preview";
const preview   = ref<string>(DEFAULT_IMAGE);
const imgLoaded = ref(false);
const imgError  = ref(false);
const imgSize   = ref<{ w: number; h: number } | null>(null);

watch(
    () => form.image,
    (val) => {
      preview.value   = val || DEFAULT_IMAGE;
      imgLoaded.value = false;
      imgError.value  = false;
      imgSize.value   = null;
    },
    { immediate: true }
);

function onImgLoad(e: Event) {
  const el = e.target as HTMLImageElement;
  imgLoaded.value = true;
  imgError.value  = false;
  imgSize.value   = { w: el.naturalWidth, h: el.naturalHeight };
}
function onImgError() {
  imgLoaded.value = false;
  imgError.value  = true;
}
async function copyImageUrl() {
  if (!preview.value) return;
  try {
    await navigator.clipboard.writeText(preview.value);
    toast.add({ title: "Copied", description: "Image URL copied to clipboard." });
  } catch {
    toast.add({ color: "error", title: "Failed", description: "Copy failed." });
  }
}

function clearImage() {
  form.image = "";
  preview.value = DEFAULT_IMAGE;
}
const imageHost = computed(() => {
  try { return preview.value && preview.value !== DEFAULT_IMAGE ? new URL(preview.value).host : ""; }
  catch { return ""; }
});

/* ===== Helpers ===== */
function splitToArray(s?: string) {
  return (s ?? "")
      .split(",")
      .map((t) => t.trim())
      .filter(Boolean);
}

/* ===== Prefill via Pinia (tetap UI sebelumnya) ===== */
onMounted(async () => {
  const res = await portfolioStore.fetchDetails() as BaseResponse<UserPortfolioModel> | null;
  const data = res?.data;
  if (!data) return;

  form.name          = data.name ?? "";
  form.email         = data.email ?? "";
  form.phone         = data.phone ?? "";
  form.address       = data.address ?? "";
  form.titlesStr     = (data.titles ?? []).join(", ");
  form.description   = data.description ?? "";
  form.hashTagStr    = (data.hashTag ?? []).join(", ");
  form.workedWithStr = (data.workedWith ?? []).join(", ");
  form.socialMedia   = [...(data.socialMedia ?? [])];
  form.aboutMe       = data.aboutMe ?? "";
  form.image      = data.image ?? "";
});

/* ===== Submit via Pinia (payload persis contoh) ===== */
async function onSubmit(_event: FormSubmitEvent<Schema>) {
  const payload = {
    name:        form.name,
    email:       form.email,
    phone:       form.phone || "",
    address:     form.address || "",
    titles:      splitToArray(form.titlesStr),
    description: form.description || "",
    hashTag:     splitToArray(form.hashTagStr),
    socialMedia: form.socialMedia as SocialMedia[],
    workedWith:  splitToArray(form.workedWithStr),
    aboutMe:     form.aboutMe || "",
    image:     form.image || "",

  };

  const res = await portfolioStore.updateDetail(payload) as BaseResponse<never> | null;

  if (res && (res.status ?? "OK")) {
    toast.add({ title: "Saved", description: "Portfolio updated successfully." });
  } else {
    toast.add({ color: "error", title: "Failed", description: "Update failed" });
  }
}
</script>

<template>
  <div>
    <h2 class="text-2xl font-bold mb-4 text-slate-800 dark:text-white">Portfolio Details</h2>

    <UForm
        method="post"
        :schema="schema"
        :state="form"
        class="space-y-4 md:space-y-6"
        @submit="onSubmit"
    >
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">

        <!-- KIRI (UI sama persis) -->
        <UCard class="space-y-4">
          <div class="grid grid-cols-1 gap-4">
            <UFormField label="Name" name="name">
              <UInput v-model="form.name"
                      :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
                      placeholder="Input name" class="w-full" type="text" />
            </UFormField>

            <UFormField label="Email" name="email">
              <UInput v-model="form.email"
                      :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
                      placeholder="you@example.com" class="w-full" type="email" />
            </UFormField>

            <UFormField label="Phone" name="phone">
              <UInput v-model="form.phone"
                      :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
                      placeholder="08xxxxxxxxxx" class="w-full" type="text" />
            </UFormField>

            <UFormField label="Address" name="address">
              <UInput v-model="form.address"
                      :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
                      placeholder="Batam" class="w-full" type="text" />
            </UFormField>

            <UFormField label="Titles (comma separated)" name="titlesStr">
              <UInput v-model="form.titlesStr"
                      :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
                      placeholder="Android Developer, Flutter, Nuxt, aboutMe" class="w-full" type="text" />
            </UFormField>

            <UFormField label="Hashtags (comma separated)" name="hashTagStr">
              <UInput v-model="form.hashTagStr"
                      :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
                      placeholder="Firebase, PlayConsole, Stripe, Cloud Run, Jetpack" class="w-full" type="text" />
            </UFormField>

            <UFormField label="Worked With (comma separated URLs)" name="workedWithStr">
              <UInput
                  v-model="form.workedWithStr"
                  placeholder="https://dummyimage.com/120x28/fc0000/000&text=Firebase, https://dummyimage.com/120x28/393452/000&text=Play+Console"
                  class="w-full"
                  :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
                  type="text"
              />
            </UFormField>

            <UFormField label="About Me (HTML allowed)" name="aboutMe">
              <UTextarea
                  v-model="form.aboutMe"
                  class="w-full"
                  :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
                  :rows="8"
                  placeholder="I’m a <strong>Senior Mobile Developer</strong> with <strong>{{ yearsExperience }}+ years</strong>..."
              />
            </UFormField>

            <!-- Social Media -->
            <div class="space-y-3">
              <div class="flex items-center justify-between">
                <label class="text-sm font-medium">Social Media</label>
                <UButton size="xs" variant="outline" icon="i-heroicons-plus" @click="form.socialMedia.push({ name: '', url: '', icon: '' })">Add</UButton>
              </div>
              <div v-if="form.socialMedia.length === 0" class="text-xs text-gray-500">
                Belum ada item. Klik “Add” untuk menambah.
              </div>
              <div v-for="(s, i) in form.socialMedia" :key="i" class="grid grid-cols-1 md:grid-cols-12 gap-3 items-end">
                <UFormField label="Name" :name="`socialMedia.${i}.name`" class="md:col-span-3">
                  <UInput v-model="s.name"
                          :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
                          placeholder="LinkedIn / GitHub / Portfolio" />
                </UFormField>
                <UFormField label="URL" :name="`socialMedia.${i}.url`" class="md:col-span-6">
                  <UInput v-model="s.url"
                          :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
                          placeholder="https://..." type="url" />
                </UFormField>
                <UFormField label="Icon" :name="`socialMedia.${i}.icon`" class="md:col-span-2">
                  <UInput v-model="s.icon"
                          :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
                          placeholder="i-simple-icons-github" />
                </UFormField>
                <div class="md:col-span-1">
                  <UButton color="error" variant="ghost" icon="i-heroicons-trash" @click="form.socialMedia.splice(i,1)" />
                </div>
              </div>
            </div>
          </div>
        </UCard>

        <!-- KANAN (preview di kanan atas—UI dipertahankan) -->
        <UCard class="space-y-4">
          <div class="grid grid-cols-1 gap-4">

            <!-- PREVIEW -->
            <div>
              <UCard class="overflow-hidden border border-gray-200 dark:border-gray-700 rounded-xl" :ui="{ body: 'p-0' }">
                <!-- Toolbar -->
                <div class="flex items-center justify-between px-4 py-3 bg-gray-50 dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700">
                  <div class="flex items-center gap-2 text-sm text-gray-700 dark:text-gray-300">
                    <UIcon name="i-heroicons-photo" class="w-4 h-4" />
                    <span>Preview</span>
                    <span v-if="imageHost" class="text-xs text-gray-500">• {{ imageHost }}</span>
                    <span v-if="imgSize" class="text-xs text-gray-500">• {{ imgSize.w }}×{{ imgSize.h }}px</span>
                  </div>
                  <div class="flex items-center gap-1">
                    <UButton size="xs" variant="ghost" icon="i-heroicons-clipboard" @click="copyImageUrl" />
                    <UButton size="xs" color="error" variant="ghost" icon="i-heroicons-x-mark" @click="clearImage" />
                  </div>
                </div>

                <!-- Canvas -->
                <div class="relative w-full h-64 bg-gray-100 dark:bg-gray-900">
                  <!-- Skeleton -->
                  <div v-if="!imgLoaded && !imgError" class="absolute inset-0 animate-pulse bg-gray-200 dark:bg-gray-800" />
                  <!-- Error -->
                  <div v-if="imgError" class="absolute inset-0 flex items-center justify-center text-gray-500">Failed to load</div>
                  <!-- Image -->
                  <img
                      :src="preview"
                      class="absolute inset-0 w-full h-full object-contain"
                      @load="onImgLoad"
                      @error="onImgError"
                      alt="Preview"
                  />
                </div>
              </UCard>
            </div>

            <!-- Image URL (UI only, tidak dikirim) -->
            <UFormField label="Image URL (UI only)" name="image">
              <div class="flex gap-2 w-full">
                <UInput v-model="form.image"
                        :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
                        placeholder="https://example.com/image.png" class="w-full" type="url" />
                <UButton v-if="form.image" icon="i-heroicons-x-mark" color="error" variant="ghost" @click="clearImage" />
              </div>
            </UFormField>

            <UFormField label="Description" name="description">
              <UTextarea v-model="form.description" class="w-full"
                         :ui="{ base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500' }"
                         :rows="6" placeholder="Crafting clean, scalable Android & Flutter experiences from Batam, Indonesia." />
            </UFormField>
          </div>
        </UCard>

        <!-- Submit -->
        <div class="lg:col-span-2 mt-2 flex gap-3">
          <UButton type="submit" label="Save" :loading="profileLoading" />
          <UButton to="/products/list" label="Cancel" color="gray" variant="outline" />
        </div>
      </div>
    </UForm>

  </div>
</template>
