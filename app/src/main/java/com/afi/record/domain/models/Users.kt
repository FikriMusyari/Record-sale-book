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
    val nama: String,
    val email: String,
    val token: String
)

