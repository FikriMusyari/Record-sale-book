package com.afi.record.domain.useCase


sealed class AuthResult {
    data class Success<T>(val data: T): AuthResult()
    data class Error(val message: String): AuthResult()
    object Loading : AuthResult()
}
