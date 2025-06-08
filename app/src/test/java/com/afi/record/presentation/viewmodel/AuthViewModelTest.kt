package com.afi.record.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.afi.record.domain.models.DataUserResponse
import com.afi.record.domain.models.LoginRequest
import com.afi.record.domain.models.UserResponse
import com.afi.record.domain.models.Users
import com.afi.record.domain.repository.AuthRepo
import com.afi.record.domain.useCase.AuthResult
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
class AuthViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val mockAuthRepo = mockk<AuthRepo>()
    private val mockTokenManager = mockk<TokenManager>()
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(mockAuthRepo, mockTokenManager)
    }

    @Test
    fun `login with valid credentials should succeed`() = runTest {
        // Given
        val loginRequest = LoginRequest("test@example.com", "password123")
        val userResponse = UserResponse("Test User", "test@example.com", "mock_token")
        val dataUserResponse = DataUserResponse(userResponse)
        
        coEvery { mockAuthRepo.login(loginRequest) } returns dataUserResponse
        coEvery { mockTokenManager.saveToken("mock_token") } returns Unit

        // When
        viewModel.login(loginRequest)
        advanceUntilIdle()

        // Then
        val result = viewModel.authResult.first()
        assertTrue(result is AuthResult.Success<*>)
        coVerify { mockAuthRepo.login(loginRequest) }
        coVerify { mockTokenManager.saveToken("mock_token") }
    }

    @Test
    fun `login with empty email should show validation error`() = runTest {
        // Given
        val loginRequest = LoginRequest("", "password123")

        // When
        viewModel.login(loginRequest)
        advanceUntilIdle()

        // Then
        val result = viewModel.authResult.first()
        assertTrue(result is AuthResult.Error)
        assertEquals("📧 Email tidak boleh kosong", (result as AuthResult.Error).message)
    }

    @Test
    fun `login with invalid email format should show validation error`() = runTest {
        // Given
        val loginRequest = LoginRequest("invalid-email", "password123")

        // When
        viewModel.login(loginRequest)
        advanceUntilIdle()

        // Then
        val result = viewModel.authResult.first()
        assertTrue(result is AuthResult.Error)
        assertEquals("📧 Format email tidak valid", (result as AuthResult.Error).message)
    }

    @Test
    fun `login with short password should show validation error`() = runTest {
        // Given
        val loginRequest = LoginRequest("test@example.com", "123")

        // When
        viewModel.login(loginRequest)
        advanceUntilIdle()

        // Then
        val result = viewModel.authResult.first()
        assertTrue(result is AuthResult.Error)
        assertEquals("🔒 Password minimal 6 karakter", (result as AuthResult.Error).message)
    }

    @Test
    fun `register with valid data should succeed`() = runTest {
        // Given
        val user = Users("Test User", "test@example.com", "password123")
        val userResponse = UserResponse("Test User", "test@example.com", "mock_token")
        
        coEvery { mockAuthRepo.register(user) } returns userResponse

        // When
        viewModel.register(user)
        advanceUntilIdle()

        // Then
        val result = viewModel.authResult.first()
        assertTrue(result is AuthResult.Success<*>)
        coVerify { mockAuthRepo.register(user) }
    }

    @Test
    fun `register with empty name should show validation error`() = runTest {
        // Given
        val user = Users("", "test@example.com", "password123")

        // When
        viewModel.register(user)
        advanceUntilIdle()

        // Then
        val result = viewModel.authResult.first()
        assertTrue(result is AuthResult.Error)
        assertEquals("👤 Nama tidak boleh kosong", (result as AuthResult.Error).message)
    }

    @Test
    fun `clearError should reset error state to idle`() = runTest {
        // Given
        viewModel.login(LoginRequest("", "password123")) // This will cause an error
        advanceUntilIdle()
        
        // When
        viewModel.clearError()
        
        // Then
        val result = viewModel.authResult.first()
        assertTrue(result is AuthResult.Idle)
    }

    @Test
    fun `resetState should reset all states`() = runTest {
        // Given
        viewModel.login(LoginRequest("test@example.com", "password123"))
        advanceUntilIdle()
        
        // When
        viewModel.resetState()
        
        // Then
        val authResult = viewModel.authResult.first()
        val hasNavigated = viewModel.hasNavigated.first()
        assertTrue(authResult is AuthResult.Idle)
        assertFalse(hasNavigated)
    }
}
