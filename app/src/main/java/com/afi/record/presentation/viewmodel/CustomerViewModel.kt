package com.afi.record.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afi.record.domain.models.CreateCustomersRequest
import com.afi.record.domain.models.Customers
import com.afi.record.domain.models.UpdateCustomersRequest
import com.afi.record.domain.repository.CustomerRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val repo: CustomerRepo
) : ViewModel() {

    private val _customers = MutableStateFlow<List<Customers>>(emptyList())
    val customers: StateFlow<List<Customers>> = _customers

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun getAllCustomers() {
        viewModelScope.launch {
            try {
                _customers.value = repo.getAllCustomers()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Gagal mengambil data pelanggan: ${e.message}"
            }
        }
    }

    fun searchCustomers(query: String) {
        viewModelScope.launch {
            try {
                _customers.value = repo.searchcustomers(query)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Pencarian gagal: ${e.message}"
            }
        }
    }

    fun createCustomer(nama: String, balance: BigDecimal) {
        viewModelScope.launch {
            try {
                val request = CreateCustomersRequest(nama, balance)
                repo.createCustomer(request)
                _errorMessage.value = null
                getAllCustomers()
            } catch (e: Exception) {
                _errorMessage.value = "Gagal menambahkan pelanggan: ${e.message}"
            }
        }
    }

    fun updateCustomer(id: String, nama: String?, balance: BigDecimal?) {
        viewModelScope.launch {
            try {
                val request = UpdateCustomersRequest(nama, balance)
                repo.updateCustomer(id, request)
                _errorMessage.value = null
                getAllCustomers()
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memperbarui pelanggan: ${e.message}"
            }
        }
    }

    fun deleteCustomer(id: String) {
        viewModelScope.launch {
            try {
                repo.deleteCustomer(id)
                _errorMessage.value = null
                getAllCustomers()
            } catch (e: Exception) {
                _errorMessage.value = "Gagal menghapus pelanggan: ${e.message}"
            }
        }
    }

    // Optional: untuk reset pesan error dari UI setelah ditampilkan
    fun clearError() {
        _errorMessage.value = null
    }
}
