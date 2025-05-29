package com.afi.record.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afi.record.domain.models.CreateProductRequest
import com.afi.record.domain.models.Products
import com.afi.record.domain.models.UpdateProductRequest
import com.afi.record.domain.repository.ProductRepo
import com.afi.record.domain.useCase.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject


@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepo,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val userId: Int? = tokenManager.getUserId()

    private val _productsState = MutableStateFlow<AuthResult>(AuthResult.Loading)
    val productsState: StateFlow<AuthResult> = _productsState

    private val _selectedProductState = MutableStateFlow<AuthResult?>(null)
    val selectedProductState: StateFlow<AuthResult?> = _selectedProductState

    private val _createProductState = MutableStateFlow<AuthResult?>(null)
    val createProductState: StateFlow<AuthResult?> = _createProductState

    private val _updateProductState = MutableStateFlow<AuthResult?>(null)
    val updateProductState: StateFlow<AuthResult?> = _updateProductState

    private val _deleteProductState = MutableStateFlow<AuthResult?>(null)
    val deleteProductState: StateFlow<AuthResult?> = _deleteProductState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        _searchQuery
            .debounce(300L)
            .distinctUntilChanged()
            .onEach { query ->
                fetchProducts(query.ifBlank { null })
            }
            .launchIn(viewModelScope)
    }

    private fun currentQuery(): String? = _searchQuery.value.ifBlank { null }

        fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun fetchProducts(query: String? = currentQuery()) {
        if (userId == null) {
            _productsState.value = AuthResult.Error("Pengguna tidak diautentikasi. Tidak dapat memuat produk.")
            return
        }
        viewModelScope.launch {
            _productsState.value = AuthResult.Loading
            try {
                val products: List<Products> = if (query.isNullOrBlank()) {
                    productRepository.getAllProduct()
                } else {
                    productRepository.searchproduct(query)
                }

                val filteredProducts = products.filter { it.userId.toInt() == userId }

                _productsState.value = AuthResult.Success(filteredProducts)
            } catch (e: Exception) {
                val errorMessage = if (query.isNullOrBlank()) "Gagal mengambil semua produk" else "Gagal mencari produk"
                _productsState.value = AuthResult.Error("$errorMessage: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }

    fun createProduct(nama: String, price: Number) {
        val request = CreateProductRequest(nama = nama, price = price)
        viewModelScope.launch {
            productRepository.createProduct(request)
                .onStart { _createProductState.value = AuthResult.Loading }
                .catch { e ->
                    _createProductState.value = AuthResult.Error("Gagal membuat produk: ${e.localizedMessage ?: "Unknown error"}")
                }
                .collect { result: AuthResult ->
                    _createProductState.value = result
                    if (result is AuthResult.Success<*>) {
                        fetchProducts(_searchQuery.value.ifBlank { null })
                    }
                }
        }
    }

    fun updateProduct(productId: Number, nama: String?, price: BigDecimal?) {
        val request = UpdateProductRequest(nama = nama, price = price)
        viewModelScope.launch {
            productRepository.updateProduct(productId, request)
                .onStart { _updateProductState.value = AuthResult.Loading }
                .catch { e ->

                    _updateProductState.value = AuthResult.Error("Gagal memperbarui produk (Flow error): ${e.localizedMessage ?: "Unknown error"}")
                }
                .collect { result: AuthResult ->
                    _updateProductState.value = result

                    if (result is AuthResult.Success<*>) {

                        fetchProducts(currentQuery())

                        val currentSelectedState: AuthResult? = _selectedProductState.value

                        if (currentSelectedState is AuthResult.Success<*>) {

                            val selectedProduct = currentSelectedState.data as? Products

                            val idFromSelectedProduct = selectedProduct?.id?.toString()
                            val idProductToUpdate = productId.toString()

                            if (idFromSelectedProduct == idProductToUpdate) {

                                val updatedProductDataFromFlow = result.data

                                if (updatedProductDataFromFlow is Products) {

                                    _selectedProductState.value = AuthResult.Success(updatedProductDataFromFlow)
                                } else {

                                    Log.e("ProductViewModel", "Error: Data sukses dari updateProduct bukan Products. Tipe aktual: ${updatedProductDataFromFlow?.javaClass?.name}")
                                    _selectedProductState.value = AuthResult.Error("Gagal memperbarui detail produk terpilih (tipe data salah).")
                                }
                            }
                        }
                    }
                }
        }
    }

    fun deleteProduct(productId: Number) {
        viewModelScope.launch {
            productRepository.deleteProduct(productId)
                .onStart { _deleteProductState.value = AuthResult.Loading }
                .catch { e ->
                    _deleteProductState.value = AuthResult.Error("Gagal menghapus produk: ${e.localizedMessage ?: "Unknown error"}")
                }
                .collect { result: AuthResult ->
                    _deleteProductState.value = result
                    if (result is AuthResult.Success<*>) {
                        fetchProducts(_searchQuery.value.ifBlank { null })
                        val currentSelected = _selectedProductState.value
                        if (currentSelected is AuthResult.Success<*>) {
                            val selectedData = currentSelected.data as? Products

                            if (selectedData?.id == productId) {
                                _selectedProductState.value = AuthResult.Success(null)
                            }
                        }
                    }
                }
        }
    }

    fun resetCreateProductState() {
        _createProductState.value = null
    }

    fun resetUpdateProductState() {
        _updateProductState.value = null
    }

    fun resetDeleteProductState() {
        _deleteProductState.value = null
    }

    fun resetSelectedProductState() {
        _selectedProductState.value = null
    }
}