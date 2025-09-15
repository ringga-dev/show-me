// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
    compatibilityDate: '2025-07-15',
    devtools: {enabled: true},
    app: {
        head: {
            link: [{
                rel: 'icon',
                type: 'image/png',
                href: '/logo.png'
            }]
        }
    },
    css: ['~/assets/css/main.css'],
    runtimeConfig: {
        public: {
            apiBase: process.env.API_BASE || 'http://localhost:9091', // {{BASE_URL}}
        }
    },
    modules: [
        '@nuxt/content',
        '@nuxt/eslint',
        '@nuxt/image',
        '@nuxt/ui',
        '@nuxtjs/mdc',
        '@pinia/nuxt',
    ],
    vite: {
        resolve: {
            alias: {
                // paksa vite pakai versi browser
                debug: 'debug/src/browser.js'
            }
        }
    },
    ui: {
        theme: {
            colors: [
                'primary',
                'secondary',
                'tertiary',
                'info',
                'success',
                'warning',
                'error',
                'gray',
            ]
        }
    }
})