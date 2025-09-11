import {defineStore} from 'pinia'
import type {UserModel, UsersModel} from '~/types/models/UserModel'
import type {BaseResponse} from '~/types/models/BaseResponse'

export const useUserStore = defineStore('user', () => {
    const user = ref<UserModel | null>(null)
    const loading = ref(false)

    const isLoaded = computed(() => user.value !== null)

    function setUser(u: UserModel | null) {
        user.value = u
    }

    function clear() {
        user.value = null
    }

    /* ambil profil berdasarkan token yang ada di cookie */
    async function fetchProfile() {
        if (!useAuthStore().isLoggedIn) return null;

        const loading = useState<boolean>('profileLoading', () => false);
        loading.value = true;

        try {
            const res = await $secureFetch<BaseResponse<UserModel>>('/api/v1/user/me', true);
            if (res.code === 200 && res.data) {
                user.value = res.data;
                return user.value;
            }
        } catch {
            console.log('error fetching user');
            await useAuthStore().logout();
        } finally {
            loading.value = false;
        }
        return null;
    }

    async function updateProfile(data: any) {
        if (!useAuthStore().isLoggedIn) return null;

        const loading = useState<boolean>('profileLoading', () => false);
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<UserModel>>('/api/v1/user/me',true, {
                method: 'PUT',
                body: data,
            })

        } catch {
            console.log('error fetching user');
        } finally {
            loading.value = false;
        }
        return null;
    }


    async function getAllUsers() {
        if (!useAuthStore().isLoggedIn) return null;

        try {
            return await $secureFetch<BaseResponse<UsersModel[]>>('/api/v1/user/all', true);
        } catch {
            console.log('error fetching user');
        } finally {
            loading.value = false;
        }
        return null;
    }

    async function addUser(user: any) {
        if (!useAuthStore().isLoggedIn) return null;

        try {
            return await $secureFetch<BaseResponse<never>>('/api/v1/user/add', true, {
                method: 'POST',
                body: user, // <-- dikirim sebagai JSON otomatis
            });
        } catch {
            console.log('error fetching user');
        } finally {
            loading.value = false;
        }
        return null;
    }

    async function deleteUser(id: string) {
        if (!useAuthStore().isLoggedIn) return null;
        try {
            return await $secureFetch<BaseResponse<never>>(`/api/v1/user/${id}/delete`, true, {
                method: 'DELETE',
            });
        } catch {
            console.log('error fetching user');
        } finally {
            loading.value = false;
        }
        return null;
    }


    return {
        user: readonly(user),
        loading: readonly(loading),
        isLoaded,
        updateProfile,
        setUser,
        clear,
        fetchProfile,
        getAllUsers,
        addUser,
        deleteUser
    }
})