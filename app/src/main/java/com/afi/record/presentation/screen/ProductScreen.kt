package com.afi.record.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.afi.record.presentation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(navController: NavController) {
    var showCreateNew by remember { mutableStateOf(false) }
    var showFilters by remember { mutableStateOf(false) }
    var showSortBy by remember { mutableStateOf(false) }

    // State for create new options
    var selectedCreateOption by remember { mutableStateOf("Product") }

    // State for sorting
    var selectedSortOption by remember { mutableStateOf("Name") }

    // State for filters
    var priceMinChecked by remember { mutableStateOf(false) }
    var priceMaxChecked by remember { mutableStateOf(false) }
    var stockMinChecked by remember { mutableStateOf(false) }
    var stockMaxChecked by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Products", fontWeight = Bold) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateNew = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Divider()

            // Dialogs
            if (showCreateNew) {
                CreateNewDialog(
                    selectedOption = selectedCreateOption,
                    onOptionSelected = { selectedCreateOption = it },
                    onDismiss = { showCreateNew = false },
                    onCreate = {
                        showCreateNew = false
                        when (selectedCreateOption) {
                            "Queue" -> navController.navigate(Screen.AddQueue.route)
                            "Customer" -> navController.navigate(Screen.AddCustomer.route)
                            "Product" -> navController.navigate(Screen.AddProduct.route)
                        }
                    }
                )
            }
            


            // Main content
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    Text("No products added", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Add a product to manage your inventory",
                        style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}


