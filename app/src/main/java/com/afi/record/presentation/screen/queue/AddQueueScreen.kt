package com.afi.record.presentation.screen.queue

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.afi.record.domain.models.CreateQueueRequest
import com.afi.record.domain.models.Customers
import com.afi.record.domain.models.OrderItem
import com.afi.record.domain.models.QueueResponse
import com.afi.record.domain.models.QueueStatus
import com.afi.record.domain.models.SelectedProduct
import com.afi.record.domain.useCase.AuthResult
import com.afi.record.presentation.Screen
import com.afi.record.presentation.viewmodel.QueueViewModel
import java.text.NumberFormat
import java.util.Locale


val statusOptions = listOf(
    QueueStatus(1, "In queue", Color(0xFFFFC107)),
    QueueStatus(2, "In process", Color(0xFF2196F3)),
    QueueStatus(3, "Unpaid", Color(0xFFFF5722)),
    QueueStatus(4, "Completed", Color(0xFF4CAF50))
)

// Payment methods with ID
data class PaymentMethod(
    val id: Int,
    val name: String
)

val paymentMethods = listOf(
    PaymentMethod(1, "Cash"),
    PaymentMethod(2, "Credit Card"),
    PaymentMethod(3, "Debit Card"),
    PaymentMethod(4, "Bank Transfer"),
    PaymentMethod(5, "E-Wallet"),
    PaymentMethod(6, "QRIS")
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQueueScreen(
    navController: NavController,
    viewModel: QueueViewModel = hiltViewModel()
) {

    var selectedCustomer by remember { mutableStateOf<Customers?>(null) }
    var selectedProducts by remember { mutableStateOf<List<SelectedProduct>>(emptyList()) }
    var selectedStatus by remember { mutableStateOf(statusOptions[0]) }
    var selectedPaymentMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var note by remember { mutableStateOf("") }
    var grandTotal by remember { mutableStateOf(0.0) }
    var totalDiscount by remember { mutableStateOf(0.0) }


    var showStatusOptions by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showProductOrderDialog by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var snackbarIsError by remember { mutableStateOf(false) }

    val queueResult by viewModel.queue.collectAsStateWithLifecycle()
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))


    LaunchedEffect(queueResult) {
        when (val result = queueResult) {
            is AuthResult.Success<*> -> {
                snackbarMessage = result.message
                snackbarIsError = false
                showSnackbar = true

                // Navigate back on successful queue creation
                if (result.data is QueueResponse) {
                    navController.navigateUp()
                }
            }
            is AuthResult.Error -> {
                snackbarMessage = result.message
                snackbarIsError = true
                showSnackbar = true
            }
            else -> {}
        }
    }

    // Calculate totals when products change
    LaunchedEffect(selectedProducts) {
        grandTotal = selectedProducts.sumOf { it.totalPrice.toDouble() }
        totalDiscount = selectedProducts.sumOf { it.discount.toDouble() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create queue") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showConfirmationDialog = true },
                        enabled = selectedCustomer != null && selectedProducts.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save",
                            tint = if (selectedCustomer != null && selectedProducts.isNotEmpty())
                                Color.Black else Color.Black.copy(alpha = 0.3f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Customer Selection Section
            CustomerSelectionSection(
                selectedCustomer = selectedCustomer,
                onCustomerClick = {
                    navController.navigate(Screen.SelectCustomer.route)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Status Selection Section
            StatusSelectionSection(
                selectedStatus = selectedStatus,
                onStatusClick = { showStatusOptions = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Method Section (only show when status is Completed)
            if (selectedStatus.id == 4) { // Completed status
                PaymentMethodSection(
                    selectedPaymentMethod = selectedPaymentMethod,
                    onPaymentMethodSelected = { selectedPaymentMethod = it }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }


            ProductOrdersSection(
                selectedProducts = selectedProducts,
                onAddProductClick = {
                    showProductOrderDialog = true
                },
                onRemoveProduct = { productToRemove ->
                    selectedProducts = selectedProducts.filter { it.product.id != productToRemove.product.id }
                },
                grandTotal = grandTotal,
                totalDiscount = totalDiscount,
                formatter = formatter
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Note Section
            NoteSection(
                note = note,
                onNoteChange = { note = it }
            )


        }
    }


    if (showStatusOptions) {
        StatusOptionsDialog(
            selectedStatus = selectedStatus,
            onStatusSelected = { status ->
                selectedStatus = status
                showStatusOptions = false
            },
            onDismiss = { showStatusOptions = false }
        )
    }

    // Product Order Dialog
    if (showProductOrderDialog) {
        ProductOrderDialog(
            onProductClick = {
                showProductOrderDialog = false
                navController.navigate(Screen.SelectProduct.route)
            },
            onDismiss = { showProductOrderDialog = false }
        )
    }


    if (showConfirmationDialog) {
        ConfirmationDialog(
            selectedCustomer = selectedCustomer,
            selectedProducts = selectedProducts,
            selectedStatus = selectedStatus,
            note = note,
            grandTotal = grandTotal,
            formatter = formatter,
            onConfirm = {
                // Create queue request
                selectedCustomer?.let { customer ->
                    val orders = selectedProducts.map { selectedProduct ->
                        OrderItem(
                            productId = selectedProduct.product.id,
                            quantity = selectedProduct.quantity,
                            discount = selectedProduct.discount
                        )
                    }

                    val request = CreateQueueRequest(
                        customerId = customer.id,
                        statusId = selectedStatus.id, // Langsung gunakan ID dari selectedStatus
                        paymentId = selectedPaymentMethod?.id, // Gunakan ID dari selectedPaymentMethod
                        note = if (note.isBlank()) null else note, // Allow blank note to be null
                        orders = orders
                    )

                    viewModel.createQueue(request)
                }
                showConfirmationDialog = false
            },
            onDismiss = { showConfirmationDialog = false }
        )
    }


    if (showSnackbar) {
        LaunchedEffect(showSnackbar) {
            kotlinx.coroutines.delay(3000)
            showSnackbar = false
            viewModel.clearQueueError()
        }
    }
}



@Composable
fun CustomerSelectionSection(
    selectedCustomer: Customers?,
    onCustomerClick: () -> Unit
) {
    Column {
        Text(
            text = "Customer",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCustomerClick() }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedCustomer?.nama ?: "Select customer",
                fontSize = 16.sp,
                color = if (selectedCustomer != null) Color.Black else Color.Gray,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Select",
                tint = Color.Gray
            )
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.LightGray.copy(alpha = 0.3f))
        )
    }
}

@Composable
fun StatusSelectionSection(
    selectedStatus: QueueStatus,
    onStatusClick: () -> Unit
) {
    Column {
        Text(
            text = "Status",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onStatusClick() }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedStatus.name,
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Select",
                tint = Color.Gray
            )
        }

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.LightGray.copy(alpha = 0.3f))
        )
    }
}

@Composable
fun PaymentMethodSection(
    selectedPaymentMethod: PaymentMethod?,
    onPaymentMethodSelected: (PaymentMethod) -> Unit
) {
    Column {
        Text(
            text = "Payment method",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.height(120.dp)
        ) {
            items(paymentMethods) { method ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPaymentMethodSelected(method) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedPaymentMethod?.id == method.id,
                        onClick = { onPaymentMethodSelected(method) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = method.name,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Text(
                            text = "ID: ${method.id}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.LightGray.copy(alpha = 0.3f))
        )
    }
}

@Composable
fun ProductOrdersSection(
    selectedProducts: List<SelectedProduct>,
    onAddProductClick: () -> Unit,
    onRemoveProduct: (SelectedProduct) -> Unit,
    grandTotal: Double,
    totalDiscount: Double,
    formatter: NumberFormat
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Product orders",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            TextButton(
                onClick = onAddProductClick,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF007AFF)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grand total price
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Grand total price",
                fontSize = 16.sp,
                color = Color.Black
            )
            Text(
                text = formatter.format(grandTotal),
                fontSize = 16.sp,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Total discount
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total discount",
                fontSize = 16.sp,
                color = Color.Black
            )
            Text(
                text = formatter.format(totalDiscount),
                fontSize = 16.sp,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.LightGray.copy(alpha = 0.3f))
        )
    }
}

@Composable
fun NoteSection(
    note: String,
    onNoteChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Note",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = note,
            onValueChange = onNoteChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = { Text("Add note...") },
            shape = RoundedCornerShape(8.dp),
            maxLines = 5,
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.LightGray,
                unfocusedBorderColor = Color.LightGray
            )
        )
    }
}

@Composable
fun ProductOrderDialog(
    onProductClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.width(320.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Make product orders",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Product field
                Column {
                    Text(
                        text = "Product",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onProductClick() }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Select product",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select",
                            tint = Color.Gray
                        )
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.LightGray.copy(alpha = 0.3f))
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quantity field
                Column {
                    Text(
                        text = "Quantity",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = "",
                        onValueChange = { },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter quantity") },
                        enabled = false
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Discount field
                Column {
                    Text(
                        text = "Discount",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = "",
                        onValueChange = { },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter discount") },
                        enabled = false
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Total price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total price",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$0",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color(0xFF007AFF))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = { /* Handle Add */ },
                        enabled = false
                    ) {
                        Text("Add", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun StatusOptionsDialog(
    selectedStatus: QueueStatus,
    onStatusSelected: (QueueStatus) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.width(320.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Pilih Status",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                statusOptions.forEach { status ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStatusSelected(status) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = status.id == selectedStatus.id,
                            onClick = { onStatusSelected(status) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(
                                    color = status.color,
                                    shape = RoundedCornerShape(3.dp)
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = status.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "ID: ${status.id}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmationDialog(
    selectedCustomer: Customers?,
    selectedProducts: List<SelectedProduct>,
    selectedStatus: QueueStatus,
    note: String,
    grandTotal: Double,
    formatter: NumberFormat,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "🎯 Konfirmasi Antrian",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text("Detail antrian yang akan dibuat:")
                Spacer(modifier = Modifier.height(8.dp))

                Text("👤 Customer: ${selectedCustomer?.nama}")
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📊 Status: ${selectedStatus.name}")
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = selectedStatus.color,
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
                Text("🆔 Status ID: ${selectedStatus.id}")
                Text("🛍️ Produk: ${selectedProducts.size} item(s)")
                Text("💰 Total: ${formatter.format(grandTotal)}")

                if (note.isNotBlank()) {
                    Text("📝 Note: $note")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("✅ Buat Antrian")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("❌ Batal")
            }
        }
    )
}



