package com.afi.record.presentation.screen.customers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.afi.record.domain.models.Customers
import com.afi.record.presentation.viewmodel.CustomerViewModel
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerScreen(
    viewModel: CustomerViewModel,
    navController: NavController
) {
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    var showCreateNew by remember { mutableStateOf(false) }
    var createName by remember { mutableStateOf("") }
    var createBalance by remember { mutableStateOf("") }

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    // Load data when first composed
    LaunchedEffect(Unit) {
        viewModel.getAllCustomers()
    }

    // Show error dialog if errorMessage updated
    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrEmpty()) {
            errorText = errorMessage ?: ""
            showErrorDialog = true
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Customers", fontWeight = Bold) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateNew = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Customer")
            }
        }
    ) { padding ->

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            if (customers.isEmpty()) {
                // No customers message
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No customers added", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Add a customer to keep track of clients",
                        style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                // List of customers
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(customers, key = { it.id }) { customer ->
                        CustomerListItem(
                            customer = customer,
                            onDelete = { viewModel.deleteCustomer(customer.id) },
                            onUpdate = { id, newName, newBalance ->
                                viewModel.updateCustomer(id, newName, newBalance)
                            }
                        )
                        Divider()
                    }
                }
            }
        }

        // Create New Customer Dialog
        if (showCreateNew) {
            Dialog(onDismissRequest = { showCreateNew = false }) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.width(320.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Create New Customer", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = createName,
                            onValueChange = { createName = it },
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = createBalance,
                            onValueChange = { input ->
                                // hanya angka dan titik
                                if (input.matches(Regex("^\\d*\\.?\\d*\$"))) {
                                    createBalance = input
                                }
                            },
                            label = { Text("Balance") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showCreateNew = false }) {
                                Text("Cancel")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(onClick = {
                                val balanceDecimal = createBalance.toBigDecimalOrNull()
                                if (createName.isNotBlank() && balanceDecimal != null) {
                                    viewModel.createCustomer(createName.trim(), balanceDecimal)
                                    showCreateNew = false
                                    createName = ""
                                    createBalance = ""
                                } else {
                                    errorText = "Please enter valid name and balance"
                                    showErrorDialog = true
                                }
                            }) {
                                Text("Create")
                            }
                        }
                    }
                }
            }
        }

        // Error dialog
        if (showErrorDialog) {
            Dialog(onDismissRequest = { showErrorDialog = false }) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.width(280.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(errorText)
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = { showErrorDialog = false }) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerListItem(
    customer: Customers,
    onDelete: (Number) -> Unit,
    onUpdate: (Number, String?, BigDecimal?) -> Unit
) {
    var editMode by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(customer.nama) }
    var editBalance by remember { mutableStateOf(customer.balance.toPlainString()) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        if (editMode) {
            OutlinedTextField(
                value = editName,
                onValueChange = { editName = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = editBalance,
                onValueChange = { input ->
                    if (input.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        editBalance = input
                    }
                },
                label = { Text("Balance") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = { editMode = false }) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = {
                    val balanceDecimal = editBalance.toBigDecimalOrNull()
                    if (editName.isNotBlank() && balanceDecimal != null) {
                        onUpdate(customer.id, editName.trim(), balanceDecimal)
                        editMode = false
                    }
                }) {
                    Text("Save")
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(customer.nama, style = MaterialTheme.typography.titleMedium)
                    Text("Balance: ${customer.balance}", style = MaterialTheme.typography.bodyMedium)
                }
                Row {
                    TextButton(onClick = { editMode = true }) {
                        Text("Edit")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { onDelete(customer.id) }) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}
