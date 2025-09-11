export const useGo = () => {
    return (path: string) => {
        return navigateTo(path, { replace: true })
    }
}