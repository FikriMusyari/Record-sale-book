package com.afi.record.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afi.record.data.remotes.ApiService
import com.afi.record.domain.models.LoginRequest
import com.afi.record.domain.models.Users
import com.afi.record.domain.useCase.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _authResult = MutableStateFlow<AuthResult?>(null)
    val authResult: StateFlow<AuthResult?> get() = _authResult

    private val _hasNavigated = MutableStateFlow(false)
    val hasNavigated: StateFlow<Boolean> = _hasNavigated

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            try {
                val response = apiService.login(request)
                val token = response.data.token
                    tokenManager.saveToken(token)
                _authResult.value = AuthResult.Success(response)
                _hasNavigated.value = true
            } catch (e: Exception) {
                _authResult.value = AuthResult.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun register(user: Users) {
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            try {
                val response = apiService.register(user)
                _authResult.value = AuthResult.Success(response)
            } catch (e: Exception) {
                _authResult.value = AuthResult.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun clearError() {
        if (_authResult.value is AuthResult.Error) {
            _authResult.value = null
        }
    }

    fun resetNavigation() {
        _hasNavigated.value = false
    }
}


