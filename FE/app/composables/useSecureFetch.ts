// composables/useSecureFetch.ts
export async function $secureFetch<T>(
    url: string,
    useToken = false,
    opts: Parameters<typeof $fetch<T>>[1] = {}
): Promise<T> {
    const config = useRuntimeConfig();
    const auth = useAuthStore();

    const request = () => {
        // Rebuild headers every call so a refreshed token is picked up for the retry
        const headers: Record<string, string> = {
            ...(opts?.headers as Record<string, string> | undefined),
        };

        if (useToken && auth?.accessToken) {
            headers.Authorization = `Bearer ${auth.accessToken}`;
        }

        return $fetch<T>(url, {
            ...opts,
            baseURL: config.public.apiBase,
            credentials: 'include',
            headers,
        });
    };

    try {
        return await request();
    } catch (err: any) {
        // Coba refresh sekali saja jika 401
        const status =
            err?.response?.status ??
            err?.statusCode ??
            err?.data?.statusCode ??
            err?.status;

        if (status === 401 && auth?.isLoggedIn) {
            await auth.refresh();        // refresh token
            return await request();      // retry sekali lagi (dengan token baru)
        }

        throw err; // biar caller yang tangani selain 401
    }
}
