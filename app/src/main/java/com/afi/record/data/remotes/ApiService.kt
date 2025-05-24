package com.afi.record.data.remotes

import com.afi.record.domain.models.AuthResponse
import com.afi.record.domain.models.LoginRequest
import com.afi.record.domain.models.UserResponse
import com.afi.record.domain.models.Users
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/users/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/users")
    suspend fun register(@Body user: Users): UserResponse
}