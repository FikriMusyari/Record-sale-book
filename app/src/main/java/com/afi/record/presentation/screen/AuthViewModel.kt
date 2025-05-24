// AuthViewModel.kt
package com.afi.record.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Data Model
data class User(
    val nama: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val user: User
)

// API Interface
interface AuthApi {
    @POST("/api/users/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("/api/users")
    suspend fun register(@Body user: User): AuthResponse
}

// ViewModel
class AuthViewModel : ViewModel() {
    // State
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var currentUser by mutableStateOf<User?>(null)

    // API Client
    private val authApi = Retrofit.Builder()
        .baseUrl("https://api-record-sale.up.railway.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AuthApi::class.java)

    // Fungsi Login
    fun login(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Username dan password wajib diisi!"
            return
        }

        viewModelScope.launch {
            try {
                isLoading = true
                val response = authApi.login(LoginRequest(email, password))
                currentUser = response.user
                onSuccess()
            } catch (e: HttpException) {
                errorMessage = when (e.code()) {
                    400 -> "Username/password salah"
                    401 -> "Akun tidak ditemukan"
                    else -> "Error: ${e.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Gagal terhubung ke server"
            } finally {
                isLoading = false
            }
        }
    }

    // Fungsi Register
    fun register(
        nama: String,
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        when {
            nama.isBlank() -> errorMessage = "Username wajib diisi!"
            email.isBlank() -> errorMessage = "Email wajib diisi!"
            password.isBlank() -> errorMessage = "Password wajib diisi!"
            password.length < 6 -> errorMessage = "Password minimal 6 karakter!"
            password != password -> errorMessage = "Password tidak sama!"
            else -> {
                viewModelScope.launch {
                    try {
                        isLoading = true
                        val response = authApi.register(User(nama, email, password))
                        currentUser = response.user
                        onSuccess()
                    } catch (e: HttpException) {
                        errorMessage = when (e.code()) {
                            400 -> "Format data tidak valid"
                            409 -> "Username/email sudah terdaftar"
                            else -> "Error: ${e.code()}"
                        }
                    } catch (e: Exception) {
                        errorMessage = "Gagal terhubung ke server"
                    } finally {
                        isLoading = false
                    }
                }
            }
        }
    }
}