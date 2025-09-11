import {defineStore} from 'pinia'
import type {BaseResponse} from '~/types/models/BaseResponse'
import type {TokenModel} from "~/types/models/TokenModel";

export const useTokenStore = defineStore('token', () => {

    const loading = ref(false)

    async function getToken() {
        if (!useAuthStore().isLoggedIn) return null;
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<TokenModel[]>>('/api/v1/token', true);
        } catch {
            console.log('error fetching apps');
        } finally {
            loading.value = false;
        }
        return null;
    }

    async function addToken(data: any) {
        if (!useAuthStore().isLoggedIn) return null;
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<TokenModel>>('/api/v1/token', true, {method: 'POST', body: data});
        } catch {
            console.log('error insert app');
        } finally {
            loading.value = false;
        }
        return null;
    }

    async function getTokenById(id: string) {
        if (!useAuthStore().isLoggedIn) return null;
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<TokenModel>>(`/api/v1/token/${id}`, true);
        } catch {
            console.log('error insert app');
        } finally {
            loading.value = false;
        }
        return null;
    }

    async function updateToken(id: string, data: any) {
        if (!useAuthStore().isLoggedIn) return null;
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<TokenModel>>(`/api/v1/token/${id}`, true, {method: 'PUT', body: data});
        } catch {
            console.log('error insert app');
        } finally {
            loading.value = false;
        }
        return null;
    }

    async function deleteToken(id: string) {
        if (!useAuthStore().isLoggedIn) return null;
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<any>>(`/api/v1/token/${id}`, true, {method: 'DELETE'})
        } catch {
            console.log('error insert app');
        } finally {
            loading.value = false;
        }
        return null;
    }

    return {loading: readonly(loading), getToken, addToken, getTokenById, updateToken, deleteToken}
})