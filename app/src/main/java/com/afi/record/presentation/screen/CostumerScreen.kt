import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerLedgerScreen() {
    var showCreateNew by remember { mutableStateOf(false) }
    var showFilters by remember { mutableStateOf(false) }
    var showSortBy by remember { mutableStateOf(false) }

    // State for create new options
    var selectedCreateOption by remember { mutableStateOf("Customer") }

    // State for sorting
    var selectedSortOption by remember { mutableStateOf("Name") }

    // State for filters
    var balanceMinChecked by remember { mutableStateOf(false) }
    var balanceMaxChecked by remember { mutableStateOf(false) }
    var debtMinChecked by remember { mutableStateOf(false) }
    var debtMaxChecked by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("anjay", fontWeight = Bold) },
                actions = {
                    TextButton(onClick = { showSortBy = true }) {
                        Text("Sort by")
                    }
                    TextButton(onClick = { showFilters = true }) {
                        Text("Filters")
                    }
                }
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
                    onDismiss = { showCreateNew = false }
                )
            }

            if (showFilters) {
                FiltersDialog(
                    balanceMinChecked = balanceMinChecked,
                    balanceMaxChecked = balanceMaxChecked,
                    debtMinChecked = debtMinChecked,
                    debtMaxChecked = debtMaxChecked,
                    onBalanceMinChange = { balanceMinChecked = it },
                    onBalanceMaxChange = { balanceMaxChecked = it },
                    onDebtMinChange = { debtMinChecked = it },
                    onDebtMaxChange = { debtMaxChecked = it },
                    onDismiss = { showFilters = false }
                )
            }

            if (showSortBy) {
                SortByDialog(
                    selectedOption = selectedSortOption,
                    onOptionSelected = { selectedSortOption = it },
                    onDismiss = { showSortBy = false }
                )
            }

            // Main content
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    Text("No customers added", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Add a customer to keep track of clients",
                        style = MaterialTheme.typography.bodyMedium)

                    // Display current selections (for debugging/verification)

                }
            }
        }
    }
}

@Composable
fun CreateNewDialog(
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.width(280.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Create new", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))

                val options = listOf("Queue", "Customer", "Product")
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = option == selectedOption,
                            onClick = { onOptionSelected(option) }
                        )
                        Text(
                            text = option,
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Create")
                    }
                }
            }
        }
    }
}

@Composable
fun FiltersDialog(
    balanceMinChecked: Boolean,
    balanceMaxChecked: Boolean,
    debtMinChecked: Boolean,
    debtMaxChecked: Boolean,
    onBalanceMinChange: (Boolean) -> Unit,
    onBalanceMaxChange: (Boolean) -> Unit,
    onDebtMinChange: (Boolean) -> Unit,
    onDebtMaxChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.width(280.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Filters", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Balance", style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = balanceMinChecked,
                        onCheckedChange = onBalanceMinChange
                    )
                    Text("Min", style = MaterialTheme.typography.bodyLarge)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = balanceMaxChecked,
                        onCheckedChange = onBalanceMaxChange
                    )
                    Text("Max", style = MaterialTheme.typography.bodyLarge)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Debt", style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = debtMinChecked,
                        onCheckedChange = onDebtMinChange
                    )
                    Text("Min", style = MaterialTheme.typography.bodyLarge)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = debtMaxChecked,
                        onCheckedChange = onDebtMaxChange
                    )
                    Text("Max", style = MaterialTheme.typography.bodyLarge)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}

@Composable
fun SortByDialog(
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.width(280.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Sort by", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))

                val sortOptions = listOf("Name", "Balance")
                sortOptions.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOptionSelected(option) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = option == selectedOption,
                            onClick = { onOptionSelected(option) }
                        )
                        Text(
                            text = option,
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomerLedgerScreenPreview() {
    MaterialTheme {
        CustomerLedgerScreen()
    }
}