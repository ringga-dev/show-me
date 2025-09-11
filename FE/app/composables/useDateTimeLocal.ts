// composables/useDateTimeLocal.ts

export type DateLike = Date | string | number | null | undefined

/** Format ke string "YYYY-MM-DDTHH:mm" sesuai <input type="datetime-local"> */
export function formatDateTimeLocal(value: DateLike): string {
    if (!value) return ''
    const d = value instanceof Date ? value : new Date(value)
    if (isNaN(d.getTime())) return ''
    const pad = (n: number) => String(n).padStart(2, '0')
    const yyyy = d.getFullYear()
    const MM = pad(d.getMonth() + 1)
    const dd = pad(d.getDate())
    const hh = pad(d.getHours())
    const mm = pad(d.getMinutes())
    return `${yyyy}-${MM}-${dd}T${hh}:${mm}`
}

/** Parse dari string input ke Date */
export function parseDateTimeLocal(value: string): Date {
    const d = new Date(value)
    if (isNaN(d.getTime())) {
        const [date, time] = value.split('T')
        const [y, m, day] = (date || '').split('-').map(Number)
        const [h, min] = (time || '').split(':').map(Number)
        return new Date(y ?? 0, (m || 1) - 1, day || 1, h || 0, min || 0)
    }
    return d
}
