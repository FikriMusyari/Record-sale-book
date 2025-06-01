package com.afi.record.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afi.record.data.remotes.ApiService
import com.afi.record.domain.models.UpdateUserRequest
import com.afi.record.domain.models.UserResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    private val _userData = MutableStateFlow<UserResponse?>(null)
    val userData: StateFlow<UserResponse?> = _userData

    private val _updateResult = MutableStateFlow<UserResponse?>(null)

    private val _errorMessage = MutableStateFlow<String?>(null)

    fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val user = apiService.getUserCurrent()
                _userData.value = user.data
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun updateUserProfile(request: UpdateUserRequest) {
        viewModelScope.launch {
            try {
                val result = apiService.updateCurrentUser(request)
                _updateResult.value = result
                loadCurrentUser()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }


    fun logout() {
        viewModelScope.launch {
            try {
                apiService.logout()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
}