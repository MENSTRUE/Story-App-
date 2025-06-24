# 📖 Story-App-

Story App by Dicoding  
Aplikasi Android untuk berbagi cerita dengan fitur autentikasi, daftar cerita, unggah cerita, dan animasi. Dibuat sebagai bagian dari submission kelas Dicoding: *Intermediate Android App Development*.

---

## 🧩 Fitur Utama

### 🔐 Autentikasi
- Halaman **Login**
  - Input:
    - Email → `R.id.ed_login_email`
    - Password → `R.id.ed_login_password` (dengan validasi min. 8 karakter)
  - Custom EditText: tampilkan error langsung jika password kurang dari 8 karakter
- Halaman **Register**
  - Input:
    - Nama → `R.id.ed_register_name`
    - Email → `R.id.ed_register_email`
    - Password → `R.id.ed_register_password`
  - Password disembunyikan secara default
  - Validasi email dan password menggunakan Custom View
- **Session Management**
  - Menyimpan token di `DataStore`
  - Jika login berhasil → langsung ke halaman utama
  - Jika belum login → masuk ke halaman login
  - Logout via menu `R.id.action_logout` (menghapus token & sesi)

---

### 📄 Daftar Cerita
- Menampilkan list cerita dari API
  - Nama user → `R.id.tv_item_name`
  - Foto user → `R.id.iv_item_photo`
- Klik salah satu item → ke halaman detail:
  - Nama → `R.id.tv_detail_name`
  - Foto → `R.id.iv_detail_photo`
  - Deskripsi → `R.id.tv_detail_description`

---

### ➕ Tambah Cerita
- Halaman untuk menambah cerita baru
  - Input:
    - Gambar dari **Gallery**
    - Deskripsi → `R.id.ed_add_description`
  - Upload via tombol → `R.id.button_add`
  - Setelah upload → kembali ke list & cerita baru muncul di atas
  - Mendukung juga pengambilan foto dari **Camera**

---

### 🎞️ Animasi
- Menggunakan salah satu dari:
  - Property Animation
  - Motion Animation
  - Shared Element Transition
- Detail lokasi animasi dijelaskan di Student Note

---

## 💡 Saran Peningkatan
Untuk mendapatkan penilaian submission terbaik:
- Bersihkan kode (hapus comment/import tidak terpakai, rapikan indentasi)
- Gunakan Custom View juga untuk validasi email
- Tambahkan informasi:
  - Loading saat fetch/upload
  - Error saat API gagal
  - Pesan saat data kosong
- Implementasi arsitektur Android dengan **ViewModel + LiveData**
- Navigasi yang tepat:
  - Setelah login/upload/logout, tombol back tidak kembali ke state sebelumnya
- Tambahkan opsi multi-bahasa (Localization)
- Gunakan `Stack Widget` untuk daftar cerita (jika memungkinkan)

---

## 🚫 Submission Ditolak Jika:
- Register/login gagal
- Custom View tidak bekerja
- Daftar/detail cerita tidak tampil
- Tambah cerita tidak lancar / data tidak muncul
- Logout tidak menghapus sesi/token
- Menggunakan animasi yang tidak sesuai
- Project tidak bisa di-build / force closed
- Mengirim file non-Android Studio atau bukan karya sendiri

---

## 🔗 API Referensi
- Dokumentasi: [https://story-api.dicoding.dev/v1](https://story-api.dicoding.dev/v1)
- Max size upload: 1MB (kompres jika perlu)
- Token login wajib disimpan untuk akses endpoint lain

---

## 🧪 Tips Urutan Pengerjaan
1. Buat halaman login & register
2. Tambahkan validasi CustomView (password & email)
3. Hubungkan register & login ke API
4. Simpan token di DataStore
5. Tampilkan daftar cerita
6. Buat halaman detail cerita
7. Tambahkan halaman unggah cerita (gallery/camera)
8. Implementasi animasi & UX improvement

---

## 📣 Forum Diskusi
Jika mengalami kendala, diskusikan di forum resmi Dicoding:  
👉 https://www.dicoding.com/academies/352/discussions

---

## 🧑‍💻 Kontributor
Wafa Bila Syaefurokhman  
Student at Dicoding Academy  
