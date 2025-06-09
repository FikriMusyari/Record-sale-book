package com.afi.record.presentation.screen.queue

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.afi.record.domain.models.DataItem
import com.afi.record.domain.models.UpdateQueueRequest
import com.afi.record.domain.useCase.AuthResult
import com.afi.record.presentation.Screen
import com.afi.record.presentation.viewmodel.QueueViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueScreen(
    navController: NavController,
    viewModel: QueueViewModel = hiltViewModel()
) {
    var showFilters by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // Collect state from ViewModel
    val queueResult by viewModel.queue.collectAsStateWithLifecycle()
    val queues by viewModel.queues.collectAsStateWithLifecycle()

    var selectedFilter by remember { mutableStateOf<String?>(null) }
    val filterOptions = listOf("In queue", "In process", "Unpaid", "Completed")

    // Edit dialog state
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedQueueForEdit by remember { mutableStateOf<DataItem?>(null) }

    // Load queues when screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.getAllQueues()
    }



    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "📋 Queue Management",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(
                        onClick = { showFilters = true },
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(4.dp)
                    ) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddQueue.route) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Queue")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            HorizontalDivider()

            // Selected filter chip
            selectedFilter?.let { filter ->
                FilterChip(
                    text = filter,
                    onClose = { selectedFilter = null }
                )
            }


            // Queue Content based on state
            when (val result = queueResult) {
                is AuthResult.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(result.message, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
                is AuthResult.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = result.message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.getAllQueues() }) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                }
                else -> {
                    // Show queue list or empty state
                    if (queues.isEmpty()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            item {
                                Text("Belum ada antrian", style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Tambahkan antrian untuk melacak pesanan klien",
                                    style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    } else {
                        // Filter queues based on selected filter
                        val filteredQueues = queues.filter { queue ->
                            if (selectedFilter == null) true else {
                                queue.status == selectedFilter
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredQueues.size) { index ->
                                val queue = filteredQueues[index]
                                QueueItem(
                                    queue = queue,
                                    onEditClick = {
                                        // Implement edit queue functionality
                                        queue.id?.let { queueId ->
                                            // For now, let's implement status update
                                            showEditDialog = true
                                            selectedQueueForEdit = queue
                                        }
                                    },
                                    onDeleteClick = {
                                        queue.id?.let { viewModel.deleteQueue(it) }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    if (showFilters) {
        ModalBottomSheet(
            onDismissRequest = { showFilters = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    Icon(
                        Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Filter by Status",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                // All option
                FilterOptionItem(
                    text = "All Queues",
                    isSelected = selectedFilter == null,
                    statusColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        selectedFilter = null
                        showFilters = false
                    }
                )

                filterOptions.forEach { option ->
                    FilterOptionItem(
                        text = option,
                        isSelected = selectedFilter == option,
                        statusColor = when (option) {
                            "In queue" -> Color(0xFFFFC107)
                            "In process" -> Color(0xFF2196F3)
                            "Unpaid" -> Color(0xFFFF5722)
                            "Completed" -> Color(0xFF4CAF50)
                            else -> Color.Gray
                        },
                        onClick = {
                            selectedFilter = if (selectedFilter == option) null else option
                            showFilters = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Edit Queue Dialog
    if (showEditDialog && selectedQueueForEdit != null) {
        EditQueueDialog(
            queue = selectedQueueForEdit!!,
            onDismiss = {
                showEditDialog = false
                selectedQueueForEdit = null
            },
            onUpdateQueue = { queueId, newStatusId, paymentId ->
                val updateRequest = UpdateQueueRequest(
                    statusId = newStatusId,
                    paymentId = paymentId
                )
                viewModel.updateQueue(queueId, updateRequest)
                showEditDialog = false
                selectedQueueForEdit = null
            }
        )
    }
}

@Composable
fun QueueItem(
    queue: DataItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with customer name and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = queue.customer ?: "Unknown Customer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = when (queue.status) {
                        "In queue" -> Color(0xFFFFC107)
                        "In process" -> Color(0xFF2196F3)
                        "Unpaid" -> Color(0xFFFF5722)
                        "Completed" -> Color(0xFF4CAF50)
                        else -> Color.Gray
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = queue.status ?: "Unknown",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Grand total
            Text(
                text = "Total: Rp ${queue.grandTotal ?: 0}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            // Note if available
            queue.note?.let { note ->
                if (note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Catatan: $note",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            // Orders summary
            queue.orders?.let { orders ->
                if (orders.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Produk (${orders.size} item):",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    orders.take(2).forEach { order ->
                        Text(
                            text = "• ${order.product} (${order.quantity}x)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    if (orders.size > 2) {
                        Text(
                            text = "... dan ${orders.size - 2} produk lainnya",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEditClick) {
                    Text("Edit")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Hapus")
                }
            }
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp)),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
        shadowElevation = 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
        ) {
            Icon(
                Icons.Default.FilterAlt,
                contentDescription = "Filter",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun FilterOptionItem(
    text: String,
    isSelected: Boolean,
    statusColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        color = if (isSelected) statusColor.copy(alpha = 0.1f) else Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = androidx.compose.material3.RadioButtonDefaults.colors(
                    selectedColor = statusColor
                )
            )
            Spacer(modifier = Modifier.width(12.dp))

            if (text != "All Queues") {
                Surface(
                    color = statusColor,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(12.dp)
                ) {}
                Spacer(modifier = Modifier.width(12.dp))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                color = if (isSelected) statusColor else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun EditQueueDialog(
    queue: DataItem,
    onDismiss: () -> Unit,
    onUpdateQueue: (Int, Int, Int?) -> Unit
) {
    val statusOptions = listOf(
        1 to "In queue",
        2 to "In process",
        3 to "Unpaid",
        4 to "Completed"
    )

    val paymentMethods = listOf(
        1 to "Cash",
        2 to "Account Balance"
    )

    var selectedStatusId by remember {
        mutableIntStateOf(
            statusOptions.find { it.second == queue.status }?.first ?: 1
        )
    }

    var selectedPaymentId by remember { mutableStateOf<Int?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.width(320.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Edit Queue Status",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Customer: ${queue.customer}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Total: Rp ${queue.grandTotal}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Select New Status:",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                statusOptions.forEach { (id, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedStatusId = id
                                // Reset payment method when status changes
                                if (id != 4) selectedPaymentId = null
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStatusId == id,
                            onClick = {
                                selectedStatusId = id
                                // Reset payment method when status changes
                                if (id != 4) selectedPaymentId = null
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Show payment method selection if status is "Completed"
                if (selectedStatusId == 4) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Select Payment Method:",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    paymentMethods.forEach { (id, name) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPaymentId = id }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedPaymentId == id,
                                onClick = { selectedPaymentId = id }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            queue.id?.let { queueId ->
                                // Validate payment method selection for completed status
                                if (selectedStatusId == 4 && selectedPaymentId == null) {
                                    // Could show error message here
                                    return@let
                                }
                                onUpdateQueue(queueId, selectedStatusId, selectedPaymentId)
                            }
                        },
                        enabled = if (selectedStatusId == 4) selectedPaymentId != null else true
                    ) {
                        Text("Update")
                    }
                }
            }
        }
    }
}