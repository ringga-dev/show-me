package ngga.ring.myblog.core.base.browser



// ---- JsoupHelper yang lebih aman (UA, referrer, try/catch) ----
object JsoupHelper {
    fun fetchDocument(url: String, userAgent: String = "Mozilla/5.0", timeout: Int = 5000): org.jsoup.nodes.Document {
        return try {
            org.jsoup.Jsoup.connect(url)
                .userAgent(userAgent)
                .referrer("https://www.google.com/")
                .timeout(timeout)
                .get()
        } catch (_: Exception) {
            org.jsoup.nodes.Document("") // kosong -> akan memicu fallback
        }
    }
}
