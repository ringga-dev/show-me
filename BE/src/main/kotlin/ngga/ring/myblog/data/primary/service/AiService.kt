package ngga.ring.myblog.data.primary.service

import ngga.ring.myblog.core.base.browser.JsoupHelper
import ngga.ring.myblog.core.config.ai.AiGeminiConfig
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.concurrent.ConcurrentHashMap


interface AiService {
//    fun aiGeminiSend(promt: String): Any
    fun aiGeminiAksSend(prompt: String, userId: String=  "default"): Any
    fun aiGptAksSend(prompt: String, userId: String=  "default"): Any
}

@Service
class AiServiceImpl(
    private val restTemplate: RestTemplate = RestTemplate(),
    private val config: AiGeminiConfig
) : AiService {

    private val tokenGemini = config.geminiToken
    private val modelGemini = config.geminiModel
    private val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelGemini:generateContent"



    /* --- static context --- */
    private val staticContext = """
    Anda adalah asisten pribadi @ringga-dev
    - Name          : Ringga Septia Pribadi  
    - Tanggal lahir : 21-09-1998
    - Kota          : Batam
    - Pekerjaan     : Mobile Developer
    - Hobi          : koding
    - Bio singkat   : … (biografi singkat yang sudah ada)

    DAFTAR PROYEK YANG TELAH DIBUAT:
    1. Aplikasi “EduLearn” – e-learning Android berbasis MVVM & Clean Code, fitur course, quiz, dan leaderboard.
    2. “BatamTravel” – app Android/iOS Flutter untuk booking paket wisata; integrasi Google Maps & API payment gateway.
    3. “CashTrack” – expense tracker Android dengan Jetpack Compose, Room, dan Flow; support multi-mata uang.
    4. “HealthNote” – reminder obat & jadwal check-up; fitur notifikasi lokal & backup ke Firebase.
    5. “KodeCamp” – internal training app untuk junior dev; modul materi, code lab, dan submission.

    TUGAS:
    1. Jawab semua pertanyaan berdasarkan konteks di atas.
    2. Jika user menanyakan proyek, berikan detail singkat (teknologi, fitur, role Anda).
    3. Cari semua hal yang ditanyakan user dari internet dan data yang valit.
    4. jangan keluar dari kontex saat mengembalikan data.
    5. jika user menanyakan hal di luar kontex yang ada, beritau kalo anda hanya bisa menjawab semua tentang ringga.
""".trimIndent()


    /* --- memori per user (UUID → history) --- */
    private val history: MutableMap<String, MutableList<Map<String, Any>>> = ConcurrentHashMap()

    // ---- Gemini ----
    override fun aiGeminiAksSend(prompt: String, userId: String): Any {
        val mem = history.computeIfAbsent(userId) { mutableListOf() }

        // 1) simpan prompt asli
        mem += mapOf("role" to "user", "parts" to listOf(mapOf("text" to prompt)))

        // 2) kalau ada kata kunci cari/search -> enrich
        val query = extractSearchQuery(prompt)
        val enrichedPrompt = if (query != null) {
            val searchResults = quickSearch(query)
            "$prompt\n\nHasil penelusuran daring singkat:\n$searchResults"
        } else prompt

        // (hindari print ke stdout di server; pakai logger kalau perlu)
        // logger.info("Enriched prompt: {}", enrichedPrompt)

        // 3) ganti isi prompt terakhir dengan yang diperkaya
        mem[mem.lastIndex] = mapOf("role" to "user", "parts" to listOf(mapOf("text" to enrichedPrompt)))

        // 4) susun body (context + history)
        val body = mapOf(
            "contents" to (listOf(mapOf("role" to "user", "parts" to listOf(mapOf("text" to staticContext))))) + mem)
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            add("X-goog-api-key", tokenGemini)
        }

        val entity = HttpEntity(body, headers)
        val res = restTemplate.postForObject(url, entity, Map::class.java) ?: return "No answer"

        @Suppress("UNCHECKED_CAST")
        val candidates = res["candidates"] as? List<Map<String, *>>
        val reply = candidates
            ?.firstOrNull()
            ?.let { it["content"] as? Map<*, *> }
            ?.let { it["parts"] as? List<Map<*, *>> }
            ?.firstOrNull()
            ?.get("text") as? String ?: "No answer"

        // 5) simpan balasan
        mem += mapOf("role" to "model", "parts" to listOf(mapOf("text" to reply)))

        // 6) trim history ke N terakhir (misal 20 pesan total)
        val maxMessages = 20
        if (mem.size > maxMessages) {
            val drop = mem.size - maxMessages
            repeat(drop) { mem.removeAt(0) }
        }

        return reply
    }

    private val openAiUrl = "https://api.openai.com/v1/chat/completions"
    private val gptModel = config.gptModel // atau "gpt-4.1", "gpt-4o-mini", dsb (sesuaikan kuota & kebutuhan)
    private val openAiKey = config.gptToken

    // ==== Util: konversi riwayat internal -> messages OpenAI ====
    private fun toOpenAiMessages(staticContext: String, mem: List<Map<String, *>>): List<Map<String, *>> {
        val msgs = mutableListOf<Map<String, *>>()
        // system prompt dulu
        msgs += mapOf("role" to "system", "content" to staticContext)

        // lalu seluruh mem (user/model) jadi user/assistant
        mem.forEach { m ->
            val role = (m["role"] as? String)?.lowercase()
            val text = ((m["parts"] as? List<Map<*, *>>)?.firstOrNull()?.get("text") as? String).orEmpty()
            val mappedRole = when (role) {
                "user" -> "user"
                "model" -> "assistant"
                else -> "user"
            }
            msgs += mapOf("role" to mappedRole, "content" to text)
        }
        return msgs
    }


    // ==== Panggilan ke OpenAI Chat Completions ====
    override fun aiGptAksSend(prompt: String, userId: String): Any {
        val mem = history.computeIfAbsent(userId) { mutableListOf() }

        // 1) simpan prompt asli
        mem += mapOf("role" to "user", "parts" to listOf(mapOf("text" to prompt)))

        // 2) trigger "cari/search"
        val query = extractSearchQuery(prompt)
        val enrichedPrompt = if (query != null) {
            val searchResults = quickSearch(query)
            "$prompt\n\nHasil penelusuran daring singkat:\n$searchResults"
        } else prompt

        // ganti isi pesan terakhir dgn versi enriched
        mem[mem.lastIndex] = mapOf("role" to "user", "parts" to listOf(mapOf("text" to enrichedPrompt)))

        // 3) susun messages untuk OpenAI (system + history ter-update)
        val messages = toOpenAiMessages(staticContext, mem)

        // 4) request ke OpenAI
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            add("Authorization", "Bearer $openAiKey") // simpan aman di env/secret
        }
        val body = mapOf(
            "model" to gptModel,                   // contoh: "gpt-5" / "gpt-4o"
            "messages" to messages,
            "temperature" to 0.3,
            "max_tokens" to 1024
        )

        val entity = HttpEntity(body, headers)

        val res: Map<*, *> = try {
            restTemplate.postForObject(openAiUrl, entity, Map::class.java)
                ?: return "No answer"
        } catch (e: org.springframework.web.client.HttpStatusCodeException) {
            val status = e.statusCode.value()
            val msg = e.responseBodyAsString
            // 401: auth; 429: rate limit/quota; lainnya: tampilkan ringkas
            return when (status) {
                401 -> "OpenAI: Unauthorized (periksa API key)."
                429 -> "OpenAI: Rate limit/quota terlampaui. Coba lagi nanti."
                else -> "OpenAI error $status: ${msg.take(400)}"
            }
        } catch (e: Exception) {
            return "OpenAI error: ${e.message}"
        }

        // 5) ambil teks jawaban
        @Suppress("UNCHECKED_CAST")
        val choices = res["choices"] as? List<Map<String, *>>
        val content = choices
            ?.firstOrNull()
            ?.get("message") as? Map<*, *>
        val reply = content?.get("content") as? String ?: "No answer"

        // 6) simpan balasan dan trim history
        mem += mapOf("role" to "model", "parts" to listOf(mapOf("text" to reply)))
        val maxMessages = 20
        if (mem.size > maxMessages) {
            val drop = mem.size - maxMessages
            repeat(drop) { mem.removeAt(0) }
        }

        return reply
    }

    // ---- Helper: ekstrak query "cari/search ..." dari prompt ----
    private fun extractSearchQuery(prompt: String): String? {
        // Bersihin timestamp/log umum biar nggak ikut ke query
        val cleaned = prompt
            .replace(Regex("""\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.\d+\+\d{2}:\d{2}"""), " ")
            .replace(Regex("""\b(INFO|WARN|ERROR|DEBUG)\b.*"""), " ")
            .replace(Regex("""\s+"""), " ")
            .trim()

        // Pola yang didukung:
        // "cari apa itu kotlin", "cari: apa itu kotlin", 'cari "apa itu kotlin"'
        // "search how to ...", "search: how to ..."
        val patterns = listOf(
            Regex("""(?i)\b(cari|search)\s*:\s*\"([^\"]+)\""""),
            Regex("""(?i)\b(cari|search)\s+\"([^\"]+)\""""),
            Regex("""(?i)\b(cari|search)\s*:\s*(.+)$"""),
            Regex("""(?i)\b(cari|search)\s+(.+)$""")
        )
        for (p in patterns) {
            val m = p.find(cleaned)
            if (m != null) {
                val grp = m.groupValues.last().trim()
                if (grp.isNotBlank()) return grp
            }
        }
        return null
    }




    // ---- Perkuat quickSearch dengan UA, selector fallback, dan DDG fallback ----
    fun quickSearch(query: String): String {
        if (query.isBlank()) return "Tidak ada kueri pencarian."

        val url = "https://www.google.com/search?q=${query.urlEncode()}&num=3&hl=id"
        val doc = JsoupHelper.fetchDocument(
            url,
            userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36",
            timeout = 7000
        )

        // Selector utama + fallback (Google sering berubah)
        val blocks = doc.select("div.tF2Cxc").ifEmpty { doc.select("div.g") }

        var results = blocks.take(3).joinToString("\n") {
            val title = it.selectFirst("h3")?.text().orEmpty()
            // beberapa layout pakai div.VwiC3b atau span aCOpRe
            val snippet = it.selectFirst("div.VwiC3b, div.IsZvec, span.aCOpRe")?.text().orEmpty()
            "- ${title.ifBlank { "(tanpa judul)" }}: ${snippet.ifBlank { "(tanpa ringkasan)" }}"
        }.trim()

        if (results.isBlank()) {
            // Fallback DuckDuckGo HTML (tanpa API key)
            val duckUrl = "https://duckduckgo.com/html/?q=${query.urlEncode()}"
            val duckDoc = JsoupHelper.fetchDocument(duckUrl)
            results = duckDoc.select("a.result__a, a.result__title").take(3).joinToString("\n") {
                "- ${it.text()}"
            }.trim()
        }

        return results.ifBlank { "Tidak ada hasil pencarian" }
    }

    /* --- util URL encode --- */
    private fun String.urlEncode() =
        java.net.URLEncoder.encode(this, Charsets.UTF_8)
}