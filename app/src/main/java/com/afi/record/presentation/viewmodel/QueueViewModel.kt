package com.afi.record.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afi.record.domain.models.CreateQueueRequest
import com.afi.record.domain.models.Customers
import com.afi.record.domain.models.DataItem
import com.afi.record.domain.models.Products
import com.afi.record.domain.models.SelectedProduct
import com.afi.record.domain.models.UpdateQueueRequest
import com.afi.record.domain.repository.QueueRepo
import com.afi.record.domain.useCase.AuthResult
import com.afi.record.domain.useCase.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(
    private val repo: QueueRepo,
    private val customerRepo: com.afi.record.domain.repository.CustomerRepo
) : ViewModel() {

    private val _queue = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val queue: StateFlow<AuthResult> = _queue

    private val _queues = MutableStateFlow<List<DataItem>>(emptyList())
    val queues: StateFlow<List<DataItem>> = _queues

    // Selected customer and products for queue creation
    private val _selectedCustomer = MutableStateFlow<Customers?>(null)
    val selectedCustomer: StateFlow<Customers?> = _selectedCustomer

    private val _selectedProducts = MutableStateFlow<List<SelectedProduct>>(emptyList())
    val selectedProducts: StateFlow<List<SelectedProduct>> = _selectedProducts

    // Temporary product selection for dialog
    private val _tempSelectedProduct = MutableStateFlow<Products?>(null)
    val tempSelectedProduct: StateFlow<Products?> = _tempSelectedProduct

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
        val price = product.price
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

    // Functions for temporary product selection
    fun setTempSelectedProduct(product: Products) {
        _tempSelectedProduct.value = product
    }

    fun clearTempSelectedProduct() {
        _tempSelectedProduct.value = null
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
                _queue.value = AuthResult.Error(ErrorHandler.getQueueErrorMessage(e, "create"))
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
                _queue.value = AuthResult.Error(ErrorHandler.getQueueErrorMessage(e, "fetch"))
            }
        }
    }

    fun updateQueue(queueId: Number, request: UpdateQueueRequest) {
        viewModelScope.launch {
            val randomMessage = updateMessages.random()
            _queue.value = AuthResult.Loading(randomMessage)

            try {
                // Get current queue data before update to check for status change
                val currentQueues = _queues.value
                val currentQueue = currentQueues.find { it.id == queueId }

                repo.updateQueue(queueId, request)

                // Check if status is being changed to "Completed" (statusId = 4)
                if (request.statusId == 4 && currentQueue != null && currentQueue.status != "Completed") {
                    // Handle customer balance deduction for completed queue
                    handleQueueCompletion(currentQueue)
                }

                _queue.value = AuthResult.Success(
                    data = "update_success",
                    message = "🎉 Antrian berhasil diperbarui!"
                )
                // Refresh queue list after update
                getAllQueues()
            } catch (e: Exception) {
                _queue.value = AuthResult.Error(ErrorHandler.getQueueErrorMessage(e, "update"))
            }
        }
    }

    private suspend fun handleQueueCompletion(queue: DataItem) {
        try {
            // Calculate total amount from queue
            val totalAmount = queue.grandTotal ?: 0

            if (totalAmount > 0) {
                // Find customer by name (since we don't have direct customer ID in DataItem)
                val customerName = queue.customer
                if (!customerName.isNullOrBlank()) {
                    // Get customer data to find the customer ID and current balance
                    val customersResponse = customerRepo.getAllCustomers()
                    val customer = customersResponse.data.find { it.nama == customerName }

                    if (customer != null) {
                        // Calculate new balance (deduct the total amount)
                        val newBalance = customer.balance - BigDecimal(totalAmount)

                        // Update customer balance
                        val updateRequest = com.afi.record.domain.models.UpdateCustomersRequest(
                            nama = null, // Don't change name
                            balance = newBalance
                        )

                        customerRepo.updateCustomer(customer.id, updateRequest)

                        // Log the balance deduction for debugging
                        println("🔄 Balance deducted for customer ${customer.nama}: ${customer.balance} -> $newBalance (Amount: $totalAmount)")
                    }
                }
            }
        } catch (e: Exception) {
            // Log error but don't fail the queue update
            println("❌ Error updating customer balance: ${e.message}")
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
                _queue.value = AuthResult.Error(ErrorHandler.getQueueErrorMessage(e, "delete"))
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
