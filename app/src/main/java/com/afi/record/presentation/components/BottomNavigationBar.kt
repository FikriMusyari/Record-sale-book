package com.afi.record.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.afi.record.presentation.Screen

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Dashboard : BottomNavItem(
        route = Screen.Dashboard.route,
        icon = Icons.Default.Home,
        label = "Dashboard"
    )

    object Customer : BottomNavItem(
        route = Screen.Customer.route,
        icon = Icons.Default.Person,
        label = "Customers"
    )

    object Queue : BottomNavItem(
        route = Screen.Queue.route,
        icon = Icons.AutoMirrored.Filled.List,
        label = "Queue"
    )

    object Product : BottomNavItem(
        route = Screen.Product.route,
        icon = Icons.Default.ShoppingCart,
        label = "Products"
    )
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Customer,
        BottomNavItem.Queue,
        BottomNavItem.Product
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Only show bottom nav for main screens
    if (currentRoute in setOf(
            Screen.Dashboard.route,
            Screen.Customer.route,
            Screen.Queue.route,
            Screen.Product.route
        )
    ) {
        NavigationBar {
            items.forEach { item ->
                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label) },
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            // Avoid multiple copies
                            launchSingleTop = true
                            // Restore state when reselecting
                            restoreState = true
                            // Pop up to start destination
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    }
                )
            }
        }
    }
}
