<script setup lang="ts">
import * as v from 'valibot'
import type {FormSubmitEvent} from "@nuxt/ui";
import {useAuthStore} from '~/stores/auth'



definePageMeta({
  middleware: 'guest'
})

const store = useAuthStore()
const toast = useToast()

const schema = v.object({
  email: v.pipe(v.string(), v.email('Invalid email'), v.nonEmpty("email is required")),
  password: v.pipe(v.string(), v.nonEmpty('password is required')),
})

type Schema = v.InferOutput<typeof schema>
const state = reactive({
  email: '',
  password: '',
  rememberMe: false
})

async function onSubmit(event: FormSubmitEvent<Schema>) {
  try {
    const res = await store.login(event.data.email, event.data.password)
    if (res.code === 200) {
      if (res.data.role?.some(r => r.toLowerCase() === 'admin')) {
        toast.add({title: 'Success', description: 'Login successful', color: 'success'})
      } else {
        toast.add({title: 'Error', description: 'Access Login Denied', color: 'error'})
      }
      navigateTo('/dashboard')
    } else {
      toast.add({title: 'Error', description: res.message?.[0] || 'Login failed', color: 'error'})
    }
  } catch (err: never) {
    toast.add({title: 'Error', description: err.message || 'Something went wrong', color: 'error'})
  }
}
</script>


<template>
  <div class="min-h-screen flex flex-col bg-gradient-to-t from-primary-500/20">
    <section>
      <div class="flex flex-col items-center justify-center px-6 py-8 mx-auto md:h-screen lg:py-0">
        <a href="#" class="flex items-center mb-6 text-2xl font-semibold text-gray-900 dark:text-white">
          <img class="w-8 h-8 mr-2" src="https://flowbite.s3.amazonaws.com/blocks/marketing-ui/logo.svg" alt="logo">
          Flowbite
        </a>
        <div
            class="w-full bg-white rounded-3xl dark:border md:mt-0 sm:max-w-md xl:p-0 dark:bg-gray-800 dark:border-gray-700">
          <div class="p-6 space-y-4 md:space-y-6 sm:p-8">
            <h1 class="text-xl font-bold leading-tight tracking-tight text-gray-900 md:text-2xl dark:text-white">
              Sign in to your account
            </h1>
            <UForm method="post" :schema="schema" :state="state" class="space-y-4 md:space-y-6" @submit="onSubmit">
              <UFormField label="Email" name="email"
                          class="block mb-2 text-sm font-medium text-gray-900 dark:text-white">
                <UInput
                    v-model="state.email"
                    placeholder="name@company.com"
                    class="w-full"
                    onerror="invalid"
                    :ui="{
                  base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500'
                }"
                />
              </UFormField>
              <UFormField label="Password" name="password"
                          class="block mb-2 text-sm font-medium text-gray-900 dark:text-white">
                <UInput
                    v-model="state.password"
                    placeholder="••••••••"
                    class="w-full"
                    onerror="invalid"
                    type="password"
                    :ui="{
                  base: 'bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500'
                }"
                />
              </UFormField>
              <div class="flex items-center justify-between">
                <div class="flex items-start">
                  <div class="flex items-center h-5">
                    <input
                        id="remember"
                        v-model="state.rememberMe"
                        aria-describedby="remember"
                        type="checkbox"
                        class="w-4 h-4 border border-gray-300 rounded bg-gray-50 focus:ring-3 focus:ring-primary-300 dark:bg-gray-700 dark:border-gray-600 dark:focus:ring-primary-600 dark:ring-offset-gray-800"
                    />
                  </div>
                  <div class="ml-3 text-sm">
                    <label for="remember" class="text-gray-500 dark:text-gray-300">Remember me</label>
                  </div>
                </div>

                <ULink as="text" type="text"
                       class="text-sm font-medium text-primary-600 hover:underline dark:text-primary-500"
                       @click="go('/auth/forgot_password')">Forgot password?
                </ULink>
              </div>
              <button type="submit"
                      class="w-full text-white bg-primary-600 hover:bg-primary-700 focus:ring-4 focus:outline-none focus:ring-primary-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-primary-600 dark:hover:bg-primary-700 dark:focus:ring-primary-800">
                Sign in
              </button>
              <p class="text-sm font-light text-gray-500 dark:text-gray-400">
                Don’t have an account yet?
                <ULink as="text" type="text" class="font-medium text-primary-600 hover:underline dark:text-primary-500"
                       @click="go('/auth/register')">Sign up
                </ULink>
              </p>
            </UForm>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>

</style>