package com.afi.record.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.afi.record.domain.models.Customers
import com.afi.record.domain.models.Products
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class QueueStateManager @Inject constructor() : ViewModel() {


    private val _selectedCustomer = MutableStateFlow<Customers?>(null)
    val selectedCustomer: StateFlow<Customers?> = _selectedCustomer


    private val _tempSelectedProduct = MutableStateFlow<Products?>(null)
    val tempSelectedProduct: StateFlow<Products?> = _tempSelectedProduct


    fun selectCustomer(customer: Customers) {
        _selectedCustomer.value = customer
    }


    fun setTempSelectedProduct(product: Products) {
        _tempSelectedProduct.value = product
    }

    fun clearTempSelectedProduct() {
        _tempSelectedProduct.value = null
    }

    fun clearAllSelections() {
        _selectedCustomer.value = null
        _tempSelectedProduct.value = null
    }
}