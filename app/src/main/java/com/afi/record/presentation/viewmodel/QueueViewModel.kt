package com.afi.record.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afi.record.domain.models.CreateQueueRequest
import com.afi.record.domain.models.Customers
import com.afi.record.domain.models.Products
import com.afi.record.domain.models.QueueResponse
import com.afi.record.domain.models.SelectedProduct
import com.afi.record.domain.models.UpdateQueueRequest
import com.afi.record.domain.repository.QueueRepo
import com.afi.record.domain.useCase.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(
    private val repo: QueueRepo
) : ViewModel() {

    private val _queue = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val queue: StateFlow<AuthResult> = _queue

    private val _queues = MutableStateFlow<List<com.afi.record.domain.models.DataItem>>(emptyList())
    val queues: StateFlow<List<com.afi.record.domain.models.DataItem>> = _queues

    // Selected customer and products for queue creation
    private val _selectedCustomer = MutableStateFlow<Customers?>(null)
    val selectedCustomer: StateFlow<Customers?> = _selectedCustomer

    private val _selectedProducts = MutableStateFlow<List<SelectedProduct>>(emptyList())
    val selectedProducts: StateFlow<List<SelectedProduct>> = _selectedProducts

    // Fun loading messages for queue operations
    private val createMessages = listOf(
        "📝 Membuat antrian baru...",
        "🎯 Menyiapkan pesanan...",
        "✨ Mengatur detail antrian...",
        "🚀 Hampir selesai..."
    )

    private val loadMessages = listOf(
        "📋 Memuat daftar antrian...",
        "🔄 Sinkronisasi data...",
        "📊 Mengambil informasi terbaru...",
        "⏳ Hampir selesai..."
    )

    private val updateMessages = listOf(
        "🔄 Memperbarui antrian...",
        "💾 Menyimpan perubahan...",
        "✏️ Mengupdate detail...",
        "🎯 Hampir selesai..."
    )

    private val deleteMessages = listOf(
        "🗑️ Menghapus antrian...",
        "🔄 Memproses penghapusan...",
        "✨ Membersihkan data...",
        "⏳ Hampir selesai..."
    )

    // Functions to manage selected customer and products
    fun selectCustomer(customer: Customers) {
        _selectedCustomer.value = customer
    }

    fun clearSelectedCustomer() {
        _selectedCustomer.value = null
    }

    fun addSelectedProduct(product: Products, quantity: Int = 1, discount: BigDecimal = BigDecimal.ZERO) {
        val price = product.price.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val totalPrice = price.multiply(BigDecimal(quantity)).subtract(discount)

        val selectedProduct = SelectedProduct(
            product = product,
            quantity = quantity,
            discount = discount,
            totalPrice = totalPrice
        )

        val currentProducts = _selectedProducts.value.toMutableList()
        // Check if product already exists, if so update it
        val existingIndex = currentProducts.indexOfFirst { it.product.id == product.id }
        if (existingIndex != -1) {
            currentProducts[existingIndex] = selectedProduct
        } else {
            currentProducts.add(selectedProduct)
        }
        _selectedProducts.value = currentProducts
    }

    fun removeSelectedProduct(productId: Int) {
        _selectedProducts.value = _selectedProducts.value.filter { it.product.id != productId }
    }

    fun clearSelectedProducts() {
        _selectedProducts.value = emptyList()
    }

    fun clearAllSelections() {
        _selectedCustomer.value = null
        _selectedProducts.value = emptyList()
    }

    fun createQueue(request: CreateQueueRequest) {
        viewModelScope.launch {
            val randomMessage = createMessages.random()
            _queue.value = AuthResult.Loading(randomMessage)

            try {
                val response = repo.createQueue(request)
                _queue.value = AuthResult.Success(
                    data = response,
                    message = "🎉 Antrian berhasil dibuat!"
                )

                getAllQueues()
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("400") == true -> "📝 Data tidak valid, periksa kembali"
                    e.message?.contains("401") == true -> "🔐 Sesi telah berakhir, silakan login kembali"
                    e.message?.contains("network") == true -> "🌐 Koneksi internet bermasalah"
                    else -> "😵 Gagal membuat antrian: ${e.localizedMessage ?: "Unknown error"}"
                }
                _queue.value = AuthResult.Error(errorMessage)
            }
        }
    }

    fun getAllQueues() {
        viewModelScope.launch {
            val randomMessage = loadMessages.random()
            _queue.value = AuthResult.Loading(randomMessage)

            try {
                val response = repo.getAllQueue()
                _queues.value = response.data ?: emptyList()
                _queue.value = AuthResult.Success(
                    data = response,
                    message = "✅ Daftar antrian berhasil dimuat!"
                )
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("401") == true -> "🔐 Sesi telah berakhir, silakan login kembali"
                    e.message?.contains("network") == true -> "🌐 Koneksi internet bermasalah"
                    e.message?.contains("timeout") == true -> "⏰ Koneksi timeout, coba lagi"
                    else -> "😵 Gagal memuat antrian: ${e.localizedMessage ?: "Unknown error"}"
                }
                _queue.value = AuthResult.Error(errorMessage)
            }
        }
    }

    fun updateQueue(queueId: Number, request: UpdateQueueRequest) {
        viewModelScope.launch {
            val randomMessage = updateMessages.random()
            _queue.value = AuthResult.Loading(randomMessage)

            try {
                repo.updateQueue(queueId, request)
                _queue.value = AuthResult.Success(
                    data = "update_success",
                    message = "🎉 Antrian berhasil diperbarui!"
                )
                // Refresh queue list after update
                getAllQueues()
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("400") == true -> "📝 Data tidak valid, periksa kembali"
                    e.message?.contains("401") == true -> "🔐 Sesi telah berakhir, silakan login kembali"
                    e.message?.contains("404") == true -> "❓ Antrian tidak ditemukan"
                    e.message?.contains("network") == true -> "🌐 Koneksi internet bermasalah"
                    else -> "😵 Gagal memperbarui antrian: ${e.localizedMessage ?: "Unknown error"}"
                }
                _queue.value = AuthResult.Error(errorMessage)
            }
        }
    }

    fun deleteQueue(queueId: Number) {
        viewModelScope.launch {
            val randomMessage = deleteMessages.random()
            _queue.value = AuthResult.Loading(randomMessage)

            try {
                repo.deleteQueue(queueId)
                _queue.value = AuthResult.Success(
                    data = "delete_success",
                    message = "🗑️ Antrian berhasil dihapus!"
                )
                // Refresh queue list after deletion
                getAllQueues()
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("401") == true -> "🔐 Sesi telah berakhir, silakan login kembali"
                    e.message?.contains("404") == true -> "❓ Antrian tidak ditemukan"
                    e.message?.contains("network") == true -> "🌐 Koneksi internet bermasalah"
                    else -> "😵 Gagal menghapus antrian: ${e.localizedMessage ?: "Unknown error"}"
                }
                _queue.value = AuthResult.Error(errorMessage)
            }
        }
    }

    fun clearQueueError() {
        if (_queue.value is AuthResult.Error) {
            _queue.value = AuthResult.Idle
        }
    }

    fun resetQueueState() {
        _queue.value = AuthResult.Idle
    }
}
