package com.afi.record.domain.useCase

import com.afi.record.domain.models.Customers
import com.afi.record.domain.models.Products


sealed class AuthResult {
    data class Success<T>(val data: T, val message: String = "🎉 Berhasil!"): AuthResult()
    data class Error(val message: String): AuthResult()
    data class Loading(val message: String = "⏳ Sedang memproses..."): AuthResult()
    object Idle : AuthResult()
}

sealed class ProductResult {
    object Loading : ProductResult()
    data class Success(val data: List<Products>) : ProductResult()
    data class Error(val message: String) : ProductResult()
}

sealed class CustomerResult {
    object Loading : CustomerResult()
    data class Success(val data: List<Customers>) : CustomerResult()
    data class Error(val message: String) : CustomerResult()
}
