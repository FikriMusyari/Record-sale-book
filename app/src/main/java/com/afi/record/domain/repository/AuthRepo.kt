package com.afi.record.domain.repository

import com.afi.record.domain.models.DataUserResponse
import com.afi.record.domain.models.LoginRequest
import com.afi.record.domain.models.UpdateUserRequest
import com.afi.record.domain.models.UserResponse
import com.afi.record.domain.models.Users

interface AuthRepo {
    suspend fun login(request: LoginRequest): DataUserResponse
    suspend fun register(user: Users): UserResponse
    suspend fun getCurrentUser(): DataUserResponse
    suspend fun updateCurrentUser(request: UpdateUserRequest): UserResponse
    suspend fun logout()
}
