import {defineStore} from 'pinia'
import type {BaseResponse} from '~/types/models/BaseResponse'
import type {BlogModel} from "~/types/models/BlogModel";

export const useBlogStore = defineStore('blog', () => {

    const loading = ref(false)

    async function getBlog() {
        if (!useAuthStore().isLoggedIn) return null;
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<BlogModel[]>>('/api/v1/blog', true);
        } catch {
            console.log('error fetching apps');
        } finally {
            loading.value = false;
        }
        return null;
    }

    async function getBlogPublic() {
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<BlogModel[]>>('/api/v1/public/blogs', false);
        } catch {
            console.log('error fetching apps');
        } finally {
            loading.value = false;
        }
        return null;
    }

    async function getBlogBySlugPublic(data: any) {
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<BlogModel>>(`/api/v1/public/blog/${data}`, false);
        } catch {
            console.log('error fetching apps');
        } finally {
            loading.value = false;
        }
        return null;
    }


    async function addBlog(data: any) {
        if (!useAuthStore().isLoggedIn) return null;
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<BlogModel>>('/api/v1/blog', true, {method: 'POST', body: data});
        } catch {
            console.log('error insert app');
        } finally {
            loading.value = false;
        }
        return null;
    }

    async function getBlogById(id: string) {
        if (!useAuthStore().isLoggedIn) return null;
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<BlogModel>>(`/api/v1/blog/${id}`, true);
        } catch {
            console.log('error insert app');
        } finally {
            loading.value = false;
        }
        return null;
    }

    async function updateBlog(id: string, data: any) {
        if (!useAuthStore().isLoggedIn) return null;
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<BlogModel>>(`/api/v1/blog/${id}`, true, {method: 'PUT', body: data});
        } catch {
            console.log('error insert app');
        } finally {
            loading.value = false;
        }
        return null;
    }

    async function deleteBlog(id: string) {
        if (!useAuthStore().isLoggedIn) return null;
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<any>>(`/api/v1/blog/${id}`, true, {method: 'DELETE'})
        } catch {
            console.log('error insert app');
        } finally {
            loading.value = false;
        }
        return null;
    }

    return {
        loading: readonly(loading),
        getBlog,
        addBlog,
        getBlogById,
        updateBlog,
        deleteBlog,
        getBlogPublic,
        getBlogBySlugPublic
    }
})