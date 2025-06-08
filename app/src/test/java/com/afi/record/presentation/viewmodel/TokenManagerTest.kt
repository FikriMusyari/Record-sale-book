package com.afi.record.presentation.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import android.util.Base64
import androidx.core.content.edit

class TokenManagerTest {

    private val mockContext = mockk<Context>()
    private val mockSharedPreferences = mockk<SharedPreferences>()
    private val mockEditor = mockk<SharedPreferences.Editor>()
    private lateinit var tokenManager: TokenManager

    @Before
    fun setup() {
        mockkStatic(EncryptedSharedPreferences::class)
        mockkStatic(Base64::class)
        
        every { mockSharedPreferences.edit(any()) } returns Unit
        every { 
            EncryptedSharedPreferences.create(
                any(), any(), any(), any(), any()
            ) 
        } returns mockSharedPreferences
        
        tokenManager = TokenManager(mockContext)
    }

    @Test
    fun `saveToken should store token in encrypted preferences`() {
        // Given
        val token = "test_token"
        every { mockSharedPreferences.edit(any()) } returns Unit

        // When
        tokenManager.saveToken(token)

        // Then
        verify { mockSharedPreferences.edit(any()) }
    }

    @Test
    fun `getToken should return stored token`() {
        // Given
        val expectedToken = "test_token"
        every { mockSharedPreferences.getString("jwt_token", null) } returns expectedToken

        // When
        val result = tokenManager.getToken()

        // Then
        assertEquals(expectedToken, result)
        verify { mockSharedPreferences.getString("jwt_token", null) }
    }

    @Test
    fun `getToken should return null when no token stored`() {
        // Given
        every { mockSharedPreferences.getString("jwt_token", null) } returns null

        // When
        val result = tokenManager.getToken()

        // Then
        assertNull(result)
    }

    @Test
    fun `getUserId should return null for invalid token format`() {
        // Given
        val invalidToken = "invalid.token"
        every { mockSharedPreferences.getString("jwt_token", null) } returns invalidToken

        // When
        val result = tokenManager.getUserId()

        // Then
        assertNull(result)
    }

    @Test
    fun `getUserId should return null when token is null`() {
        // Given
        every { mockSharedPreferences.getString("jwt_token", null) } returns null

        // When
        val result = tokenManager.getUserId()

        // Then
        assertNull(result)
    }

    @Test
    fun `getUserId should extract user ID from valid JWT token`() {
        // Given
        val payload = """{"id":123,"email":"test@example.com"}"""
        val encodedPayload = "eyJpZCI6MTIzLCJlbWFpbCI6InRlc3RAZXhhbXBsZS5jb20ifQ" // Base64 encoded payload
        val validToken = "header.$encodedPayload.signature"
        
        every { mockSharedPreferences.getString("jwt_token", null) } returns validToken
        every { 
            Base64.decode(encodedPayload, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP) 
        } returns payload.toByteArray()

        // When
        val result = tokenManager.getUserId()

        // Then
        assertEquals(123, result)
    }

    @Test
    fun `getUserId should return null for malformed JSON in token`() {
        // Given
        val invalidPayload = "invalid_json"
        val encodedPayload = "aW52YWxpZF9qc29u" // Base64 encoded "invalid_json"
        val validToken = "header.$encodedPayload.signature"
        
        every { mockSharedPreferences.getString("jwt_token", null) } returns validToken
        every { 
            Base64.decode(encodedPayload, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP) 
        } returns invalidPayload.toByteArray()

        // When
        val result = tokenManager.getUserId()

        // Then
        assertNull(result)
    }

    @Test
    fun `getUserId should return null when id field is missing from token`() {
        // Given
        val payload = """{"email":"test@example.com","name":"Test User"}"""
        val encodedPayload = "eyJlbWFpbCI6InRlc3RAZXhhbXBsZS5jb20iLCJuYW1lIjoiVGVzdCBVc2VyIn0" 
        val validToken = "header.$encodedPayload.signature"
        
        every { mockSharedPreferences.getString("jwt_token", null) } returns validToken
        every { 
            Base64.decode(encodedPayload, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP) 
        } returns payload.toByteArray()

        // When
        val result = tokenManager.getUserId()

        // Then
        assertNull(result)
    }
}
