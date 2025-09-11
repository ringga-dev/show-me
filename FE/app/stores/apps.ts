import {defineStore} from 'pinia'
import type {BaseResponse} from '~/types/models/BaseResponse'
import type {AppModel} from "~/types/models/AppModel";
import type {RouteParamValue} from "#vue-router";

export const useAppsStore = defineStore('apps', () => {

    const loading = ref(false)

    async function getApps() {
        if (!useAuthStore().isLoggedIn) return null;
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<AppModel[]>>('/api/v1/app/all', true);
        } catch {
            console.log('error fetching apps');
        } finally {
            loading.value = false;
        }
        return null;
    }

    async function addApp(appForm: any) {
        if (!useAuthStore().isLoggedIn) return null;
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<AppModel>>('/api/v1/app/add', true, {method: 'POST', body: appForm});
        } catch {
            console.log('error insert app');
        } finally {
            loading.value = false;
        }
        return null;
    }

    async function getAppsById(id: string) {
        if (!useAuthStore().isLoggedIn) return null;
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<AppModel>>(`/api/v1/app/${id}`, true);
        } catch {
            console.log('error insert app');
        } finally {
            loading.value = false;
        }
        return null;
    }

    async function updateApp(id: string | RouteParamValue[], appForm: any) {
        if (!useAuthStore().isLoggedIn) return null;
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<AppModel>>(`/api/v1/app/${id}`, true, {method: 'PUT', body: appForm});
        } catch {
            console.log('error insert app');
        } finally {
            loading.value = false;
        }
        return null;
    }

    async function deleteApp(id: string) {
        if (!useAuthStore().isLoggedIn) return null;
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<any>>(`/api/v1/app/${id}`, true, {method: 'DELETE'})
        } catch {
            console.log('error insert app');
        } finally {
            loading.value = false;
        }
        return null;
    }

    return {loading: readonly(loading), getApps, addApp, getAppsById, updateApp, deleteApp}
})