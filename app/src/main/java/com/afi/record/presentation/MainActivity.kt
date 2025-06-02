package com.afi.record.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.afi.record.presentation.components.BottomNavigationBar
import com.afi.record.presentation.screen.DashboardScreen
import com.afi.record.presentation.screen.SignInScreen
import com.afi.record.presentation.screen.SignUpScreen
import com.afi.record.presentation.screen.customers.CustomerScreen
import com.afi.record.presentation.screen.customers.SelectCustomerScreen
import com.afi.record.presentation.screen.products.ProductScreen
import com.afi.record.presentation.screen.products.SelectProductScreen
import com.afi.record.presentation.screen.queue.AddQueueScreen
import com.afi.record.presentation.screen.queue.QueueScreen
import com.afi.record.presentation.ui.theme.RecordTheme
import com.afi.record.presentation.viewmodel.AuthViewModel
import com.afi.record.presentation.viewmodel.CustomerViewModel
import com.afi.record.presentation.viewmodel.DashboardViewModel
import com.afi.record.presentation.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RecordTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        AppNavHost(navController, Modifier.padding(innerPadding))
    }
}

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.SignIn.route,
        modifier = modifier
    ) {
        // Auth screens
        authNavGraph(navController)

        // Main screens with BottomNav
        mainNavGraph(navController)

        // Add screens without BottomNav
        addNavGraph(navController)
    }
}

fun NavGraphBuilder.authNavGraph(navController: NavController) {
    composable(Screen.SignIn.route) {
        val viewModel: AuthViewModel = hiltViewModel()
        SignInScreen(viewModel, navController)
    }
    composable(Screen.SignUp.route) {
        val viewModel: AuthViewModel = hiltViewModel()
        SignUpScreen(viewModel, navController)
    }
}

fun NavGraphBuilder.mainNavGraph(navController: NavController) {
    composable(Screen.Dashboard.route) {
        val viewModel: DashboardViewModel = hiltViewModel()
        DashboardScreen(viewModel, navController)
    }
    composable(Screen.Customer.route) {
        val viewModel: CustomerViewModel = hiltViewModel()
        CustomerScreen(viewModel)
    }
    composable(Screen.Queue.route) {
        QueueScreen(navController)
    }
    composable(Screen.Product.route) {
        val viewModel: ProductViewModel = hiltViewModel()
        ProductScreen(viewModel)
    }
}

fun NavGraphBuilder.addNavGraph(navController: NavHostController) {
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
