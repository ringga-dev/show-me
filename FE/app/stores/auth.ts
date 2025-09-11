import {defineStore} from 'pinia'
import type {LoginModel} from '~/types/models/LoginModel'
import type {BaseResponse} from '~/types/models/BaseResponse'

export const useAuthStore = defineStore('auth', () => {
    /* ---------------- state ---------------- */
    const accessToken = useCookie<string | null>('accessToken', {sameSite: 'lax', default: () => null})
    const refreshToken = useCookie<string | null>('refreshToken', {sameSite: 'lax', default: () => null})

    /* ---------------- getters ---------------- */
    const isLoggedIn = computed(() => !!accessToken.value)
    const isRefreshable = computed(() => !!refreshToken.value)

    /* ---------------- actions ---------------- */

    /* login & simpan cookie */
    async function login(email: string, password: string) {
        const config = useRuntimeConfig()
        const res: BaseResponse<LoginModel> = await $fetch('/api/v1/auth/login', {
            method: 'POST',
            body: {email, password},
            baseURL: config.public.apiBase,
            credentials: 'include',
        })

        if (res.code === 200 && res.data?.token) {
            if (res.data.role?.some(r => r.toLowerCase() === 'admin')) {
                accessToken.value = res.data.token.accessToken ?? ""
                refreshToken.value = res.data.token.refreshToken ?? ""
            }
        }
        return res
    }

    /* refresh token */
    async function refresh(

    ) {
        if (!refreshToken.value) return false

        const config = useRuntimeConfig()
        try {
            const res: BaseResponse<LoginModel> =
                await $fetch('/api/v1/auth/refresh-token', {
                    method: 'POST',
                    body: {refreshToken: refreshToken.value},
                    baseURL: config.public.apiBase,
                    credentials: 'include',
                })

            console.log(res)

            if (res.code === 200 && res.data?.token) {
                accessToken.value = res.data.token.accessToken ?? ""
                refreshToken.value = res.data.token.refreshToken ?? ""
                return true
            } else {
                await logout()
            }
        } catch (e) {
            console.error(e)
            await logout()
        }
        await logout()
        return false
    }

    /* logout */
    async function logout(redirect = true) {
        accessToken.value = null
        refreshToken.value = null

        if (redirect) {
            await navigateTo('/auth/login', {external: true})
        }
    }

    return {
        accessToken: readonly(accessToken),
        refreshToken: readonly(refreshToken),
        isLoggedIn,
        isRefreshable,
        login,
        refresh,
        logout,
    }
})