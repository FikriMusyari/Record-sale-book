// MainActivity.kt
package com.afi.record.presentation

import CustomerScreen
import ProductScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.afi.record.presentation.screen.AddCustomerScreen
import com.afi.record.presentation.screen.AddProductScreen
import com.afi.record.presentation.screen.AddQueueScreen
import com.afi.record.presentation.screen.DashboardScreen
import com.afi.record.presentation.screen.QueueScreen
import com.afi.record.presentation.screen.SignInScreen
import com.afi.record.presentation.screen.SignUpScreen
import com.afi.record.presentation.ui.theme.RecordTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            RecordTheme {
                NavHost(
                    navController = navController,
                    startDestination = Screen.SignIn.route
                ) {
                    composable(Screen.SignIn.route) {
                        SignInScreen(navController)
                    }
                    composable(Screen.SignUp.route) {
                        SignUpScreen(navController)
                    }
                    composable(Screen.Dashboard.route) {
                        DashboardScreen(navController = navController)
                    }
                    composable(Screen.Customer.route) {
                        CustomerScreen(navController)
                    }
                    composable(Screen.AddCustomer.route) {
                        AddCustomerScreen(navController)
                    }
                    composable(Screen.AddProduct.route) {
                        AddProductScreen(navController) // Changed from ProductScreen to AddProductScreen
                    }
                    composable(Screen.AddQueue.route) {
                        AddQueueScreen(navController)
                    }
                    composable(Screen.Product.route) {
                        ProductScreen(navController)
                    }
                    composable(Screen.Queue.route) {
                        QueueScreen(navController)
                    }
                }
            }
        }
    }
}