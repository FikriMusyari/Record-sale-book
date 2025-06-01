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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
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
fun BottomNavigationBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val items = remember {
        listOf(
            BottomNavItem.Dashboard,
            BottomNavItem.Customer,
            BottomNavItem.Queue,
            BottomNavItem.Product
        )
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    if (currentRoute in items.map { it.route }) {
        NavigationBar(modifier = modifier) {
            items.forEach { item ->
                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label) },
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState = true
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

