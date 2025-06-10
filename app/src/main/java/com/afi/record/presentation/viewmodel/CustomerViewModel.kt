package com.afi.record.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afi.record.domain.models.CreateCustomersRequest
import com.afi.record.domain.models.UpdateCustomersRequest
import com.afi.record.domain.repository.CustomerRepo
import com.afi.record.domain.useCase.CustomerResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val repo: CustomerRepo,
    tokenManager: TokenManager
) : ViewModel() {

    private val userId: Int? = tokenManager.getUserId()

    private val _customers = MutableStateFlow<CustomerResult?>(null)
    val customers: StateFlow<CustomerResult?> = _customers

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    fun getAllCustomers() {
        if (userId == null) {
            _customers.value = CustomerResult.Error("Pengguna tidak diautentikasi. Tidak dapat memuat pelanggan.")
            return
        }
        viewModelScope.launch {
            _customers.value = CustomerResult.Loading
            try {
                val response = repo.getAllCustomers()
                val customers = response.data
                val filteredCustomers = customers.filter { it.userId.toInt() == userId }

                _customers.value = CustomerResult.Success(filteredCustomers)
            } catch (e: Exception) {
                _customers.value = CustomerResult.Error(e.localizedMessage ?: "Gagal mendapatkan data pelanggan")
            }
        }
    }

    fun searchCustomers(query: String) {
        _searchQuery.value = query
        if (userId == null) {
            _customers.value = CustomerResult.Error("Pengguna tidak diautentikasi. Tidak dapat mencari pelanggan.")
            return
        }
        viewModelScope.launch {
            _customers.value = CustomerResult.Loading
            try {
                val response = repo.searchcustomers(query)
                val customers = response.data ?: emptyList()
                // Filter berdasarkan userId seperti di getAllCustomers
                val filteredCustomers = customers.filter { it.userId.toInt() == userId }
                _customers.value = CustomerResult.Success(filteredCustomers)
            } catch (e: Exception) {
                _customers.value = CustomerResult.Error("Pencarian gagal: ${e.message}")
            }
        }
    }

    fun createCustomer(nama: String, balance: BigDecimal) {
        viewModelScope.launch {
            try {
                val request = CreateCustomersRequest(nama, balance)
                repo.createCustomer(request)
                getAllCustomers()
            } catch (e: Exception) {
                _customers.value = CustomerResult.Error("Gagal menambahkan pelanggan: ${e.message}")
            }
        }
    }

    fun updateCustomer(id: Number, nama: String?, balance: BigDecimal?) {
        viewModelScope.launch {
            try {
                val request = UpdateCustomersRequest(nama, balance)
                repo.updateCustomer(id, request)
                getAllCustomers()
            } catch (e: Exception) {
                _customers.value = CustomerResult.Error("Gagal memperbarui pelanggan: ${e.message}")
            }
        }
    }

    fun deleteCustomer(id: Number) {
        viewModelScope.launch {
            try {
                repo.deleteCustomer(id)
                getAllCustomers()
            } catch (e: Exception) {
                _customers.value = CustomerResult.Error("Gagal menghapus pelanggan: ${e.message}")
            }
        }
    }
}
