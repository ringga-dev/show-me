import {defineStore} from 'pinia'
import type {BaseResponse} from "~/types/models/BaseResponse";
import type {
    UserPortfolioExperienceDataModel,
    UserPortfolioModel, UserPortfolioProjectModel,
    UserPortfolioSkillModel
} from "~/types/models/UserPortfolioModel";

export const useUserPortfolioStore = defineStore('userPortfolio', () => {
    const loading = ref(false)

    /* ambil profil berdasarkan token yang ada di cookie */
    async function fetchDetails() {
        if (!useAuthStore().isLoggedIn) return null;

        const loading = useState<boolean>('profileLoading', () => false);
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<UserPortfolioModel>>('/api/v1/portfolio', true);
        } catch {
            console.log('error fetching user');
            await useAuthStore().logout();
        } finally {
            loading.value = false;
        }
        return null;
    }

    async function fetchDetailsPublic() {

        const loading = useState<boolean>('profileLoading', () => false);
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<UserPortfolioModel>>('/api/v1/public/portfolio', false);
        } catch {
            console.log('error fetching user');
            await useAuthStore().logout();
        } finally {
            loading.value = false;
        }
        return null;
    }

    async function updateDetail(data: any) {
        if (!useAuthStore().isLoggedIn) return null;

        const loading = useState<boolean>('profileLoading', () => false);
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<never>>('/api/v1/portfolio', true, {
                method: 'POST',
                body: data, // <-- dikirim sebagai JSON otomatis
            });
        } catch {
            console.log('error fetching user');
        } finally {
            loading.value = false;
        }
        return null;

    }

    /* ================== EXPERIENCE ================== */
    async function getPortfolioSkill() {
        if (!useAuthStore().isLoggedIn) return null;

        const loading = useState<boolean>('profileLoading', () => false);
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<UserPortfolioSkillModel[]>>('/api/v1/portfolio/skill', true);
        } catch {
            console.log('error fetching user');
        } finally {
            loading.value = false;
        }
        return null;
    }

    async function getPortfolioSkillPublic() {

        const loading = useState<boolean>('profileLoading', () => false);
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<UserPortfolioSkillModel[]>>('/api/v1/public/portfolio/skill', false);
        } catch {
            console.log('error fetching user');
        } finally {
            loading.value = false;
        }
        return null;
    }



    async function getPortfolioSkillById(id: string) {
        if (!useAuthStore().isLoggedIn) return null
        loading.value = true
        try {
            return await $secureFetch<BaseResponse<UserPortfolioSkillModel>>(
                `/api/v1/portfolio/skill/${id}`,
                true
            )
        } catch {
            console.log('error fetching skill by id')
        } finally {
            loading.value = false
        }
        return null
    }


    async function addPortfolioSkills(data: any) {
        if (!useAuthStore().isLoggedIn) return null;

        const loading = useState<boolean>('profileLoading', () => false);
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<never>>('/api/v1/portfolio/skill', true, {
                method: 'POST',
                body: data, // <-- dikirim sebagai JSON otomatis
            })
        } catch {
            console.log('error fetching user');
        } finally {
            loading.value = false;
        }
    }

    async function updatePortfolioSkills(data: any, id: string) {
        if (!useAuthStore().isLoggedIn) return null;

        const loading = useState<boolean>('profileLoading', () => false);
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<never>>(`/api/v1/portfolio/skill/${id}`, true, {
                method: 'PUT',
                body: data, // <-- dikirim sebagai JSON otomatis
            })
        } catch {
            console.log('error fetching user');
        } finally {
            loading.value = false;
        }
    }

    async function deletePortfolioSkill(id: string) {
        if (!useAuthStore().isLoggedIn) return null;
        try {
            return await $secureFetch<BaseResponse<never>>(`/api/v1/portfolio/skill/${id}`, true, {
                method: 'DELETE',
            });
        } catch {
            console.log('error fetching user');
        } finally {
            loading.value = false;
        }
        return null;

    }

    /** Experience */
    async function getPortfolioExperience() {
        if (!useAuthStore().isLoggedIn) return null;
        const loading = useState<boolean>('profileLoading', () => false);
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<UserPortfolioExperienceDataModel[]>>('/api/v1/portfolio/experience', true)

        }catch {
            console.log('error fetching user');
        } finally {
            loading.value = false;
        }
    }

    async function getPortfolioExperiencePublic() {
        const loading = useState<boolean>('profileLoading', () => false);
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<UserPortfolioExperienceDataModel[]>>('/api/v1/public/portfolio/experience', false)

        }catch {
            console.log('error fetching user');
        } finally {
            loading.value = false;
        }
    }

    async function getPortfolioExperienceById(id: string) {
        if (!useAuthStore().isLoggedIn) return null;
        const loading = useState<boolean>('profileLoading', () => false);
        loading.value = true;
        try {
            return await $secureFetch<BaseResponse<UserPortfolioExperienceDataModel>>(
                `/api/v1/portfolio/experience/${id}`,
                true
            )
        } catch {
            console.log('error fetching skill by id')
        } finally {
            loading.value = false
        }
        return null
    }


    async function addPortfolioExperience( data: any) {
        if (!useAuthStore().isLoggedIn) return null;
        const loading = useState<boolean>('profileLoading', () => false);
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<never>>(`/api/v1/portfolio/experience`, true, {
                method: 'POST',
                body: data, // <-- dikirim sebagai JSON otomatis
            })
        } catch {
            console.log('error fetching skill by id')
        } finally {
            loading.value = false
        }
        return null
    }

    async function updatePortfolioExperience(id: string, data: any) {
        if (!useAuthStore().isLoggedIn) return null;
        const loading = useState<boolean>('profileLoading', () => false);
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<never>>(`/api/v1/portfolio/experience/${id}`, true, {
                method: 'PUT',
                body: data, // <-- dikirim sebagai JSON otomatis
            })
        } catch {
            console.log('error fetching skill by id')
        } finally {
            loading.value = false
        }
        return null
    }

    async function deletePortfolioExperience(id: string) {
        if (!useAuthStore().isLoggedIn) return null;
        const loading = useState<boolean>('profileLoading', () => false);
        loading.value = true;

        try {
            return await $secureFetch<BaseResponse<never>>(`/api/v1/portfolio/experience/${id}`, true)
        } catch {
            console.log('error fetching skill by id')
        } finally {
            loading.value = false
        }
        return null
    }

    /** Project */
    async function getPortfolioProjects() {
        if (!useAuthStore().isLoggedIn) return null
        loading.value = true
        try {
            return await $secureFetch<BaseResponse<UserPortfolioProjectModel[]>>('/api/v1/portfolio/project', true)
        } finally { loading.value = false }
    }

    async function getPortfolioProjectsPublic() {
        loading.value = true
        try {
            return await $secureFetch<BaseResponse<UserPortfolioProjectModel[]>>('/api/v1/public/portfolio/project', false)
        } finally { loading.value = false }
    }

    async function getPortfolioProjectById(id: string) {
        if (!useAuthStore().isLoggedIn) return null
        loading.value = true
        try {
            return await $secureFetch<BaseResponse<UserPortfolioProjectModel>>(`/api/v1/portfolio/project/${id}`, true)
        } finally { loading.value = false }
    }

    async function addPortfolioProject(data: any) {
        if (!useAuthStore().isLoggedIn) return null
        loading.value = true
        try {
            return await $secureFetch<BaseResponse<never>>('/api/v1/portfolio/project', true, {
                method: 'POST',
                body: data
            })
        } catch {
            console.log('error fetching skill by id')
        } finally {
            loading.value = false
        }
        return null
    }

    async function updatePortfolioProject(id: string, data: any) {
        if (!useAuthStore().isLoggedIn) return null
        loading.value = true
        try {
            return await $secureFetch<BaseResponse<never>>(`/api/v1/portfolio/project/${id}`, true, {
                method: 'PUT',
                body: data
            })
        } catch {
            console.log('error fetching skill by id')
        } finally {
            loading.value = false
        }
        return null
    }

    async function deletePortfolioProject(id: string) {
        if (!useAuthStore().isLoggedIn) return null
        loading.value = true
        try {
            return await $secureFetch<BaseResponse<never>>(`/api/v1/portfolio/project/${id}`, true, {
                method: 'DELETE'
            })
        } catch {
            console.log('error fetching skill by id')
        } finally {
            loading.value = false
        }
        return null
    }





    return {
        loading,
        fetchDetails,
        fetchDetailsPublic,
        updateDetail,
        getPortfolioSkill,
        getPortfolioSkillPublic,
        deletePortfolioSkill,
        addPortfolioSkills,
        getPortfolioSkillById,
        updatePortfolioSkills,
        getPortfolioExperience,
        getPortfolioExperiencePublic,
        addPortfolioExperience,
        getPortfolioExperienceById,
        updatePortfolioExperience,
        deletePortfolioExperience,
        getPortfolioProjects,
        getPortfolioProjectsPublic,
        getPortfolioProjectById,
        addPortfolioProject,
        updatePortfolioProject,
        deletePortfolioProject
    }
})