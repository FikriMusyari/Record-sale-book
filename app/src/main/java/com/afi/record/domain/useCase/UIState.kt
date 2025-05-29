package com.afi.record.domain.useCase

import com.afi.record.domain.models.Products


sealed class AuthResult {
    data class Success<T>(val data: T): AuthResult()
    data class Error(val message: String): AuthResult()
    object Loading : AuthResult()
}

sealed class ProductResult {
    object Loading : ProductResult()
    data class Success(val data: List<Products>) : ProductResult()
    data class Error(val message: String) : ProductResult()
}
