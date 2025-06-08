package com.afi.record.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afi.record.domain.models.LoginRequest
import com.afi.record.domain.models.Users
import com.afi.record.domain.repository.AuthRepo
import com.afi.record.domain.useCase.AuthResult
import com.afi.record.domain.useCase.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepo,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _authResult = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authResult: StateFlow<AuthResult> get() = _authResult

    private val _hasNavigated = MutableStateFlow(false)
    val hasNavigated: StateFlow<Boolean> = _hasNavigated

    // Fun loading messages for different operations
    private val loginMessages = listOf(
        "🔐 Sedang masuk ke akun Anda...",
        "✨ Memverifikasi kredensial...",
        "🚀 Hampir selesai...",
        "🎯 Menghubungkan ke server..."
    )

    private val registerMessages = listOf(
        "📝 Membuat akun baru...",
        "🎨 Menyiapkan profil Anda...",
        "🔧 Mengkonfigurasi akun...",
        "🎉 Hampir selesai!"
    )

    fun login(request: LoginRequest) {
        // Input validation
        if (request.email.isBlank()) {
            _authResult.value = AuthResult.Error(ErrorHandler.Validation.EMPTY_EMAIL)
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(request.email).matches()) {
            _authResult.value = AuthResult.Error(ErrorHandler.Validation.INVALID_EMAIL)
            return
        }
        if (request.password.isBlank()) {
            _authResult.value = AuthResult.Error(ErrorHandler.Validation.EMPTY_PASSWORD)
            return
        }
        if (request.password.length < 6) {
            _authResult.value = AuthResult.Error(ErrorHandler.Validation.SHORT_PASSWORD)
            return
        }

        viewModelScope.launch {
            val randomMessage = loginMessages.random()
            _authResult.value = AuthResult.Loading(randomMessage)

            try {
                val response = repo.login(request)
                val token = response.data.token
                tokenManager.saveToken(token)

                _authResult.value = AuthResult.Success(
                    data = response,
                    message = "🎉 Selamat datang kembali, ${response.data.nama}!"
                )
                _hasNavigated.value = true
            } catch (e: Exception) {
                _authResult.value = AuthResult.Error(ErrorHandler.getAuthErrorMessage(e))
            }
        }
    }

    fun register(user: Users) {
        // Input validation
        if (user.nama.isBlank()) {
            _authResult.value = AuthResult.Error("👤 Nama tidak boleh kosong")
            return
        }
        if (user.email.isBlank()) {
            _authResult.value = AuthResult.Error("📧 Email tidak boleh kosong")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(user.email).matches()) {
            _authResult.value = AuthResult.Error("📧 Format email tidak valid")
            return
        }
        if (user.password.isBlank()) {
            _authResult.value = AuthResult.Error("🔒 Password tidak boleh kosong")
            return
        }
        if (user.password.length < 6) {
            _authResult.value = AuthResult.Error("🔒 Password minimal 6 karakter")
            return
        }

        viewModelScope.launch {
            val randomMessage = registerMessages.random()
            _authResult.value = AuthResult.Loading(randomMessage)

            try {
                val response = repo.register(user)
                _authResult.value = AuthResult.Success(
                    data = response,
                    message = "🎊 Akun berhasil dibuat! Selamat datang, ${response.nama}!"
                )
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("409") == true -> "📧 Email sudah terdaftar"
                    e.message?.contains("400") == true -> "📝 Data tidak valid, periksa kembali"
                    e.message?.contains("network") == true -> "🌐 Koneksi internet bermasalah"
                    else -> "😵 Gagal membuat akun: ${e.localizedMessage ?: "Unknown error"}"
                }
                _authResult.value = AuthResult.Error(errorMessage)
            }
        }
    }

    // Helper functions for authentication flow

    fun clearError() {
        if (_authResult.value is AuthResult.Error) {
            _authResult.value = AuthResult.Idle
        }
    }

    fun resetNavigation() {
        _hasNavigated.value = false
    }

    fun resetState() {
        _authResult.value = AuthResult.Idle
        _hasNavigated.value = false
    }
}


