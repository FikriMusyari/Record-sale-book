package com.afi.record.domain.useCase

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Centralized error handling utility for the application
 */
object ErrorHandler {

    /**
     * Converts exceptions to user-friendly error messages
     */
    fun getErrorMessage(exception: Throwable): String {
        return when (exception) {
            is HttpException -> {
                when (exception.code()) {
                    400 -> "📝 Data tidak valid, periksa kembali input Anda"
                    401 -> "🔐 Sesi telah berakhir, silakan login kembali"
                    403 -> "🚫 Anda tidak memiliki akses untuk melakukan tindakan ini"
                    404 -> "🔍 Data yang dicari tidak ditemukan"
                    409 -> "⚠️ Data sudah ada atau konflik dengan data lain"
                    422 -> "📝 Data tidak dapat diproses, periksa format input"
                    429 -> "⏰ Terlalu banyak permintaan, coba lagi nanti"
                    500 -> "🔧 Terjadi kesalahan pada server, coba lagi nanti"
                    502, 503 -> "🌐 Server sedang maintenance, coba lagi nanti"
                    else -> "😵 Terjadi kesalahan: ${exception.message()}"
                }
            }
            is SocketTimeoutException -> "⏰ Koneksi timeout, periksa koneksi internet Anda"
            is UnknownHostException -> "🌐 Tidak dapat terhubung ke server, periksa koneksi internet"
            is IOException -> "🌐 Koneksi internet bermasalah, coba lagi"
            else -> {
                val message = exception.localizedMessage ?: exception.message ?: "Unknown error"
                when {
                    message.contains("timeout", ignoreCase = true) -> "⏰ Koneksi timeout, coba lagi"
                    message.contains("network", ignoreCase = true) -> "🌐 Koneksi internet bermasalah"
                    message.contains("401") -> "🔐 Sesi telah berakhir, silakan login kembali"
                    message.contains("400") -> "📝 Data tidak valid, periksa kembali"
                    message.contains("409") -> "⚠️ Data sudah ada atau konflik"
                    message.contains("500") -> "🔧 Terjadi kesalahan pada server"
                    else -> "😵 Terjadi kesalahan: $message"
                }
            }
        }
    }

    /**
     * Specific error messages for authentication operations
     */
    fun getAuthErrorMessage(exception: Throwable): String {
        return when (exception) {
            is HttpException -> {
                when (exception.code()) {
                    401 -> "❌ Email atau password salah"
                    409 -> "📧 Email sudah terdaftar"
                    422 -> "📝 Format email atau password tidak valid"
                    429 -> "⏰ Terlalu banyak percobaan login, coba lagi nanti"
                    else -> getErrorMessage(exception)
                }
            }
            else -> getErrorMessage(exception)
        }
    }

    /**
     * Specific error messages for customer operations
     */
    fun getCustomerErrorMessage(exception: Throwable, operation: String): String {
        val baseMessage = when (operation.lowercase()) {
            "create" -> "menambahkan pelanggan"
            "update" -> "memperbarui pelanggan"
            "delete" -> "menghapus pelanggan"
            "fetch" -> "mendapatkan data pelanggan"
            "search" -> "mencari pelanggan"
            else -> "memproses pelanggan"
        }

        return when (exception) {
            is HttpException -> {
                when (exception.code()) {
                    400 -> "📝 Data pelanggan tidak valid"
                    404 -> "🔍 Pelanggan tidak ditemukan"
                    409 -> "⚠️ Pelanggan dengan nama ini sudah ada"
                    else -> "😵 Gagal $baseMessage: ${getErrorMessage(exception)}"
                }
            }
            else -> "😵 Gagal $baseMessage: ${getErrorMessage(exception)}"
        }
    }

    /**
     * Specific error messages for product operations
     */
    fun getProductErrorMessage(exception: Throwable, operation: String): String {
        val baseMessage = when (operation.lowercase()) {
            "create" -> "menambahkan produk"
            "update" -> "memperbarui produk"
            "delete" -> "menghapus produk"
            "fetch" -> "mendapatkan data produk"
            "search" -> "mencari produk"
            else -> "memproses produk"
        }

        return when (exception) {
            is HttpException -> {
                when (exception.code()) {
                    400 -> "📝 Data produk tidak valid"
                    404 -> "🔍 Produk tidak ditemukan"
                    409 -> "⚠️ Produk dengan nama ini sudah ada"
                    else -> "😵 Gagal $baseMessage: ${getErrorMessage(exception)}"
                }
            }
            else -> "😵 Gagal $baseMessage: ${getErrorMessage(exception)}"
        }
    }

    /**
     * Specific error messages for queue operations
     */
    fun getQueueErrorMessage(exception: Throwable, operation: String): String {
        val baseMessage = when (operation.lowercase()) {
            "create" -> "membuat antrian"
            "update" -> "memperbarui antrian"
            "delete" -> "menghapus antrian"
            "fetch" -> "mendapatkan data antrian"
            else -> "memproses antrian"
        }

        return when (exception) {
            is HttpException -> {
                when (exception.code()) {
                    400 -> "📝 Data antrian tidak valid, periksa produk dan pelanggan"
                    404 -> "🔍 Antrian tidak ditemukan"
                    422 -> "📝 Tidak dapat memproses antrian, periksa stok produk"
                    else -> "😵 Gagal $baseMessage: ${getErrorMessage(exception)}"
                }
            }
            else -> "😵 Gagal $baseMessage: ${getErrorMessage(exception)}"
        }
    }

    /**
     * Validation error messages
     */
    object Validation {
        const val EMPTY_EMAIL = "📧 Email tidak boleh kosong"
        const val INVALID_EMAIL = "📧 Format email tidak valid"
        const val EMPTY_PASSWORD = "🔒 Password tidak boleh kosong"
        const val SHORT_PASSWORD = "🔒 Password minimal 6 karakter"
        const val EMPTY_NAME = "👤 Nama tidak boleh kosong"
        const val INVALID_BALANCE = "💰 Balance harus berupa angka yang valid"
        const val NEGATIVE_BALANCE = "💰 Balance tidak boleh negatif"
        const val INVALID_PRICE = "💰 Harga harus berupa angka yang valid"
        const val NEGATIVE_PRICE = "💰 Harga tidak boleh negatif"
        const val EMPTY_PRODUCT_NAME = "🏷️ Nama produk tidak boleh kosong"
        const val EMPTY_CUSTOMER_NAME = "👤 Nama pelanggan tidak boleh kosong"
        const val INVALID_QUANTITY = "📦 Jumlah harus berupa angka positif"
        const val EMPTY_SELECTION = "⚠️ Pilih minimal satu item"
    }

    /**
     * Success messages
     */
    object Success {
        const val LOGIN = "🎉 Selamat datang kembali!"
        const val REGISTER = "🎊 Akun berhasil dibuat!"
        const val LOGOUT = "👋 Sampai jumpa lagi!"
        const val PROFILE_UPDATED = "🎉 Profil berhasil diperbarui!"
        const val CUSTOMER_CREATED = "✅ Pelanggan berhasil ditambahkan!"
        const val CUSTOMER_UPDATED = "✅ Pelanggan berhasil diperbarui!"
        const val CUSTOMER_DELETED = "✅ Pelanggan berhasil dihapus!"
        const val PRODUCT_CREATED = "✅ Produk berhasil ditambahkan!"
        const val PRODUCT_UPDATED = "✅ Produk berhasil diperbarui!"
        const val PRODUCT_DELETED = "✅ Produk berhasil dihapus!"
        const val QUEUE_CREATED = "🎉 Antrian berhasil dibuat!"
        const val QUEUE_UPDATED = "✅ Antrian berhasil diperbarui!"
        const val QUEUE_DELETED = "✅ Antrian berhasil dihapus!"
        const val DATA_LOADED = "✅ Data berhasil dimuat!"
    }
}
