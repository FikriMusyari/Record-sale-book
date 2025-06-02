package com.afi.record.presentation.screen.customers

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.afi.record.domain.models.Customers
import com.afi.record.domain.useCase.CustomerResult
import com.afi.record.presentation.viewmodel.CustomerViewModel
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerScreen(
    viewModel: CustomerViewModel
) {
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var showCreateNew by remember { mutableStateOf(false) }
    var createName by remember { mutableStateOf("") }
    var createBalance by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current


    LaunchedEffect(Unit) {
        viewModel.getAllCustomers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Customers",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        TextField(
                            value = searchQuery,
                            onValueChange = { newQuery ->
                                viewModel.searchCustomers(newQuery)
                            },
                            placeholder = { Text("Search customer...") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Filled.Search, contentDescription = "Search Icon")
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.searchCustomers("") }) {
                                        Icon(Icons.Filled.Clear, contentDescription = "Clear Search")
                                    }
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = Color.Gray,
                                cursorColor = MaterialTheme.colorScheme.primary
                            ),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                )
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
            .padding(padding)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
        ){

            when (val state = customers) {
                is CustomerResult.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Loading customers...", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }

                is CustomerResult.Error -> {
                    errorText = "Gagal Mendapatkan data pelanggan"
                }

                is CustomerResult.Success -> {
                    val customers = state.data
                    if (customers.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (searchQuery.isEmpty()) "No customers added" else "No customers found",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = if (searchQuery.isEmpty()) "Add a customer to get started." else "Try different keywords or clear the search.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(customers.size, key = { customers[it].id.toString() }) { index ->
                                val customer = customers[index]
                                CustomerListItem(
                                    customer = customer,
                                    onDelete = { viewModel.deleteCustomer(customer.id) },
                                    onUpdate = { id, newName, newBalance ->
                                        viewModel.updateCustomer(id, newName, newBalance)
                                    }
                                )
                            }
                        }
                    }
                }

                null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Welcome! Add or search for customers.", style = MaterialTheme.typography.bodyLarge)
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
                                if (input.matches(Regex("""^\d*\.?\d*$"""))) {
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
                                }
                            }) {
                                Text("Create")
                            }
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (editMode) {
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Nama") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = editBalance,
                    onValueChange = { input ->
                        if (input.matches(Regex("""^\d*\.?\d*$"""))) {
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
                        Text(
                            "Balance: ${customer.balance}",
                            style = MaterialTheme.typography.bodyMedium
                        )
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
}
