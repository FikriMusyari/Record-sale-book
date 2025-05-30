package com.afi.record.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afi.record.data.remotes.ApiService
import com.afi.record.domain.models.CreateProductRequest
import com.afi.record.domain.models.Products
import com.afi.record.domain.models.UpdateProductRequest
import com.afi.record.domain.useCase.ProductResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProductViewModel @Inject constructor(
    private val apiService: ApiService,
    tokenManager: TokenManager
) : ViewModel() {

    private val userId: Int? = tokenManager.getUserId()

    private val _productsState = MutableStateFlow<ProductResult?>(null)
    val productsState: StateFlow<ProductResult?> get() = _productsState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _productIdToEdit = MutableStateFlow<Number?>(null)
    val productToEdit: StateFlow<Products?> = combine(_productIdToEdit, _productsState) { id, productsState ->
        if (id == null) null
        else {
            (productsState as? ProductResult.Success)?.data?.find { it.id.toLong() == id.toLong() }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun setProductId(id: Number) {
        _productIdToEdit.value = id
    }

    fun clearProductIdToEdit() {
        _productIdToEdit.value = null
    }

    fun searchProducts(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            _productsState.value = ProductResult.Loading
            try {
                val response = apiService.searchproducts(query)
                val products = response.data ?: emptyList()
                _productsState.value = ProductResult.Success(products)
            } catch (e: Exception) {
                _productsState.value = ProductResult.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }


    fun createProduct(request: CreateProductRequest) {
        viewModelScope.launch {
            _productsState.value = ProductResult.Loading
            try {
                val createdProduct = apiService.createProduct(request)
                _productsState.value = ProductResult.Success(listOf(createdProduct))
                getAllProducts()
            } catch (e: Exception) {
                _productsState.value = ProductResult.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun getAllProducts() {
        if (userId == null) {
            _productsState.value = ProductResult.Error("Pengguna tidak diautentikasi. Tidak dapat memuat produk.")
            return
        }
        viewModelScope.launch {
            _productsState.value = ProductResult.Loading
            try {
                val response = apiService.getAllProducts()
                val products = response.data
                val filteredProducts = products.filter { it.userId.toInt() == userId }

                _productsState.value = ProductResult.Success(filteredProducts)
            } catch (e: Exception) {
                _productsState.value = ProductResult.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun updateProduct(productId: Number, request: UpdateProductRequest) {
        viewModelScope.launch {
            _productsState.value = ProductResult.Loading
            try {
                val updatedProduct = apiService.updateProduct(productId, request)
                _productsState.value = ProductResult.Success(listOf(updatedProduct))
                getAllProducts()
            } catch (e: Exception) {
                _productsState.value = ProductResult.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun deleteProduct(productId: Number) {
        viewModelScope.launch {
            _productsState.value = ProductResult.Loading
            try {
                apiService.deleteProduct(productId)
                // Setelah delete, refresh seluruh produk
                getAllProducts()
            } catch (e: Exception) {
                _productsState.value = ProductResult.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    // Clear error state
    fun clearError() {
        if (_productsState.value is ProductResult.Error) {
            _productsState.value = null
        }
    }
}