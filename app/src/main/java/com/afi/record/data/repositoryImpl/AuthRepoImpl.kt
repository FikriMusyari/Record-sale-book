package com.afi.record.data.repositoryImpl

import com.afi.record.data.remotes.ApiService
import com.afi.record.domain.models.DataUserResponse
import com.afi.record.domain.models.LoginRequest
import com.afi.record.domain.models.UpdateUserRequest
import com.afi.record.domain.models.UserResponse
import com.afi.record.domain.models.Users
import com.afi.record.domain.repository.AuthRepo
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val api: ApiService
) : AuthRepo {
    
    override suspend fun login(request: LoginRequest): DataUserResponse = 
        api.login(request)
    
    override suspend fun register(user: Users): UserResponse = 
        api.register(user)
    
    override suspend fun getCurrentUser(): DataUserResponse = 
        api.getUserCurrent()
    
    override suspend fun updateCurrentUser(request: UpdateUserRequest): UserResponse = 
        api.updateCurrentUser(request)
    
    override suspend fun logout() = 
        api.logout()
}
