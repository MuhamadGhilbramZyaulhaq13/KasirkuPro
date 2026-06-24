# KasirKu Pro 🛒

KasirKu Pro adalah aplikasi Kasir (*Point of Sale*) berbasis desktop yang dirancang khusus untuk mempermudah operasional toko ritel. Aplikasi ini menyediakan solusi lengkap mulai dari manajemen inventaris barang hingga pencatatan transaksi penjualan secara *real-time*, didukung oleh sistem basis data lokal yang aman dan cepat.

## 🌟 Fitur Utama

* **Manajemen Stok Barang:** Sistem CRUD (*Create, Read, Update, Delete*) penuh untuk mengelola daftar barang dagangan, harga, dan ketersediaan stok.
 <img width="1171" height="867" alt="Screenshot 2026-06-24 205553" src="https://github.com/user-attachments/assets/546258a9-e4be-4839-ad65-b990bc9cc743" />

* **Transaksi Kasir Otomatis:** Antarmuka kasir yang responsif dengan kalkulasi total belanja dan uang kembalian secara otomatis.
 <img width="1171" height="867" alt="Screenshot 2026-06-24 205421" src="https://github.com/user-attachments/assets/70ed355c-c17c-4d97-b514-e13872451c51" />

* **Laporan Penjualan:** Pemantauan omzet dan riwayat transaksi harian.
<img width="1171" height="867" alt="Screenshot 2026-06-24 205618" src="https://github.com/user-attachments/assets/08cdf0f0-3913-427e-909d-67a0b02b01aa" />

* **Basis Data Terintegrasi:** Menggunakan SQLite yang berjalan sepenuhnya di latar belakang tanpa memerlukan instalasi server tambahan oleh pengguna.
* **Standalone Installer:** Tersedia dalam bentuk *installer* Windows (`.msi`) yang sudah dilengkapi dengan Java Runtime bawaan.

## 💻 Teknologi yang Digunakan

* **Bahasa Pemrograman:** Java (Classic)
* **Antarmuka Pengguna (GUI):** Java Swing / AWT
* **Basis Data:** SQLite (JDBC Driver)
* **Build & Packaging:** IntelliJ IDEA Artifacts & `jpackage` (WiX Toolset)
* **Versi Java Target:** JDK 25 (Eclipse Temurin LTS)

## 📥 Cara Instalasi (Untuk Pengguna Umum)

Jika Anda hanya ingin menggunakan aplikasi ini tanpa melihat kode sumbernya:

1. Buka halaman [Releases](../../releases) di *repository* ini.
2. Unduh file instalasi terbaru bernama `KasirKuPro-1.0.msi`.
3. Jalankan file yang diunduh dan ikuti instruksi instalasi di layar.
4. **Penting:** Setelah terinstal, klik kanan pada *shortcut* aplikasi KasirKu Pro di Desktop, pilih **Properties** -> tab **Compatibility** -> centang **"Run this program as an administrator"** untuk memberikan izin pengelolaan basis data.
5. Aplikasi siap digunakan!

## 🛠️ Cara Menjalankan Kode (Untuk Developer)

Jika Anda ingin mengembangkan atau memodifikasi aplikasi ini:

1. Clone *repository* ini ke mesin lokal Anda:
   ```bash
   git clone [https://github.com/USERNAME_ANDA/KasirKuPro.git](https://github.com/USERNAME_ANDA/KasirKuPro.git)
