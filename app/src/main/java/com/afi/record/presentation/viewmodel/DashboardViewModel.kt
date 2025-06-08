package com.afi.record.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afi.record.domain.models.Customers
import com.afi.record.domain.models.DashboardMetrics
import com.afi.record.domain.models.DataItem
import com.afi.record.domain.models.Products
import com.afi.record.domain.models.UpdateUserRequest
import com.afi.record.domain.models.UserResponse
import com.afi.record.domain.repository.AuthRepo
import com.afi.record.domain.repository.CustomerRepo
import com.afi.record.domain.repository.ProductRepo
import com.afi.record.domain.repository.QueueRepo
import com.afi.record.domain.useCase.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val tokenManager: TokenManager,
    private val queueRepo: QueueRepo,
    private val customerRepo: CustomerRepo,
    private val productRepo: ProductRepo
) : ViewModel() {

    // State for user data
    private val _userData = MutableStateFlow<UserResponse?>(null)
    val userData: StateFlow<UserResponse?> = _userData

    // State for operations (getCurrentUser, updateProfile, logout)
    private val _dashboardResult = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val dashboardResult: StateFlow<AuthResult> = _dashboardResult

    // Dashboard metrics state
    private val _dashboardMetrics = MutableStateFlow(DashboardMetrics())
    val dashboardMetrics: StateFlow<DashboardMetrics> = _dashboardMetrics

    // Data state
    private val _queues = MutableStateFlow<List<DataItem>>(emptyList())
    private val _customers = MutableStateFlow<List<Customers>>(emptyList())
    private val _products = MutableStateFlow<List<Products>>(emptyList())

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

    // Dashboard data loading functions
    fun loadDashboardData() {
        viewModelScope.launch {
            _dashboardResult.value = AuthResult.Loading("📊 Memuat data dashboard...")

            try {
                // Load all data with proper error handling for 404
                val queueResponse = try {
                    queueRepo.getAllQueue()
                } catch (e: Exception) {
                    if (e.message?.contains("404") == true || e.message?.contains("No queue found") == true) {
                        // Return empty response for 404 - this is normal for new users
                        com.afi.record.domain.models.QueueResponse(data = emptyList())
                    } else {
                        throw e
                    }
                }

                val customerResponse = try {
                    customerRepo.getAllCustomers()
                } catch (e: Exception) {
                    if (e.message?.contains("404") == true) {
                        // Return empty response for 404
                        com.afi.record.domain.models.CustomersResponse(data = emptyList())
                    } else {
                        throw e
                    }
                }

                val productResponse = try {
                    productRepo.getAllProducts()
                } catch (e: Exception) {
                    if (e.message?.contains("404") == true) {
                        // Return empty response for 404
                        com.afi.record.domain.models.ProductResponse(data = emptyList())
                    } else {
                        throw e
                    }
                }

                _queues.value = queueResponse.data ?: emptyList()
                _customers.value = customerResponse.data
                _products.value = productResponse.data

                // Calculate metrics
                calculateDashboardMetrics()

                _dashboardResult.value = AuthResult.Success(
                    data = "dashboard_loaded",
                    message = "✅ Dashboard berhasil dimuat!"
                )
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("401") == true -> "🔐 Sesi telah berakhir, silakan login kembali"
                    e.message?.contains("network") == true -> "🌐 Koneksi internet bermasalah"
                    e.message?.contains("timeout") == true -> "⏰ Koneksi timeout, coba lagi"
                    else -> "😵 Gagal memuat dashboard: ${e.localizedMessage ?: "Unknown error"}"
                }
                _dashboardResult.value = AuthResult.Error(errorMessage)

                // Set empty data on error to prevent crashes
                _queues.value = emptyList()
                _customers.value = emptyList()
                _products.value = emptyList()
                calculateDashboardMetrics()
            }
        }
    }

    private fun calculateDashboardMetrics() {
        val queues = _queues.value
        val customers = _customers.value
        val products = _products.value

        // Calculate total queues
        val totalQueues = queues.size

        // Calculate uncompleted queues (In queue, In process, Unpaid)
        val uncompletedQueues = queues.count { queue ->
            queue.status in listOf("In queue", "In process", "Unpaid")
        }

        // Calculate active customers (customers with non-zero balance)
        val activeCustomers = customers.count { customer ->
            customer.balance > BigDecimal.ZERO
        }

        // Calculate products sold (sum of quantities from completed orders)
        val productsSold = queues
            .filter { it.status == "Completed" }
            .flatMap { it.orders ?: emptyList() }
            .sumOf { it.quantity ?: 0 }

        // Calculate revenue (sum of grand totals from completed queues)
        val revenue = queues
            .filter { it.status == "Completed" }
            .sumOf { it.grandTotal ?: 0 }

        _dashboardMetrics.value = DashboardMetrics(
            totalQueues = totalQueues,
            uncompletedQueues = uncompletedQueues,
            activeCustomers = activeCustomers,
            productsSold = productsSold,
            revenue = BigDecimal(revenue)
        )
    }
}
