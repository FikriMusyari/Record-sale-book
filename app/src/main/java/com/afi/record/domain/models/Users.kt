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

data class UpdateUserRequest(
    val nama: String?,
    val oldPassword: String?,
    val newPassword: String?
)

data class DataUserResponse(
    val data: UserResponse
)

data class UserResponse(
    val nama: String,
    val email: String,
    val token: String
)

