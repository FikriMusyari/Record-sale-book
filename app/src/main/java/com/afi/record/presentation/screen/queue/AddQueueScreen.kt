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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.afi.record.domain.models.CreateQueueRequest
import com.afi.record.domain.models.Customers
import com.afi.record.domain.models.OrderItem
import com.afi.record.domain.models.Products
import com.afi.record.domain.models.QueueResponse
import com.afi.record.domain.models.QueueStatus
import com.afi.record.domain.models.SelectedProduct
import com.afi.record.domain.useCase.AuthResult
import com.afi.record.presentation.Screen
import com.afi.record.presentation.viewmodel.QueueViewModel
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale


val statusOptions = listOf(
    QueueStatus(1, "In queue", Color(0xFFFFC107)),
    QueueStatus(2, "In process", Color(0xFF2196F3)),
    QueueStatus(3, "Unpaid", Color(0xFFFF5722)),
    QueueStatus(4, "Completed", Color(0xFF4CAF50))
)


data class PaymentMethod(
    val id: Int,
    val name: String
)

val paymentMethods = listOf(
    PaymentMethod(1, "Cash"),
    PaymentMethod(2, "Account Balance")
)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQueueScreen(
    navController: NavController,
    viewModel: QueueViewModel = hiltViewModel()
) {

    val selectedCustomer by viewModel.selectedCustomer.collectAsStateWithLifecycle()
    val selectedProducts by viewModel.selectedProducts.collectAsStateWithLifecycle()
    val tempSelectedProduct by viewModel.tempSelectedProduct.collectAsStateWithLifecycle()
    var selectedStatus by remember { mutableStateOf(statusOptions[0]) }
    var selectedPaymentMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var note by remember { mutableStateOf("") }
    var grandTotal by remember { mutableStateOf(BigDecimal.ZERO) }
    var totalDiscount by remember { mutableStateOf(BigDecimal.ZERO) }


    var showStatusOptions by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showProductOrderDialog by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var snackbarIsError by remember { mutableStateOf(false) }


    var dialogSelectedProduct by remember { mutableStateOf<Products?>(null) }
    var tempQuantity by remember { mutableStateOf("1") }
    var tempDiscount by remember { mutableStateOf("0") }

    val queueResult by viewModel.queue.collectAsStateWithLifecycle()
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    LaunchedEffect(queueResult) {
        when (val result = queueResult) {
            is AuthResult.Success<*> -> {
                snackbarMessage = result.message
                snackbarIsError = false
                showSnackbar = true


                if (result.data is QueueResponse) {
                    viewModel.clearAllSelections()
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

    LaunchedEffect(selectedProducts) {
        grandTotal = selectedProducts.fold(BigDecimal.ZERO) { acc, item -> acc.add(item.totalPrice) }
        totalDiscount = selectedProducts.fold(BigDecimal.ZERO) { acc, item -> acc.add(item.discount) }
    }

    LaunchedEffect(tempSelectedProduct) {
        tempSelectedProduct?.let { product ->
            dialogSelectedProduct = product
            showProductOrderDialog = true
            viewModel.clearTempSelectedProduct()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create queue") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearAllSelections()
                        navController.navigateUp()
                    }) {
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

            if (selectedStatus.id == 4) {
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
                    viewModel.removeSelectedProduct(productToRemove.product.id)
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
            selectedProduct = dialogSelectedProduct,
            quantity = tempQuantity,
            discount = tempDiscount,
            onQuantityChange = { tempQuantity = it },
            onDiscountChange = { tempDiscount = it },
            onProductClick = {
                navController.navigate(Screen.SelectProduct.route)
            },
            onAddProduct = {
                dialogSelectedProduct?.let { product ->
                    val quantity = tempQuantity.toIntOrNull() ?: 1
                    val discount = tempDiscount.toBigDecimalOrNull() ?: BigDecimal.ZERO
                    viewModel.addSelectedProduct(product, quantity, discount)

                    // Reset dialog state
                    dialogSelectedProduct = null
                    tempQuantity = "1"
                    tempDiscount = "0"
                    showProductOrderDialog = false
                }
            },
            onDismiss = {
                // Reset dialog state
                dialogSelectedProduct = null
                tempQuantity = "1"
                tempDiscount = "0"
                showProductOrderDialog = false
            }
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
                        statusId = selectedStatus.id,
                        paymentId = selectedPaymentMethod?.id,
                        note = if (note.isBlank()) null else note,
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selectedCustomer != null)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Customer",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Customer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCustomerClick() }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = selectedCustomer?.nama ?: "Pilih customer",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selectedCustomer != null)
                            MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    if (selectedCustomer != null) {
                        Text(
                            text = "Saldo: ${NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(selectedCustomer.balance.toDouble())}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun StatusSelectionSection(
    selectedStatus: QueueStatus,
    onStatusClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Status",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Status Antrian",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onStatusClick() }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = selectedStatus.color,
                                shape = RoundedCornerShape(6.dp)
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selectedStatus.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
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
    grandTotal: BigDecimal,
    totalDiscount: BigDecimal,
    formatter: NumberFormat
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Products",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Produk Pesanan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Button(
                    onClick = onAddProductClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tambah", fontSize = 14.sp)
                }
            }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Products List
        if (selectedProducts.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.heightIn(max = 200.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedProducts.size) { index ->
                    val selectedProduct = selectedProducts[index]
                    SelectedProductItem(
                        selectedProduct = selectedProduct,
                        onRemove = { onRemoveProduct(selectedProduct) },
                        formatter = formatter
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

            // Summary section
            if (selectedProducts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Discount",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = formatter.format(totalDiscount.toDouble()),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Red,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Grand Total",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = formatter.format(grandTotal.toDouble()),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectedProductItem(
    selectedProduct: SelectedProduct,
    onRemove: () -> Unit,
    formatter: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = selectedProduct.product.nama,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Qty: ${selectedProduct.quantity} × ${selectedProduct.product.price}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                if (selectedProduct.discount > BigDecimal.ZERO) {
                    Text(
                        text = "Discount: ${formatter.format(selectedProduct.discount.toDouble())}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatter.format(selectedProduct.totalPrice.toDouble()),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                TextButton(
                    onClick = onRemove,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Remove", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun NoteSection(
    note: String,
    onNoteChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Note",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Catatan (Opsional)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                placeholder = {
                    Text(
                        "Tambahkan catatan untuk pesanan ini...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                shape = RoundedCornerShape(8.dp),
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
fun ProductOrderDialog(
    selectedProduct: Products?,
    quantity: String,
    discount: String,
    onQuantityChange: (String) -> Unit,
    onDiscountChange: (String) -> Unit,
    onProductClick: () -> Unit,
    onAddProduct: () -> Unit,
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedProduct?.nama ?: "Select product",
                                fontSize = 16.sp,
                                color = if (selectedProduct != null) Color.Black else Color.Gray
                            )
                            if (selectedProduct != null) {
                                Text(
                                    text = "Price: ${selectedProduct.price}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
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

                if (selectedProduct != null) {
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
                            value = quantity,
                            onValueChange = { newValue ->
                                // Only allow positive integers
                                if (newValue.isEmpty() || (newValue.toIntOrNull() != null && newValue.toInt() > 0)) {
                                    onQuantityChange(newValue)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter quantity") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            isError = quantity.isNotEmpty() && (quantity.toIntOrNull() == null || quantity.toInt() <= 0),
                            supportingText = {
                                if (quantity.isNotEmpty() && (quantity.toIntOrNull() == null || quantity.toInt() <= 0)) {
                                    Text(
                                        text = "Quantity harus berupa angka positif",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
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
                            value = discount,
                            onValueChange = { newValue ->
                                // Only allow valid decimal numbers (including 0)
                                if (newValue.isEmpty() || newValue.toBigDecimalOrNull() != null) {
                                    val decimalValue = newValue.toBigDecimalOrNull()
                                    if (decimalValue == null || decimalValue >= BigDecimal.ZERO) {
                                        onDiscountChange(newValue)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter discount (optional)") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            isError = discount.isNotEmpty() && (discount.toBigDecimalOrNull() == null || discount.toBigDecimalOrNull()!! < BigDecimal.ZERO),
                            supportingText = {
                                if (discount.isNotEmpty() && (discount.toBigDecimalOrNull() == null || discount.toBigDecimalOrNull()!! < BigDecimal.ZERO)) {
                                    Text(
                                        text = "Discount harus berupa angka positif atau nol",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Total price calculation
                    val price = selectedProduct.price
                    val qty = quantity.toIntOrNull() ?: 1
                    val disc = discount.toBigDecimalOrNull() ?: BigDecimal.ZERO
                    val totalPrice = price.multiply(BigDecimal(qty)).subtract(disc)

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
                            text = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(totalPrice.toDouble()),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                } else {
                    Spacer(modifier = Modifier.height(24.dp))

                    // Info text
                    Text(
                        text = "Select a product to continue with quantity and discount settings.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color(0xFF007AFF))
                    }
                    if (selectedProduct != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = onAddProduct,
                            enabled = quantity.toIntOrNull() != null && quantity.toIntOrNull()!! > 0
                        ) {
                            Text("Add", color = Color(0xFF007AFF))
                        }
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
    grandTotal: BigDecimal,
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
                Text("💰 Total: ${formatter.format(grandTotal.toDouble())}")

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



