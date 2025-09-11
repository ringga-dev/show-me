package ngga.ring.myblog.data.auth.db

import com.github.lalyos.jfiglet.FigletFont
import jakarta.transaction.Transactional
import ngga.ring.myblog.data.auth.service.AuthService
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Default {

    companion object {
        private const val GREEN = "\u001B[32m"
        private const val RED = "\u001B[31m"
        private const val CYAN = "\u001B[36m"
        private const val RESET = "\u001B[0m"
        private const val BOLD = "\u001B[1m"
    }

    @Bean
    @Transactional
    fun initData(authService: AuthService) = ApplicationRunner {
        try {
            val data = authService.postRegisterUser(
                userName = "admin",
                email = "admin.dev@gmail.com",
                password = "@Sasuke97",
                confirmPassword = "@Sasuke97",
                role = listOf("admin", "user"),
                status = "ACTIVE",
                isVerified = true,
            )

            printBoxSuccess(
                username = data.data?.userName ?: "admin",
                email = data.data?.email ?: "admin.dev@gmail.com",
                role = data.data?.role ?: listOf("admin", "user"),
                status = data.data?.status ?: "ACTIVE"
            )

        } catch (e: Exception) {
            printBoxError(e.message ?: "Terjadi kesalahan saat setup akun default")
        }
    }


    private fun printBoxSuccess(username: String, email: String, role: List<String>, status: String) {
        // Fungsi untuk membuat warna gradien


        val GREEN = "\u001B[32m"
        val CYAN = "\u001B[36m"
        val RESET = "\u001B[0m"
        val BOLD = "\u001B[1m"

        // Banner ASCII
        val ascii = FigletFont.convertOneLine("Welcome On Board")
        println(colorGradient(ascii, 0, 255, 0, 0, 200, 255))

        // Sambutan
        println("${BOLD}🎉 Selamat datang di ${CYAN}Blog Apps${RESET}! 🎉")
        println("Akun default berhasil dibuat dan siap digunakan.\n")

        // Informasi akun
        val box = """
╔════════════════════════════════════════════════════════╗
║   ✅ ${BOLD}${GREEN}SETUP AKUN DEFAULT BERHASIL${RESET}                        ║
╠════════════════════════════════════════════════════════╣
║  👤 ${CYAN}Username${RESET} : $username
║  📧 ${CYAN}Email${RESET}    : $email
║  🛡️ ${CYAN}Role${RESET}     : $role
║  📌 ${CYAN}Status${RESET}   : $status
╚════════════════════════════════════════════════════════╝
""".trimIndent()
        println(box)

        // Login info
        println(
            "🔑 Login dengan:\n" +
                    "   ${CYAN}Email${RESET} : ${colorGradient(email, 0, 255, 0, 0, 200, 255)}\n" +
                    "   ${CYAN}Password${RESET} : ${colorGradient("@Sasuke97", 0, 255, 0, 0, 200, 255)}"
        )

        // Langkah selanjutnya
        println("\n📌 ${BOLD}Langkah Selanjutnya:${RESET}")
        println("1️⃣ Login ke aplikasi menggunakan akun di atas.")
        println("2️⃣ Ganti password default di menu profil untuk keamanan.")
        println("3️⃣ Mulai eksplorasi dan buat konten blog pertamamu!")
    }


    private fun colorGradient(text: String, r1: Int, g1: Int, b1: Int, r2: Int, g2: Int, b2: Int): String {
        val result = StringBuilder()
        for (i in text.indices) {
            val ratio = i.toFloat() / (text.length - 1).coerceAtLeast(1)
            val r = (r1 + ratio * (r2 - r1)).toInt()
            val g = (g1 + ratio * (g2 - g1)).toInt()
            val b = (b1 + ratio * (b2 - b1)).toInt()
            result.append("\u001B[38;2;${r};${g};${b}m${text[i]}")
        }
        return result.append("\u001B[0m").toString()
    }

    private fun printBoxError(errorMessage: String) {
        val ascii = FigletFont.convertOneLine("Welcome To Board")
        println(colorGradient("$ascii", 0, 255, 0, 0, 200, 255))
        println("Note: $errorMessage")
        println(
            "Login By: " + colorGradient("admin.dev@gmail.com", 0, 255, 0, 0, 200, 255)
        )

        val ascii2 = FigletFont.convertOneLine("          HAPPY HACKING        ")
        println(colorGradient("$ascii2", 0, 255, 0, 0, 200, 255))
    }
}
