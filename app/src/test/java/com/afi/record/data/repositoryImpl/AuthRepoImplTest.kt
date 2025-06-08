package com.afi.record.data.repositoryImpl

import com.afi.record.data.remotes.ApiService
import com.afi.record.domain.models.DataUserResponse
import com.afi.record.domain.models.LoginRequest
import com.afi.record.domain.models.UpdateUserRequest
import com.afi.record.domain.models.UserResponse
import com.afi.record.domain.models.Users
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AuthRepoImplTest {

    private val mockApiService = mockk<ApiService>()
    private lateinit var authRepo: AuthRepoImpl

    @Before
    fun setup() {
        authRepo = AuthRepoImpl(mockApiService)
    }

    @Test
    fun `login should call api service login`() = runTest {
        // Given
        val loginRequest = LoginRequest("test@example.com", "password123")
        val userResponse = UserResponse("Test User", "test@example.com", "mock_token")
        val expectedResponse = DataUserResponse(userResponse)
        
        coEvery { mockApiService.login(loginRequest) } returns expectedResponse

        // When
        val result = authRepo.login(loginRequest)

        // Then
        assertEquals(expectedResponse, result)
        coVerify { mockApiService.login(loginRequest) }
    }

    @Test
    fun `register should call api service register`() = runTest {
        // Given
        val user = Users("Test User", "test@example.com", "password123")
        val expectedResponse = UserResponse("Test User", "test@example.com", "mock_token")
        
        coEvery { mockApiService.register(user) } returns expectedResponse

        // When
        val result = authRepo.register(user)

        // Then
        assertEquals(expectedResponse, result)
        coVerify { mockApiService.register(user) }
    }

    @Test
    fun `getCurrentUser should call api service getUserCurrent`() = runTest {
        // Given
        val userResponse = UserResponse("Test User", "test@example.com", "mock_token")
        val expectedResponse = DataUserResponse(userResponse)
        
        coEvery { mockApiService.getUserCurrent() } returns expectedResponse

        // When
        val result = authRepo.getCurrentUser()

        // Then
        assertEquals(expectedResponse, result)
        coVerify { mockApiService.getUserCurrent() }
    }

    @Test
    fun `updateCurrentUser should call api service updateCurrentUser`() = runTest {
        // Given
        val updateRequest = UpdateUserRequest("New Name", "oldPassword", "newPassword")
        val expectedResponse = UserResponse("New Name", "test@example.com", "mock_token")
        
        coEvery { mockApiService.updateCurrentUser(updateRequest) } returns expectedResponse

        // When
        val result = authRepo.updateCurrentUser(updateRequest)

        // Then
        assertEquals(expectedResponse, result)
        coVerify { mockApiService.updateCurrentUser(updateRequest) }
    }

    @Test
    fun `logout should call api service logout`() = runTest {
        // Given
        coEvery { mockApiService.logout() } returns Unit

        // When
        authRepo.logout()

        // Then
        coVerify { mockApiService.logout() }
    }

    @Test
    fun `login should propagate api exceptions`() = runTest {
        // Given
        val loginRequest = LoginRequest("test@example.com", "password123")
        val expectedException = RuntimeException("Network error")
        
        coEvery { mockApiService.login(loginRequest) } throws expectedException

        // When & Then
        try {
            authRepo.login(loginRequest)
            fail("Expected exception to be thrown")
        } catch (e: Exception) {
            assertEquals(expectedException, e)
        }
        
        coVerify { mockApiService.login(loginRequest) }
    }

    @Test
    fun `register should propagate api exceptions`() = runTest {
        // Given
        val user = Users("Test User", "test@example.com", "password123")
        val expectedException = RuntimeException("Email already exists")
        
        coEvery { mockApiService.register(user) } throws expectedException

        // When & Then
        try {
            authRepo.register(user)
            fail("Expected exception to be thrown")
        } catch (e: Exception) {
            assertEquals(expectedException, e)
        }
        
        coVerify { mockApiService.register(user) }
    }

    @Test
    fun `getCurrentUser should propagate api exceptions`() = runTest {
        // Given
        val expectedException = RuntimeException("Unauthorized")
        
        coEvery { mockApiService.getUserCurrent() } throws expectedException

        // When & Then
        try {
            authRepo.getCurrentUser()
            fail("Expected exception to be thrown")
        } catch (e: Exception) {
            assertEquals(expectedException, e)
        }
        
        coVerify { mockApiService.getUserCurrent() }
    }

    @Test
    fun `updateCurrentUser should propagate api exceptions`() = runTest {
        // Given
        val updateRequest = UpdateUserRequest("New Name", "oldPassword", "newPassword")
        val expectedException = RuntimeException("Invalid password")
        
        coEvery { mockApiService.updateCurrentUser(updateRequest) } throws expectedException

        // When & Then
        try {
            authRepo.updateCurrentUser(updateRequest)
            fail("Expected exception to be thrown")
        } catch (e: Exception) {
            assertEquals(expectedException, e)
        }
        
        coVerify { mockApiService.updateCurrentUser(updateRequest) }
    }

    @Test
    fun `logout should propagate api exceptions`() = runTest {
        // Given
        val expectedException = RuntimeException("Server error")
        
        coEvery { mockApiService.logout() } throws expectedException

        // When & Then
        try {
            authRepo.logout()
            fail("Expected exception to be thrown")
        } catch (e: Exception) {
            assertEquals(expectedException, e)
        }
        
        coVerify { mockApiService.logout() }
    }
}
