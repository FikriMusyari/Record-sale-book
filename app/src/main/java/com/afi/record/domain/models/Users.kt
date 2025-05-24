package com.afi.record.domain.models

data class Users(
    val nama: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class UserResponse(
    val id: Int,
    val nama: String,
    val email: String
)

data class AuthResponse(
    val token: String,
    val user: UserResponse
)

