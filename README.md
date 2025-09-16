

```markdown
# 📝 MyBlog App

[![Backend: Kotlin](https://img.shields.io/badge/Backend-Kotlin-orange?logo=kotlin)](https://kotlinlang.org/)  
[![Frontend: Nuxt 4](https://img.shields.io/badge/Frontend-Nuxt%204-green?logo=nuxt.js)](https://nuxt.com/)  
[![Database: MySQL](https://img.shields.io/badge/Database-MySQL-blue?logo=mysql)](https://www.mysql.com/)  
[![License: RSCAL](https://img.shields.io/badge/License-RSCAL%20v1.0-purple)](LICENSE)

Aplikasi **Blog & Portfolio Management** dengan **Spring Boot Kotlin (backend)** dan **Nuxt 4 (frontend)**.  
App ini berfungsi sebagai **media portofolio pribadi** sekaligus **blog management system** untuk artikel, project, dan aplikasi.

---

## 🚀 Fitur Utama

### ✍️ **Blog Management**
- CRUD artikel dengan editor Markdown
- Kategori & tag sistem
- SEO optimization (meta tags, structured data)
- Comment system dengan moderation
- Artikel scheduling (publish di waktu tertentu)

### 👤 **Portfolio Showcase**
- Daftar project/aplikasi dengan detail lengkap
- Showcase karya (nama, deskripsi, link, rating, gambar)
- Integrasi ke Google Play / Website / GitHub
- Project filtering berdasarkan teknologi/kategori
- Project timeline (urutkan berdasarkan tahun)

### 🔐 **Authentication & Authorization**
- Login/Register user dengan JWT
- Role-based access (Admin, Author, Viewer)
- Social login (Google, GitHub)
- Password reset functionality

### 📊 **Dashboard Management**
- Statistik posting, views, interaksi
- Manajemen pengguna & permissions
- Analytics integration (Google Analytics)
- Popular posts & projects tracking

### 🌗 **UI Modern**
- Dark mode & light mode dengan auto-detection
- Responsif untuk mobile & desktop
- PWA support (Progressive Web App)
- Reading time estimation
- Share functionality untuk artikel

---

### Teknologi Stack
- **Backend:** Kotlin + Spring Boot 3.x
- **Frontend:** Nuxt 4 + Vue 3 + TailwindCSS
- **Database:** MySQL / PostgreSQL
- **ORM:** Hibernate (JPA)
- **API:** RESTful JSON
- **Authentication:** JWT + Spring Security
- **Deployment:** Docker + Docker Compose

---

## 🚀 Cara Menjalankan

### Prasyarat
- JDK 17+
- Node.js 18+
- MySQL 8.0+ / PostgreSQL 14+
- Docker (opsional)

### Instalasi & Konfigurasi

1. **Clone Repository**
   ```bash
   git clone https://github.com/username/myblog-app.git
   cd myblog-app
   ```

2. **Backend Setup**
   ```bash
   cd backend
   ./gradlew build
   # Konfigurasi database di application.yml
   java -jar build/libs/myblog-app-0.0.1.jar
   ```

3. **Frontend Setup**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

4. **Database Setup**
   ```bash
   # Buat database
   mysql -u root -p -e "CREATE DATABASE myblog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
   
   # Import schema
   mysql -u root -p myblog < database/schema.sql
   
   # Import initial data
   mysql -u root -p myblog < database/seed.sql
   ```

### Dengan Docker
```bash
# Jalankan semua service
docker-compose up -d

# Lihat logs
docker-compose logs -f
```

---

## 📄 Lisensi

Proyek ini dilisensikan di bawah **Ringga Source Code Attribution License (RSCAL v1.0)**.

### Ketentuan Penting:
- ✅ **Gratis untuk digunakan** (komersial/non-komersial)
- ✅ **Boleh dimodifikasi** dengan syarat mencantumkan atribusi
- ❌ **Wajib menyertakan kode sumber** dalam setiap distribusi
- ❌ **Tidak boleh menghapus atribusi** pengembang asli

Lihat file [LICENSE.md](LICENSE.md) untuk detail lengkap.

---

## 🤝 Kontribusi

Kontribusi sangat diterima! Untuk berkontribusi:

1. Fork proyek ini
2. Buat branch fitur baru (`git checkout -b fitur-baru`)
3. Commit perubahan Anda (`git commit -am 'Menambah fitur baru'`)
4. Push ke branch (`git push origin fitur-baru`)
5. Buat Pull Request

### Panduan Kontribusi
- Ikuti standar kode yang sudah ada
- Tambahkan tes untuk fitur baru
- Update dokumentasi yang relevan
- Pastikan semua tes lulus sebelum PR

---

## 📞 Kontak

- **Pengembang:** Ringga-dev
- **Email:** [contact@ringga-dev.com](mailto:contact@ringga-dev.com)
- **Website:** [https://ringga-dev.com](https://ringga-dev.com)
- **GitHub:** [@ringga-dev](https://github.com/ringga-dev)

---

## 🙏 Atribusi

- **Icon:** [Heroicons](https://heroicons.com/) oleh Tailwind CSS
- **UI Components:** [Headless UI](https://headlessui.com/)
- **Markdown Editor:** [Tiptap](https://tiptap.dev/)
- **Database Schema:** Terinspirasi dari [Ghost CMS](https://ghost.org/)

---

## 📈 Roadmap

### v1.1 (Q3 2024)
- [ ] Multi-language support (i18n)
- [ ] Newsletter subscription
- [ ] Advanced analytics dashboard
- [ ] Project collaboration features

### v1.2 (Q4 2024)
- [ ] Mobile app (React Native)
- [ ] API rate limiting
- [ ] Advanced search with Elasticsearch
- [ ] Webhooks for integrations

---

## 📸 Screenshots

```