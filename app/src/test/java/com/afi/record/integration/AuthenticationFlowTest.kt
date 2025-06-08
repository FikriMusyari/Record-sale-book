package com.afi.record.integration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.afi.record.data.repositoryImpl.AuthRepoImpl
import com.afi.record.data.remotes.ApiService
import com.afi.record.domain.models.DataUserResponse
import com.afi.record.domain.models.LoginRequest
import com.afi.record.domain.models.UserResponse
import com.afi.record.domain.models.Users
import com.afi.record.domain.useCase.AuthResult
import com.afi.record.presentation.viewmodel.AuthViewModel
import com.afi.record.presentation.viewmodel.TokenManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthenticationFlowTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val mockApiService = mockk<ApiService>()
    private val mockTokenManager = mockk<TokenManager>()
    private lateinit var authRepo: AuthRepoImpl
    private lateinit var authViewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepo = AuthRepoImpl(mockApiService)
        authViewModel = AuthViewModel(authRepo, mockTokenManager)
    }

    @Test
    fun `complete login flow should work end to end`() = runTest {
        // Given
        val loginRequest = LoginRequest("test@example.com", "password123")
        val userResponse = UserResponse("Test User", "test@example.com", "mock_token_12345")
        val dataUserResponse = DataUserResponse(userResponse)
        
        coEvery { mockApiService.login(loginRequest) } returns dataUserResponse
        coEvery { mockTokenManager.saveToken("mock_token_12345") } returns Unit

        // When
        authViewModel.login(loginRequest)
        advanceUntilIdle()

        // Then
        val authResult = authViewModel.authResult.first()
        val hasNavigated = authViewModel.hasNavigated.first()
        
        assertTrue("Auth result should be success", authResult is AuthResult.Success<*>)
        assertTrue("Should have navigated after successful login", hasNavigated)
        
        coVerify { mockApiService.login(loginRequest) }
        coVerify { mockTokenManager.saveToken("mock_token_12345") }
    }

    @Test
    fun `complete registration flow should work end to end`() = runTest {
        // Given
        val user = Users("Test User", "test@example.com", "password123")
        val userResponse = UserResponse("Test User", "test@example.com", "mock_token_67890")
        
        coEvery { mockApiService.register(user) } returns userResponse

        // When
        authViewModel.register(user)
        advanceUntilIdle()

        // Then
        val authResult = authViewModel.authResult.first()
        
        assertTrue("Auth result should be success", authResult is AuthResult.Success<*>)
        assertEquals(
            "Success message should contain user name",
            "🎊 Akun berhasil dibuat! Selamat datang, Test User!",
            (authResult as AuthResult.Success<*>).message
        )
        
        coVerify { mockApiService.register(user) }
    }

    @Test
    fun `login with network error should show appropriate error message`() = runTest {
        // Given
        val loginRequest = LoginRequest("test@example.com", "password123")
        val networkException = RuntimeException("network timeout")
        
        coEvery { mockApiService.login(loginRequest) } throws networkException

        // When
        authViewModel.login(loginRequest)
        advanceUntilIdle()

        // Then
        val authResult = authViewModel.authResult.first()
        
        assertTrue("Auth result should be error", authResult is AuthResult.Error)
        assertTrue(
            "Error message should mention network",
            (authResult as AuthResult.Error).message.contains("network")
        )
        
        coVerify { mockApiService.login(loginRequest) }
    }

    @Test
    fun `login with 401 error should show credential error message`() = runTest {
        // Given
        val loginRequest = LoginRequest("test@example.com", "wrongpassword")
        val unauthorizedException = RuntimeException("401 Unauthorized")
        
        coEvery { mockApiService.login(loginRequest) } throws unauthorizedException

        // When
        authViewModel.login(loginRequest)
        advanceUntilIdle()

        // Then
        val authResult = authViewModel.authResult.first()
        
        assertTrue("Auth result should be error", authResult is AuthResult.Error)
        assertEquals(
            "Should show credential error message",
            "❌ Email atau password salah",
            (authResult as AuthResult.Error).message
        )
        
        coVerify { mockApiService.login(loginRequest) }
    }

    @Test
    fun `registration with 409 error should show email exists error`() = runTest {
        // Given
        val user = Users("Test User", "existing@example.com", "password123")
        val conflictException = RuntimeException("409 Conflict")
        
        coEvery { mockApiService.register(user) } throws conflictException

        // When
        authViewModel.register(user)
        advanceUntilIdle()

        // Then
        val authResult = authViewModel.authResult.first()
        
        assertTrue("Auth result should be error", authResult is AuthResult.Error)
        assertEquals(
            "Should show email exists error message",
            "📧 Email sudah terdaftar",
            (authResult as AuthResult.Error).message
        )
        
        coVerify { mockApiService.register(user) }
    }

    @Test
    fun `error state can be cleared and reset`() = runTest {
        // Given - cause an error first
        val loginRequest = LoginRequest("", "password123") // Empty email will cause validation error
        
        // When - login with invalid data
        authViewModel.login(loginRequest)
        advanceUntilIdle()
        
        // Then - should be in error state
        val errorResult = authViewModel.authResult.first()
        assertTrue("Should be in error state", errorResult is AuthResult.Error)
        
        // When - clear the error
        authViewModel.clearError()
        
        // Then - should be back to idle state
        val clearedResult = authViewModel.authResult.first()
        assertTrue("Should be in idle state after clearing error", clearedResult is AuthResult.Idle)
    }

    @Test
    fun `state can be completely reset`() = runTest {
        // Given - successful login
        val loginRequest = LoginRequest("test@example.com", "password123")
        val userResponse = UserResponse("Test User", "test@example.com", "mock_token")
        val dataUserResponse = DataUserResponse(userResponse)
        
        coEvery { mockApiService.login(loginRequest) } returns dataUserResponse
        coEvery { mockTokenManager.saveToken("mock_token") } returns Unit
        
        authViewModel.login(loginRequest)
        advanceUntilIdle()
        
        // When - reset state
        authViewModel.resetState()
        
        // Then - all states should be reset
        val authResult = authViewModel.authResult.first()
        val hasNavigated = authViewModel.hasNavigated.first()
        
        assertTrue("Auth result should be idle", authResult is AuthResult.Idle)
        assertFalse("Has navigated should be false", hasNavigated)
    }
}
