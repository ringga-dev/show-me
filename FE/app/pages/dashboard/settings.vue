<template>
  <main>
    <div class="max-w-4xl mx-auto px-4 sm:px-6 py-8 space-y-6">
      <!-- Header -->
      <div class="text-center">
        <h1 class="text-3xl sm:text-4xl font-bold text-gray-900 dark:text-white mb-2">
          Profile Editor
        </h1>
        <p class="text-gray-600 dark:text-gray-400 max-w-lg mx-auto">
          Manage your professional profile and portfolio settings
        </p>
      </div>

      <!-- Main Content -->
      <div class="space-y-6">
        <!-- Identity Section -->
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-semibold text-gray-900 dark:text-white flex items-center gap-2">
              <svg class="w-5 h-5 text-indigo-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
              </svg>
              Personal Information
            </h2>
          </div>

          <div class="grid md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Full Name</label>
              <input
                  v-model="state.name"
                  type="text"
                  class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 dark:bg-gray-700 dark:text-white"
                  placeholder="Your full name"
              >
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Username</label>
              <input
                  v-model="state.username"
                  type="text"
                  class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 dark:bg-gray-700 dark:text-white"
                  placeholder="Your username"
              >
            </div>
          </div>
        </div>

        <!-- Social Links -->
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-semibold text-gray-900 dark:text-white flex items-center gap-2">
              <svg class="w-5 h-5 text-indigo-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M21 12a9 9 0 01-9 9m9-9a9 9 0 00-9-9m9 9H3m9 9a9 9 0 01-9-9m9 9c1.657 0 3-4.03 3-9s-1.343-9-3-9m0 18c-1.657 0-3-4.03-3-9s1.343-9 3-9m-9 9a9 9 0 019-9"/>
              </svg>
              Social Profiles
            </h2>
            <button
                @click="addSocialLink"
                class="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-full shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
              <svg class="-ml-0.5 mr-1.5 h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
              </svg>
              Add Link
            </button>
          </div>

          <div class="space-y-3">
            <div
                v-for="(link, i) in state.social"
                :key="i"
                class="flex flex-col sm:flex-row items-start sm:items-center gap-3"
            >
              <div class="flex items-center gap-3 w-full sm:w-auto">
                <SelectIcon
                    v-model="link.icon"
                    emit-label
                    @update:label="(label) => (link.name = label)"
                    class="min-w-[120px]"
                />
                <input
                    v-model="link.name"
                    type="text"
                    class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 dark:bg-gray-700 dark:text-white"
                    placeholder="Platform"
                    disabled
                >
              </div>
              <input
                  v-model="link.url"
                  type="url"
                  class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 dark:bg-gray-700 dark:text-white"
                  placeholder="https://example.com"
              >
              <button
                  @click="removeSocialLink(i)"
                  class="p-2 text-gray-500 hover:text-red-500 dark:text-gray-400 dark:hover:text-red-400"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                </svg>
              </button>
            </div>
          </div>
        </div>

        <!-- Stats Section -->
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
          <h2 class="text-lg font-semibold text-gray-900 dark:text-white flex items-center gap-2 mb-4">
            <svg class="w-5 h-5 text-indigo-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
            </svg>
            Key Statistics
          </h2>

          <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div
                v-for="(st, i) in state.stats"
                :key="i"
                class="bg-gray-50 dark:bg-gray-700 p-3 rounded-lg"
            >
              <label class="block text-xs font-medium text-gray-500 dark:text-gray-400 mb-1">{{
                  st.label || 'Stat'
                }}</label>
              <input
                  v-model.number="st.value"
                  type="number"
                  min="0"
                  class="w-full px-3 py-2 bg-white dark:bg-gray-600 border border-gray-200 dark:border-gray-500 rounded-md text-center font-bold text-gray-900 dark:text-white focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
              >
            </div>
          </div>
        </div>

        <!-- Skills Section -->
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-semibold text-gray-900 dark:text-white flex items-center gap-2">
              <svg class="w-5 h-5 text-indigo-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"/>
              </svg>
              Skills & Expertise
            </h2>
            <button
                @click="addSkillCategory"
                class="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-full shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
              <svg class="-ml-0.5 mr-1.5 h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
              </svg>
              Add Category
            </button>
          </div>

          <div class="space-y-6">
            <div
                v-for="(cat, i) in state.skills"
                :key="i"
                class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4 border border-gray-200 dark:border-gray-600"
            >
              <div class="flex items-center justify-between mb-3">
                <div class="flex items-center gap-3">
                  <select
                      v-model="cat.icon"
                      class="px-3 py-1.5 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 dark:bg-gray-700 dark:text-white text-sm"
                  >
                    <option value="i-heroicons-device-phone-mobile">Mobile</option>
                    <option value="i-heroicons-server">Backend</option>
                    <option value="i-heroicons-globe-alt">Web</option>
                    <option value="i-heroicons-cog-8-tooth">Tools</option>
                    <option value="i-heroicons-code-bracket">Programming</option>
                  </select>
                  <input
                      v-model="cat.name"
                      type="text"
                      class="px-4 py-1.5 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 dark:bg-gray-700 dark:text-white font-medium"
                      placeholder="Category name"
                  >
                </div>
                <button
                    @click="removeSkillCategory(i)"
                    class="p-1.5 text-gray-500 hover:text-red-500 dark:text-gray-400 dark:hover:text-red-400"
                >
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                  </svg>
                </button>
              </div>

              <div class="space-y-3">
                <div
                    v-for="(sk, j) in cat.skills"
                    :key="j"
                    class="flex flex-col sm:flex-row items-start sm:items-center gap-3"
                >
                  <input
                      v-model="sk.name"
                      type="text"
                      class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 dark:bg-gray-700 dark:text-white"
                      placeholder="Skill name"
                  >

                  <div class="flex items-center gap-3 w-full sm:w-64">
                    <input
                        v-model.number="sk.level"
                        type="range"
                        min="0"
                        max="100"
                        class="w-full h-2 bg-gray-200 dark:bg-gray-600 rounded-lg appearance-none cursor-pointer"
                    >
                    <span class="text-sm font-medium text-gray-700 dark:text-gray-300 w-10 text-right">{{
                        sk.level
                      }}%</span>
                  </div>

                  <button
                      @click="cat.skills.splice(j, 1)"
                      class="p-1.5 text-gray-500 hover:text-red-500 dark:text-gray-400 dark:hover:text-red-400"
                  >
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                    </svg>
                  </button>
                </div>

                <button
                    @click="cat.skills.push({ name: '', level: 50 })"
                    class="inline-flex items-center px-2.5 py-1.5 border border-gray-300 dark:border-gray-600 text-xs font-medium rounded text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                >
                  <svg class="-ml-0.5 mr-1.5 h-3.5 w-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
                  </svg>
                  Add Skill
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Projects Section -->
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-semibold text-gray-900 dark:text-white flex items-center gap-2">
              <svg class="w-5 h-5 text-indigo-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"/>
              </svg>
              Projects Portfolio
            </h2>
            <button
                @click="addProject"
                class="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-full shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
              <svg class="-ml-0.5 mr-1.5 h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
              </svg>
              Add Project
            </button>
          </div>

          <div class="space-y-4">
            <div
                v-for="(p, i) in state.projects"
                :key="p.id"
                class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4 border border-gray-200 dark:border-gray-600"
            >
              <div class="flex items-center justify-between mb-3">
                <input
                    v-model="p.title"
                    type="text"
                    class="text-lg font-bold bg-transparent border-b border-transparent focus:border-gray-300 dark:focus:border-gray-500 focus:outline-none px-1 py-0.5 w-full"
                    placeholder="Project title"
                >
                <button
                    @click="removeProject(i)"
                    class="p-1 text-gray-500 hover:text-red-500 dark:text-gray-400 dark:hover:text-red-400"
                >
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                  </svg>
                </button>
              </div>

              <textarea
                  v-model="p.description"
                  rows="3"
                  class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 dark:bg-gray-600 dark:text-white mb-3"
                  placeholder="Project description"
              ></textarea>

              <div class="grid md:grid-cols-2 gap-4 mb-3">
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Image URL</label>
                  <input
                      v-model="p.image"
                      type="url"
                      class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 dark:bg-gray-600 dark:text-white"
                      placeholder="https://example.com/image.jpg"
                  >
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Live URL</label>
                  <input
                      v-model="p.liveUrl"
                      type="url"
                      class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 dark:bg-gray-600 dark:text-white"
                      placeholder="https://example.com"
                  >
                </div>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Technologies Used</label>
                <div class="flex flex-wrap items-center gap-2">
                  <div
                      v-for="(t, j) in p.technologies"
                      :key="j"
                      class="flex items-center bg-indigo-100 dark:bg-indigo-900 text-indigo-800 dark:text-indigo-200 px-2.5 py-0.5 rounded-full text-xs font-medium"
                  >
                    <input
                        v-model="p.technologies[j]"
                        type="text"
                        class="bg-transparent border-none focus:ring-0 w-20 text-xs"
                        placeholder="Tech name"
                    >
                    <button
                        @click="removeProjectTech(i, j)"
                        class="ml-1 text-indigo-500 hover:text-indigo-700 dark:hover:text-indigo-300"
                    >
                      <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                      </svg>
                    </button>
                  </div>
                  <button
                      @click="p.technologies.push('')"
                      class="inline-flex items-center px-2 py-0.5 border border-gray-300 dark:border-gray-600 text-xs font-medium rounded-full text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600"
                  >
                    <svg class="-ml-0.5 mr-1 h-3 w-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
                    </svg>
                    Add Tech
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Appearance Section -->
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
          <h2 class="text-lg font-semibold text-gray-900 dark:text-white flex items-center gap-2 mb-4">
            <svg class="w-5 h-5 text-indigo-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M7 21a4 4 0 01-4-4V5a2 2 0 012-2h4a2 2 0 012 2v12a4 4 0 01-4 4zm0 0h12a2 2 0 002-2v-4a2 2 0 00-2-2h-2.343M11 7.343l1.657-1.657a2 2 0 012.828 0l2.829 2.829a2 2 0 010 2.828l-8.486 8.485M7 17h.01"/>
            </svg>
            Appearance Settings
          </h2>

          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium text-gray-900 dark:text-white">Dark Mode</p>
              <p class="text-sm text-gray-500 dark:text-gray-400">Toggle between light and dark theme</p>
            </div>
            <button
                type="button"
                @click="toggleDark"
                class="relative inline-flex h-6 w-11 flex-shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
                :class="isDark ? 'bg-indigo-600' : 'bg-gray-200'"
                role="switch"
                aria-checked="false"
            >
              <span class="sr-only">Dark mode</span>
              <span
                  aria-hidden="true"
                  class="pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out"
                  :class="isDark ? 'translate-x-5' : 'translate-x-0'"
              ></span>
            </button>
          </div>
        </div>

        <!-- Actions -->
        <div class="flex flex-col sm:flex-row justify-end gap-3 pt-4">
          <button
              @click="resetAll"
              :disabled="isSaving"
              class="inline-flex items-center px-4 py-2 border border-gray-300 dark:border-gray-600 text-sm font-medium rounded-md shadow-sm text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
          >
            Reset to Defaults
          </button>
          <button
              @click="saveAll"
              :disabled="isSaving"
              class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
          >
            <svg
                v-if="isSaving"
                class="animate-spin -ml-1 mr-2 h-4 w-4 text-white"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
            >
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor"
                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <svg
                v-else
                class="-ml-1 mr-2 h-4 w-4"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
            >
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
            </svg>
            Save Changes
          </button>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup lang="ts">
import {reactive, computed, ref} from 'vue'
import SelectIcon from "~/components/input/SelectIcon.vue";

definePageMeta({layout: 'dashboard'})

/* ---- State Management ---- */
const defaultState = {
  name: 'Ringga Septia Pribadi',
  username: 'ringga-dev',
  social: [
    {name: 'LinkedIn', url: 'https://www.linkedin.com/in/ringga-septia-pribadi/', icon: 'i-simple-icons-linkedin'},
    {name: 'GitHub', url: 'https://github.com/ringga-dev', icon: 'i-simple-icons-github'},
    {name: 'Portfolio', url: 'https://ringga-dev.my.id', icon: 'i-heroicons-globe-alt'}
  ],
  stats: [
    {label: 'Projects', value: 8},
    {label: 'Years', value: 3},
    {label: 'Apps Shipped', value: 5},
    {label: 'Techs', value: 20}
  ],
  skills: [
    {
      name: 'Mobile',
      icon: 'i-heroicons-device-phone-mobile',
      skills: [
        {name: 'Kotlin', level: 95},
        {name: 'Flutter & Dart', level: 90},
        {name: 'Android SDK', level: 93},
        {name: 'Jetpack Compose', level: 88}
      ]
    },
    {
      name: 'Backend & DB',
      icon: 'i-heroicons-server',
      skills: [
        {name: 'Spring Boot', level: 80},
        {name: 'Node.js / Express', level: 85},
        {name: 'SQLite', level: 90},
        {name: 'Ktor Client', level: 82}
      ]
    },
    {
      name: 'Web Front End',
      icon: 'i-heroicons-globe-alt',
      skills: [
        {name: 'React', level: 80},
        {name: 'Vue.js', level: 75},
        {name: 'Next.js', level: 70},
        {name: 'Nuxt', level: 70},
        {name: 'Tailwind CSS', level: 85},
        {name: 'Material UI', level: 80}
      ]
    },
    {
      name: 'Tools & Workflow',
      icon: 'i-heroicons-cog-8-tooth',
      skills: [
        {name: 'Android Studio', level: 96},
        {name: 'Git & CI/CD', level: 90},
        {name: 'MVVM / Clean Code', level: 94},
        {name: 'Code Review', level: 92}
      ]
    }
  ],
  projects: [
    {
      id: 1,
      title: 'BatamFast App',
      description: 'End-to-end booking & e-ticketing app for BatamFast ferries. Built with Flutter',
      image: '/images/project/img_batamfast.png',
      technologies: ['Flutter', 'Dart', 'REST API', 'Firebase', 'Google Maps', 'Geolocator'],
      liveUrl: 'https://play.google.com/store/apps/details?id=com.batamfast.booking'
    },
    {
      id: 2,
      title: 'Sanur App',
      description: 'End-to-end booking & e-ticketing app for Sanur Ferry. Built with Flutter',
      image: '/images/project/img_sanur.png',
      technologies: ['Flutter', 'Dart', 'REST API', 'GetX'],
      liveUrl: null
    },
    {
      id: 3,
      title: 'POS Laundry Q',
      description: 'Lightweight Android app to track daily expenses with MVVM, Jetpack & Hilt DI.',
      image: '/images/project/img_loundry.png',
      technologies: ['Compose', 'Hilt', 'Coroutines', 'Material3'],
      liveUrl: 'https://play.google.com/store/apps/details?id=ngga.ring.laundryq&hl=id'
    }
  ]
}

const state = reactive(JSON.parse(JSON.stringify(defaultState)))
const isSaving = ref(false)

/* ---- Dark Mode ---- */
const colorMode = useColorMode()
const isDark = computed(() => colorMode.value === 'dark')
const toggleDark = () => (colorMode.preference = isDark.value ? 'light' : 'dark')

/* ---- Helper Methods ---- */
const addSocialLink = () => {
  state.social.push({name: '', url: '', icon: 'i-heroicons-link'})
}

const removeSocialLink = (index: number) => {
  state.social.splice(index, 1)
}

const addSkillCategory = () => {
  state.skills.push({
    name: '',
    icon: 'i-heroicons-cog',
    skills: [{name: '', level: 50}]
  })
}

const removeSkillCategory = (index: number) => {
  state.skills.splice(index, 1)
}

const addProject = () => {
  state.projects.push({
    id: Date.now(),
    title: '',
    description: '',
    image: '',
    technologies: [],
    liveUrl: ''
  })
}

const removeProject = (index: number) => {
  state.projects.splice(index, 1)
}

const removeProjectTech = (projectIndex: number, techIndex: number) => {
  state.projects[projectIndex].technologies.splice(techIndex, 1)
}

/* ---- Persistence ---- */
const saveAll = async () => {
  isSaving.value = true
  try {
    localStorage.setItem('portfolioSettings', JSON.stringify(state))
    useToast().add({
      title: 'Success',
      description: 'Your profile has been saved',
      icon: 'i-heroicons-check-circle',
      color: 'success'
    })
  } catch (error) {
    useToast().add({
      title: error || 'Error',
      description: 'Failed to save profile',
      icon: 'i-heroicons-exclamation-circle',
    })
  } finally {
    isSaving.value = false
  }
}

const resetAll = () => {
  if (confirm('Are you sure you want to reset all fields to default values?')) {
    Object.assign(state, JSON.parse(JSON.stringify(defaultState)))
    saveAll()
  }
}


/* ---- Load on Mount ---- */
onMounted(() => {
  const saved = localStorage.getItem('portfolioSettings')
  if (saved) {
    try {
      Object.assign(state, JSON.parse(saved))
    } catch (e) {
      console.error('Failed to parse saved settings', e)
    }
  }
})

// Fungsi untuk handle ketika icon dipilih
const handleIconSelect = (iconObject, link) => {
  link.name = iconObject.label; // Auto isi nama platform
  // Jika Anda masih perlu menyimpan value icon secara terpisah
  link.icon = iconObject.value;
};

</script>

<style>
.glass-card {
  @reference bg-white/50 dark:bg-slate-800/50 backdrop-blur-lg rounded-xl p-6 shadow-sm border border-slate-200/50 dark:border-slate-700/30;
}

/* Better transitions for inputs */
.u-input, .u-textarea, .u-slider {
  transition: all 0.2s ease;
}

/* Better focus styles */
.u-input:focus, .u-textarea:focus {
  @reference ring-2 ring-indigo-500/50 border-indigo-500;
}
</style>