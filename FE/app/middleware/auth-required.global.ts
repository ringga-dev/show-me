// midellware/auth-redircet.global.ts
export default defineNuxtRouteMiddleware((to) => {
    const accessToken = useCookie('accessToken').value

    // Jika path diawali /dashboard/... dan belum login → redirect
    if (to.path.startsWith('/dashboard')) {
        if (!accessToken || accessToken.length < 1){
            return navigateTo('/auth/login')
        }

    }
})