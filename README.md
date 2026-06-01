## 💰🧾 SIGARKA (Sistem Gaji Karyawan)
---

## 📝 Deskripsi Aplikasi
**_SiGarKa_** adalah aplikasi manajemen bertema _Sumber Daya Manusia (SDM)_ berbasis desktop yang dirancang khusus untuk mengelola data karyawan dan otomatisasi perhitungan gaji. Aplikasi ini menggabungkan antarmuka modern menggunakan _JavaFX_ dengan sistem penyimpanan data lokal _SQLite_. Fokus utamanya adalah memberikan kemudahan bagi admin HRD dalam <sup>1</sup>Menyimpan data karyawan, <sup>2</sup>mencatat kehadiran, <sup>3</sup>menghitung bonus/potongan secara akurat, dan <sup>4</sup>mencetak slip gaji profesional.

---

## ✨ Fitur Aplikasi
```
🛡️  Sistem Keamanan Login
 └── Membatasi akses aplikasi hanya untuk Admin.

👥  Manajemen Data Karyawan
 ├── Tambah Karyawan Baru (Validasi ID 5 Angka)
 ├── Lihat Daftar Karyawan (Pengurutan Manager > Staf)
 └── Hapus Data Karyawan (Konfirmasi Keamanan)

💸  KALKULASI GAJI OTOMATIS
 ├── Hitung Gaji Tetap (Gaji Pokok + Lembur + Tunjangan)
 ├── Hitung Gaji Kontrak (Total Jam Kerja × Tarif)
 └── Pengurangan Otomatis (Alfa & Izin)

🏆  SISTEM BADGE PRESTASI (BONUS)
 ├── Super Productive (Lembur ≥ 5 Hari)
 └── Discipline Master (Kehadiran 100%)

📄  MANAJEMEN SLIP GAJI
 ├── Penyimpanan Riwayat Gaji per Periode
 ├── Pratinjau Visual Slip Gaji (Format A4)
 └── Pencetakan Langsung ke Printer Hardware

📊  STATISTIK REAL-TIME (NEW)
 └── Ringkasan data karyawan yang tersimpan di database SQLite
```

---

## ⚙️ Cara Menjalankan Aplikasi

**Prasyarat:** Pastikan JDK 21 atau versi yang kompatibel sudah terinstall.

**1. Clone repository:**
```bash
git clone https://github.com/faticharaisyah258/OOP-3-SIGARKA.git
```

**2. Pindah ke direktori proyek:**
```bash
cd OOP-3-SIGARKA
```

**3. Jalankan aplikasi:**
```bash
# Windows
.\gradlew run

# macOS / Linux
./gradlew run
```

**4. Gunakan kredensial berikut untuk masuk:**
```
    Username: admin
    Password: 123
```

---

## 🏛️ Struktur Kode
```
SiGarKa\
├── build.gradle
|
└── app\src\main\java\sigarka\
    ├── MainApp.java
    │
    ├── database\
    │   └── KoneksiDatabase.java
    │
    ├── models\
    │   ├── Karyawan.java
    │   ├── KaryawanTetap.java
    │   └── KaryawanKontrak.java
    |
    ├── repository\
    │   ├── KaryawanRepo.java
    │   └── GajiRepo.java
    │
    ├── scenes\
    │   ├── LoginSc.java
    │   ├── LogoutSc.java
    │   ├── MenuSc.java
    │   │
    │   ├── hitung\
    │   │   └── HitungGajiSc.java
    │   │
    │   ├── karyawan\
    │   │   ├── KelolaKaryawanSc.java
    │   │   ├── TambahKaryawanSc.java
    │   │   └── HapusKaryawanSc.java 
    │   │
    │   └── slip\
    │       ├── SlipGajiSc.java
    │       └── DesainSlip.java
    │
    └── View\
        ├── AppStyle.java
        └── IconLoader.java
```
- **Database:** _Menangani koneksi teknis ke SQLite, pembuatan tabel awal, dan pembaruan struktur database secara otomatis._
- **Models:** _Struktur objek data (atribut) dan memproses logika perhitungan gaji._
- **Repository:** _Mengelola seluruh perintah SQL (Query) untuk proses interaksi data antara database dan aplikasi._
- **Scenes:** _Mengatur UI, alur perpindahan halaman, serta menangani interaksi dari tombol atau inputan._
- **View:** _Menyimpan gaya visual (warna/font) dan mengelola pemuatan aset eksternal seperti logo dan ikon._

---

## ✍️ Penerapan 4 Pilar OOP
**A. Inheritance**
- _Kelas_ `KaryawanTetap` _dan_ `KaryawanKontrak` _mewarisi atribut umum_ (`ID`, `Nama`, `Tipe`) _dari satu induk kelas yaitu_ `Karyawan`.

**B. Abstraction**
- _Menggunakan abstract class_ `Karyawan` _dan abstract method_ `hitungGaji()` _untuk menyembunyikan rincian perhitungan yang berbeda-beda, namun mewajibkan setiap tipe karyawan memiliki fungsi_ `hitungGaji()`.

**C. Polymorphism**
- _Method_ `hitungGaji()` _memiliki banyak bentuk perilaku; satu perintah yang sama akan menghasilkan perhitungan berbeda tergantung apakah objeknya karyawan Tetap atau Kontrak._

**D. Encapsulation**
- _Melindungi data dengan akses_ `private` _pada atribut_ (_seperti_ `gajiPokok`) _dan menyediakan method Getter/Setter untuk mengaksesnya secara aman dan terkontrol._

---

## 📒 Pembagian Tugas

| Nama                                  | Tugas Utama                                                                              |
|---------------------------------------|------------------------------------------------------------------------------------------|
| Salwa Ainiyyah (H071251032)           | Tampilan & Alur Pengguna, mengatur semua UI folder `scenes\` (font, image, color, padding, dll) |
| Faticha Raisyah Bachtiar (H071251057) | Mengelola struktur basis data serta alur siklus hidup data karyawan dalam sistem.          |
| Muhammad Rayyan Izdihar (H071251089)  | Menangani logika penggajian dan slip gaji, serta database riwayat gaji                    |