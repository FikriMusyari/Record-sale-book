# Record - Queue Management System

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)

## 📱 Overview

**Record** adalah aplikasi Android modern untuk manajemen sistem antrian yang dibangun dengan teknologi terkini. Aplikasi ini memungkinkan pengguna untuk mengelola pelanggan, produk, dan antrian dengan interface yang intuitif dan responsif.

## ✨ Features

### 🔐 Authentication
- **Login & Register** - Sistem autentikasi pengguna yang aman
- **User Management** - Profil pengguna dan pengaturan akun
- **Secure Token Storage** - Penyimpanan token yang aman menggunakan Android Security Crypto

### 👥 Customer Management
- **Customer CRUD** - Tambah, lihat, edit, dan hapus data pelanggan
- **Customer Search** - Pencarian pelanggan berdasarkan nama
- **Balance Tracking** - Pelacakan saldo pelanggan

### 📦 Product Management
- **Product CRUD** - Manajemen produk lengkap
- **Product Search** - Pencarian produk berdasarkan nama
- **Price Management** - Pengaturan harga produk

### 📋 Queue Management
- **Queue Creation** - Pembuatan antrian baru dengan multiple produk
- **Order Items** - Pengelolaan item pesanan dengan quantity dan diskon
- **Status Tracking** - Pelacakan status antrian (pending, in progress, completed)
- **Payment Integration** - Integrasi sistem pembayaran
- **Queue History** - Riwayat antrian dan transaksi

### 📊 Dashboard
- **Metrics Overview** - Ringkasan metrik bisnis
- **Recent Activities** - Aktivitas terbaru
- **Quick Actions** - Akses cepat ke fitur utama

## 🏗️ Architecture

Aplikasi ini menggunakan **Clean Architecture** dengan pola **MVVM (Model-View-ViewModel)**:

```
app/
├── data/                    # Data Layer
│   ├── remotes/            # API Services
│   └── repositoryImpl/     # Repository Implementations
├── domain/                 # Domain Layer
│   ├── models/            # Data Models & DTOs
│   ├── repository/        # Repository Interfaces
│   └── useCase/          # Business Logic
└── presentation/          # Presentation Layer
    ├── screen/           # UI Screens (Compose)
    ├── viewmodel/       # ViewModels
    └── ui/              # UI Components & Theme
```

## 🛠️ Tech Stack

### Core Technologies
- **Kotlin** - Bahasa pemrograman utama
- **Jetpack Compose** - Modern UI toolkit untuk Android
- **Android Architecture Components** - ViewModel, LiveData, Navigation

### Dependency Injection
- **Dagger Hilt** - Dependency injection framework

### Networking
- **Retrofit** - HTTP client untuk API calls
- **OkHttp** - HTTP client dengan logging interceptor
- **Gson** - JSON serialization/deserialization

### Security
- **Android Security Crypto** - Encrypted SharedPreferences untuk token storage

## 📋 Requirements

- **Android SDK**: API 28+ (Android 9.0)
- **Target SDK**: API 35 (Android 15)
- **Kotlin**: 2.0.21
- **Gradle**: 8.9.2


## 🤝 Contributing
- Fikri Musyari Taufiq (12350110187)
- Ari Gumilang (12350110349)
- Irfan Nurfathoni Putra (12350110028)
- Muhammad Hammam Zuhdi (12250111977)

