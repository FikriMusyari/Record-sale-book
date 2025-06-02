package com.afi.record.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afi.record.domain.models.CreateProductRequest
import com.afi.record.domain.models.UpdateProductRequest
import com.afi.record.domain.repository.ProductRepo
import com.afi.record.domain.useCase.ProductResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject


@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repo: ProductRepo,
    tokenManager: TokenManager
) : ViewModel() {

    private val userId: Int? = tokenManager.getUserId()

    private val _productsState = MutableStateFlow<ProductResult?>(null)
    val productsState: StateFlow<ProductResult?> get() = _productsState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    fun getAllProducts() {
        if (userId == null) {
            _productsState.value = ProductResult.Error("Pengguna tidak diautentikasi. Tidak dapat memuat produk.")
            return
        }
        viewModelScope.launch {
            _productsState.value = ProductResult.Loading
            try {
                val response = repo.getAllProducts()
                val products = response.data
                val filterProducts = products.filter { it.userId.toInt() == userId }

                _productsState.value = ProductResult.Success(filterProducts)
            } catch (e: Exception) {
                _productsState.value = ProductResult.Error(e.localizedMessage ?: "Gagal mendapatkan data produk")
            }
        }
    }

    fun searchproducts(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            _productsState.value = ProductResult.Loading
            try {
                val response = repo.searchproducts(query)
                val products = response.data ?: emptyList()
                _productsState.value = ProductResult.Success(products)
            } catch (e: Exception) {
                _productsState.value = ProductResult.Error("Pencarian gagal: ${e.message}")
            }
        }
    }

    fun createProduct(nama: String, price: BigDecimal) {
        viewModelScope.launch {
            try {
                val request = CreateProductRequest(nama, price)
                repo.createProduct(request)
                getAllProducts()
            } catch (e: Exception) {
                _productsState.value = ProductResult.Error("Gagal menambahkan produk: ${e.message}")
            }
        }
    }

    fun updateProduct(id: Number, nama: String?, price: BigDecimal?) {
        viewModelScope.launch {
            try {
                val request = UpdateProductRequest(nama, price)
                repo.updateProduct(id, request)
                getAllProducts()
            } catch (e: Exception) {
                _productsState.value = ProductResult.Error("Gagal memperbarui produk: ${e.message}")
            }
        }
    }

    fun deleteProduct(id: Number) {
        viewModelScope.launch {
            try {
                repo.deleteProduct(id)
                getAllProducts()
            } catch (e: Exception) {
                _productsState.value = ProductResult.Error("Gagal menghapus produk: ${e.message}")
            }
        }
    }
}