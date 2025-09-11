// middleware/guest.ts
export default defineNuxtRouteMiddleware((to) => {
    const accessToken = useCookie('accessToken').value

    // Kalau sudah login dan mau ke halaman auth -> lempar ke dashboard
    if (accessToken && to.path.startsWith('/auth')) {
        if (to.path !== '/dashboard') {     // cegah redirect ke path yang sama
            return navigateTo('/dashboard')
        }
    }
})