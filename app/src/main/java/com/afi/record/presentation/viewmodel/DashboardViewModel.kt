package com.afi.record.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afi.record.domain.models.UpdateUserRequest
import com.afi.record.domain.models.UserResponse
import com.afi.record.domain.repository.AuthRepo
import com.afi.record.domain.useCase.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val tokenManager: TokenManager
) : ViewModel() {

    // State for user data
    private val _userData = MutableStateFlow<UserResponse?>(null)
    val userData: StateFlow<UserResponse?> = _userData

    // State for operations (getCurrentUser, updateProfile, logout)
    private val _dashboardResult = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val dashboardResult: StateFlow<AuthResult> = _dashboardResult

    // Fun loading messages for dashboard operations
    private val profileMessages = listOf(
        "🔍 Mengambil data profil...",
        "👤 Memuat informasi akun...",
        "📊 Sinkronisasi data...",
        "✨ Hampir selesai..."
    )

    private val updateMessages = listOf(
        "💾 Menyimpan perubahan...",
        "🔄 Memperbarui profil...",
        "⚡ Sinkronisasi data...",
        "🎯 Hampir selesai..."
    )

    private val logoutMessages = listOf(
        "👋 Sedang keluar...",
        "🔒 Mengamankan sesi...",
        "📱 Membersihkan data...",
        "✅ Hampir selesai..."
    )

    fun loadCurrentUser() {
        viewModelScope.launch {
            val randomMessage = profileMessages.random()
            _dashboardResult.value = AuthResult.Loading(randomMessage)

            try {
                val user = authRepo.getCurrentUser()
                _userData.value = user.data
                _dashboardResult.value = AuthResult.Success(
                    data = user,
                    message = "✅ Profil berhasil dimuat!"
                )
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("401") == true -> "🔐 Sesi telah berakhir, silakan login kembali"
                    e.message?.contains("network") == true -> "🌐 Koneksi internet bermasalah"
                    e.message?.contains("timeout") == true -> "⏰ Koneksi timeout, coba lagi"
                    else -> "😵 Gagal memuat profil: ${e.localizedMessage ?: "Unknown error"}"
                }
                _dashboardResult.value = AuthResult.Error(errorMessage)
            }
        }
    }

    fun updateUserProfile(request: UpdateUserRequest) {
        viewModelScope.launch {
            val randomMessage = updateMessages.random()
            _dashboardResult.value = AuthResult.Loading(randomMessage)

            try {
                val result = authRepo.updateCurrentUser(request)
                _userData.value = result
                _dashboardResult.value = AuthResult.Success(
                    data = result,
                    message = "🎉 Profil berhasil diperbarui!"
                )
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("400") == true -> "📝 Data tidak valid, periksa kembali"
                    e.message?.contains("401") == true -> "🔐 Sesi telah berakhir, silakan login kembali"
                    e.message?.contains("network") == true -> "🌐 Koneksi internet bermasalah"
                    else -> "😵 Gagal memperbarui profil: ${e.localizedMessage ?: "Unknown error"}"
                }
                _dashboardResult.value = AuthResult.Error(errorMessage)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val randomMessage = logoutMessages.random()
            _dashboardResult.value = AuthResult.Loading(randomMessage)

            try {
                authRepo.logout()
                tokenManager.saveToken("") // Clear local token
                _userData.value = null // Clear user data
                _dashboardResult.value = AuthResult.Success(
                    data = "logout_success",
                    message = "👋 Sampai jumpa lagi!"
                )
            } catch (e: Exception) {
                tokenManager.saveToken("")
                _userData.value = null
                _dashboardResult.value = AuthResult.Success(
                    data = "logout_success",
                    message = "👋 Anda telah keluar dari aplikasi"
                )
            }
        }
    }

    // Helper functions for dashboard state management
    fun clearDashboardError() {
        if (_dashboardResult.value is AuthResult.Error) {
            _dashboardResult.value = AuthResult.Idle
        }
    }

    fun resetDashboardState() {
        _dashboardResult.value = AuthResult.Idle
    }
}