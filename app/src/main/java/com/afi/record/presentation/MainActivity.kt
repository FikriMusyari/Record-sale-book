package com.afi.record.presentation


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.afi.record.presentation.component.BottomNavigationBar
import com.afi.record.presentation.screen.AddCustomerScreen
import com.afi.record.presentation.screen.AddProductScreen
import com.afi.record.presentation.screen.ProductScreen
import com.afi.record.presentation.screen.AddQueueScreen
import com.afi.record.presentation.screen.DashboardScreen
import com.afi.record.presentation.screen.QueueScreen
import com.afi.record.presentation.screen.SignInScreen
import com.afi.record.presentation.screen.SignUpScreen
import com.afi.record.presentation.ui.theme.RecordTheme
import dagger.hilt.android.AndroidEntryPoint
import com.afi.record.presentation.viewmodel.AuthViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecordTheme {
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(navController)
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.SignIn.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.SignIn.route) {
                            val viewModel: AuthViewModel = hiltViewModel()
                            SignInScreen(viewModel, navController)
                        }
                        composable(Screen.SignUp.route) {
                            val viewModel: AuthViewModel = hiltViewModel()
                            SignUpScreen(viewModel,navController)
                        }

                        // Main screens (with bottom nav)
                        composable(Screen.Dashboard.route) {
                            DashboardScreen(navController)
                        }
//                        composable(Screen.Customer.route) {
//                            CustomerScreen(navController)
//                        }
                        composable(Screen.Queue.route) {
                            QueueScreen(navController)
                        }
                        composable(Screen.Product.route) {
                            ProductScreen(navController)
                        }

                        // Add screens (no bottom nav)
                        composable(Screen.AddCustomer.route) {
                            AddCustomerScreen(navController)
                        }
                        composable(Screen.AddProduct.route) {
                            AddProductScreen(navController)
                        }
                        composable(Screen.AddQueue.route) {
                            AddQueueScreen(navController)
                        }
                    }
                }
            }
        }
    }
}