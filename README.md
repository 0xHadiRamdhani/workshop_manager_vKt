# Workshop Manager - Bengkel Sekolah

Aplikasi manajemen bengkel sekolah berbasis Android yang dikembangkan dengan Kotlin untuk mengelola siswa, alat, dan sesi workshop secara efisien.

## 📋 Deskripsi

Workshop Manager adalah aplikasi manajemen lengkap untuk bengkel sekolah yang memungkinkan pengelolaan siswa, inventaris alat, dan sesi workshop dalam satu platform terintegrasi. Aplikasi ini dirancang khusus untuk memenuhi kebutuhan bengkel sekolah menengah kejuruan dan perguruan tinggi.

## ✨ Fitur Utama

### 🎯 Manajemen Siswa
- ✅ Tambah, edit, dan hapus data siswa
- ✅ Informasi lengkap: NIS, nama, kelas, email, telepon, alamat
- ✅ Status aktif/non-aktif siswa
- ✅ Pencarian dan filter data siswa

### 🔧 Manajemen Alat
- ✅ Inventaris alat lengkap dengan kode, nama, kategori
- ✅ Status ketersediaan alat (Tersedia/Dipinjam/Rusak)
- ✅ Informasi kondisi alat (Excellent/Good/Fair/Poor/Broken)
- ✅ Lokasi penyimpanan alat
- ✅ Riwayat maintenance dan pembelian

### 📅 Manajemen Sesi Workshop
- ✅ Penjadwalan sesi workshop
- ✅ Alokasi siswa dan alat untuk setiap sesi
- ✅ Status sesi (Scheduled/In Progress/Completed/Cancelled/No Show)
- ✅ Briefing keselamatan sebelum sesi
- ✅ Catatan dan dokumentasi proyek

### 📊 Dashboard dan Statistik
- ✅ Ringkasan jumlah siswa aktif
- ✅ Status ketersediaan alat
- ✅ Jadwal sesi hari ini
- ✅ Aksi cepat untuk operasi umum

### 💾 Teknologi yang Digunakan
- **Bahasa**: Kotlin
- **Arsitektur**: MVVM (Model-View-ViewModel)
- **Database**: Room Persistence Library
- **UI**: Material Design Components
- **Navigation**: Navigation Drawer
- **Async**: Coroutines dan LiveData

## 🏗️ Arsitektur Aplikasi

```
app/
├── models/           # Data models (Student, Tool, WorkshopSession)
├── database/         # Room database setup dan DAOs
├── repositories/     # Data access layer
├── viewmodels/       # Business logic dan state management
├── fragments/        # UI components
├── adapters/         # RecyclerView adapters
├── utils/            # Utility classes
└── MainActivity.kt   # Main activity dengan navigation drawer
```

## 🛠️ Persyaratan Sistem

- **Minimum SDK**: Android 24 (Android 7.0)
- **Target SDK**: Android 36
- **Java Version**: 11
- **Kotlin Version**: 2.0.21

## 📦 Dependensi Utama

```kotlin
// Android Core
implementation("androidx.core:core-ktx:1.10.1")
implementation("androidx.appcompat:appcompat:1.6.1")

// Material Design
implementation("com.google.android.material:material:1.10.0")

// Room Database
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
kapt("androidx.room:room-runtime:2.6.1")

// Lifecycle & ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

// UI Components
implementation("androidx.recyclerview:recyclerview:1.3.2")
implementation("androidx.cardview:cardview:1.0.0")
```

## 🚀 Instalasi dan Penggunaan

### 1. Clone Repository
```bash
git clone https://github.com/username/workshop-manager.git
cd workshop-manager
```

### 2. Buka di Android Studio
- Buka Android Studio
- Pilih "Open an existing Android Studio project"
- Navigasi ke folder project dan pilih folder root

### 3. Build dan Run
- Tunggu hingga Gradle sync selesai
- Klik tombol "Run" atau tekan `Shift+F10`
- Pilih emulator atau device yang tersedia

### 4. Penggunaan Awal
1. Aplikasi akan membuka di halaman Dashboard
2. Gunakan navigation drawer untuk mengakses fitur:
   - **Manajemen Siswa**: Kelola data siswa
   - **Manajemen Alat**: Kelola inventaris alat
   - **Sesi Workshop**: Kelola jadwal dan sesi workshop

## 📱 Tampilan Aplikasi

### Dashboard
- Ringkasan statistik harian
- Quick actions untuk operasi cepat
- Status sesi workshop hari ini

### Manajemen Siswa
- Form tambah/edit siswa
- Daftar siswa dengan search dan filter
- Detail informasi siswa

### Manajemen Alat
- Form tambah/edit alat
- Daftar alat dengan status ketersediaan
- Informasi detail kondisi alat

### Manajemen Sesi Workshop
- Form pembuatan sesi baru
- Alokasi alat untuk sesi
- Status dan monitoring sesi

## 🔧 Struktur Database

### Tabel Students
```sql
CREATE TABLE students (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    studentId TEXT NOT NULL,
    fullName TEXT NOT NULL,
    className TEXT NOT NULL,
    email TEXT,
    phone TEXT,
    address TEXT,
    createdAt DATE,
    updatedAt DATE
);
```

### Tabel Tools
```sql
CREATE TABLE tools (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    toolCode TEXT NOT NULL,
    toolName TEXT NOT NULL,
    category TEXT NOT NULL,
    description TEXT,
    quantity INTEGER DEFAULT 1,
    status TEXT DEFAULT 'Tersedia',
    purchaseDate DATE,
    lastMaintenanceDate DATE,
    location TEXT,
    isActive BOOLEAN DEFAULT true,
    createdAt DATE,
    updatedAt DATE
);
```

### Tabel WorkshopSessions
```sql
CREATE TABLE workshop_sessions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    studentId INTEGER,
    sessionDate DATE,
    startTime TEXT,
    endTime TEXT,
    projectName TEXT,
    projectDescription TEXT,
    toolsUsed TEXT,
    instructorName TEXT,
    safetyBriefingCompleted BOOLEAN,
    sessionStatus TEXT,
    notes TEXT,
    createdAt DATE,
    updatedAt DATE,
    FOREIGN KEY (studentId) REFERENCES students(id) ON DELETE CASCADE
);
```

## 🎯 Penggunaan di Sekolah

Aplikasi ini cocok untuk:
- **SMK Teknik**: Mengelola bengkel otomotif, mesin, elektronika
- **Perguruan Tinggi**: Laboratorium teknik dan workshop
- **Sekolah Menengah**: Program kejuruan dengan fasilitas workshop
- **Lembaga Pelatihan**: Kursus teknik dan vokasional

## 🔒 Keamanan dan Privasi

- Data tersimpan secara lokal di device
- Tidak ada pengiriman data ke server eksternal
- Backup dan restore data tersedia
- Enkripsi database dengan Room

## 🐛 Troubleshooting

### Masalah Umum:
1. **Build gagal**: Pastikan Android Studio dan Gradle versi terbaru
2. **Database error**: Clear app data dan restart aplikasi
3. **UI tidak responsive**: Cek logcat untuk error spesifik

### Solusi:
- Update Android Studio ke versi terbaru
- Sync project dengan Gradle files
- Clean dan rebuild project
- Restart device/emulator

## 🤝 Kontribusi

Kontribusi sangat welcome! Silakan:
1. Fork repository ini
2. Buat branch fitur baru (`git checkout -b fitur-baru`)
3. Commit perubahan (`git commit -m 'Menambahkan fitur baru'`)
4. Push ke branch (`git push origin fitur-baru`)
5. Buat Pull Request

## 📄 Lisensi

Proyek ini dilisensikan di bawah MIT License - lihat file [LICENSE](LICENSE) untuk detail.

## 📞 Kontak

Jika ada pertanyaan atau masalah:
- Email: support@workshopmanager.com
- Issues: [GitHub Issues](https://github.com/username/workshop-manager/issues)

---

**⭐ Jika aplikasi ini membantu, jangan lupa beri bintang di GitHub! ⭐**

Made with ❤️ untuk pendidikan teknik di Indonesia