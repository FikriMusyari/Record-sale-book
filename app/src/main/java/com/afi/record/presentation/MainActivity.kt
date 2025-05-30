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
import com.afi.record.presentation.screen.AddQueueScreen
import com.afi.record.presentation.screen.CustomerScreen
import com.afi.record.presentation.screen.DashboardScreen
import com.afi.record.presentation.screen.EditProductScreen
import com.afi.record.presentation.screen.ProductScreen
import com.afi.record.presentation.screen.QueueScreen
import com.afi.record.presentation.screen.SelectCustomerScreen
import com.afi.record.presentation.screen.SelectProductScreen
import com.afi.record.presentation.screen.SignInScreen
import com.afi.record.presentation.screen.SignUpScreen
import com.afi.record.presentation.ui.theme.RecordTheme
import com.afi.record.presentation.viewmodel.AuthViewModel
import com.afi.record.presentation.viewmodel.ProductViewModel
import com.afi.record.presentation.viewmodel.CustomerViewModel
import dagger.hilt.android.AndroidEntryPoint

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

                        composable(Screen.Customer.route) {
                            val viewModel: CustomerViewModel = hiltViewModel()
                            CustomerScreen(
                                navController = navController,
                                viewModel = viewModel
                            )
                        }


                        composable(Screen.Queue.route) {
                            QueueScreen(navController)
                        }
                        composable(Screen.Product.route) {
                            val viewModel: ProductViewModel = hiltViewModel()
                            ProductScreen(viewModel,navController)
                        }
                        composable(Screen.EditProduct.route){
                            val viewModel: ProductViewModel = hiltViewModel()
                            EditProductScreen(viewModel, navController)
                        }

                        // Add screens (no bottom nav)
                        composable(Screen.AddCustomer.route) {
                            AddCustomerScreen(navController)
                        }
                        composable(Screen.AddProduct.route) {
                            val viewModel: ProductViewModel = hiltViewModel()
                            AddProductScreen(viewModel, navController)
                        }
                        composable(Screen.AddQueue.route) {
                            AddQueueScreen(navController)
                        }
                        composable(Screen.SelectProduct.route) {
                            SelectProductScreen(navController)
                        }
                        composable(Screen.SelectCustomer.route) {
                            SelectCustomerScreen(navController)
                        }
                    }
                }
            }
        }
    }
}