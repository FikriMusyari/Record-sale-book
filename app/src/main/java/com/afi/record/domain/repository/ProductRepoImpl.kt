package com.afi.record.domain.repository

import com.afi.record.data.remotes.ApiService
import com.afi.record.domain.models.CreateProductRequest
import com.afi.record.domain.models.Products
import com.afi.record.domain.models.UpdateProductRequest
import com.afi.record.domain.useCase.AuthResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class ProductRepoImpl (
    private val api: ApiService
) : ProductRepo {

    override  suspend fun getAllProduct(): List<Products> {
        return api.getAllProducts()
    }

    override suspend fun searchproduct(query: String): List<Products> {
        return api.searchproducts(query)
    }

    override fun createProduct(request: CreateProductRequest): Flow<AuthResult> {
        return flow {
            emit(AuthResult.Loading)

            try {
                val createdProduct = api.createProduct(request)

                emit(AuthResult.Success(createdProduct))

            } catch (e: HttpException) {
                val errorMessage = e.response()?.errorBody()?.string() ?: "Terjadi kesalahan HTTP"
                emit(AuthResult.Error("Error ${e.code()}: $errorMessage"))
            } catch (e: IOException) {
                emit(AuthResult.Error("Tidak dapat terhubung ke server. Periksa koneksi internet Anda."))
            } catch (e: Exception) {
                emit(AuthResult.Error("Gagal membuat produk: ${e.localizedMessage ?: "Terjadi kesalahan tidak diketahui"}"))
            }
        }
    }
    override fun updateProduct(productId: Number, request: UpdateProductRequest): Flow<AuthResult> {
        return flow {
            emit(AuthResult.Loading)
            try {
                val updatedProduct: Products = api.updateProduct(productId, request) // Sesuaikan dengan API Anda
                emit(AuthResult.Success(updatedProduct))
            } catch (e: HttpException) {
                val errorMessage = e.response()?.errorBody()?.string() ?: "Terjadi kesalahan HTTP"
                emit(AuthResult.Error("Error ${e.code()}: $errorMessage"))
            } catch (e: IOException) {
                emit(AuthResult.Error("Gagal memperbarui produk: Periksa koneksi."))
            } catch (e: Exception) {
                emit(AuthResult.Error("Gagal memperbarui produk: ${e.localizedMessage ?: "Kesalahan tidak diketahui"}"))
            }
        }
    }

    override fun deleteProduct(productId: Number): Flow<AuthResult> {
        return flow {
            emit(AuthResult.Loading)
            try {

                val deleteConfirmation: Any = api.deleteProduct(productId)
                emit(AuthResult.Success(deleteConfirmation))
            } catch (e: HttpException) {
                val errorMessage = e.response()?.errorBody()?.string() ?: "Terjadi kesalahan HTTP"
                emit(AuthResult.Error("Error ${e.code()}: $errorMessage"))
            } catch (e: IOException) {
                emit(AuthResult.Error("Gagal menghapus produk: Periksa koneksi."))
            } catch (e: Exception) {
                emit(AuthResult.Error("Gagal menghapus produk: ${e.localizedMessage ?: "Kesalahan tidak diketahui"}"))
            }
        }
    }

}