package com.afi.record.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.afi.record.domain.repository.AuthRepo
import com.afi.record.presentation.screen.SignInScreen
import com.afi.record.presentation.ui.theme.RecordTheme
import com.afi.record.presentation.viewmodel.AuthViewModel
import com.afi.record.presentation.viewmodel.TokenManager
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockAuthRepo = mockk<AuthRepo>(relaxed = true)
    private val mockTokenManager = mockk<TokenManager>(relaxed = true)

    @Test
    fun signInScreen_displaysAllElements() {
        composeTestRule.setContent {
            RecordTheme {
                val navController = rememberNavController()
                val viewModel = AuthViewModel(mockAuthRepo, mockTokenManager)
                SignInScreen(viewModel, navController)
            }
        }

        // Verify all UI elements are displayed
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Belum punya akun? Daftar sekarang").assertIsDisplayed()
    }

    @Test
    fun signInScreen_loginButtonEnabledByDefault() {
        composeTestRule.setContent {
            RecordTheme {
                val navController = rememberNavController()
                val viewModel = AuthViewModel(mockAuthRepo, mockTokenManager)
                SignInScreen(viewModel, navController)
            }
        }

        // Login button should be enabled by default
        composeTestRule.onNodeWithText("Login").assertIsEnabled()
    }

    @Test
    fun signInScreen_canEnterEmailAndPassword() {
        composeTestRule.setContent {
            RecordTheme {
                val navController = rememberNavController()
                val viewModel = AuthViewModel(mockAuthRepo, mockTokenManager)
                SignInScreen(viewModel, navController)
            }
        }

        // Enter email
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        
        // Enter password
        composeTestRule.onNodeWithText("Password").performTextInput("password123")

        // Verify login button is still enabled
        composeTestRule.onNodeWithText("Login").assertIsEnabled()
    }

    @Test
    fun signInScreen_clickRegisterLink() {
        composeTestRule.setContent {
            RecordTheme {
                val navController = rememberNavController()
                val viewModel = AuthViewModel(mockAuthRepo, mockTokenManager)
                SignInScreen(viewModel, navController)
            }
        }

        // Click on register link
        composeTestRule.onNodeWithText("Belum punya akun? Daftar sekarang").performClick()
        
        // Note: In a real test, we would verify navigation occurred
        // For now, we just verify the click doesn't crash the app
    }

    @Test
    fun signInScreen_clickLoginButton() {
        composeTestRule.setContent {
            RecordTheme {
                val navController = rememberNavController()
                val viewModel = AuthViewModel(mockAuthRepo, mockTokenManager)
                SignInScreen(viewModel, navController)
            }
        }

        // Enter valid credentials
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")

        // Click login button
        composeTestRule.onNodeWithText("Login").performClick()
        
        // Note: In a real test with proper mocking, we would verify the login call
        // For now, we just verify the click doesn't crash the app
    }
}
