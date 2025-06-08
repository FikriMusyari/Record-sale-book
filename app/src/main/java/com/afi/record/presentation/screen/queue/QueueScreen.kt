package com.afi.record.presentation.screen.queue

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.afi.record.presentation.Screen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.afi.record.presentation.viewmodel.QueueViewModel
import com.afi.record.domain.useCase.AuthResult
import com.afi.record.domain.models.DataItem
import com.afi.record.domain.models.UpdateQueueRequest
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueScreen(
    navController: NavController,
    viewModel: QueueViewModel = hiltViewModel()
) {
    var showFilters by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
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

    LaunchedEffect(isSearchActive) {
        if (isSearchActive) {
            focusRequester.requestFocus()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (isSearchActive) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester),
                                placeholder = { Text("Search...") },
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = "Search")
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Close, contentDescription = "Clear")
                                        }
                                    }
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        // Handle search logic here
                                    }
                                )
                            )
                        }
                    } else {
                        Text("Queue", fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    if (!isSearchActive) {
                        Row {
                            IconButton(onClick = { isSearchActive = true }) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = { showFilters = true }) {
                                Text("Filter", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    } else {
                        IconButton(onClick = {
                            isSearchActive = false
                            searchQuery = ""
                        }) {
                            Text("Cancel", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
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
                        // Filter queues based on search and filter
                        val filteredQueues = queues.filter { queue ->
                            val matchesSearch = if (searchQuery.isBlank()) true else {
                                queue.customer?.contains(searchQuery, ignoreCase = true) == true ||
                                queue.note?.contains(searchQuery, ignoreCase = true) == true
                            }
                            val matchesFilter = if (selectedFilter == null) true else {
                                queue.status == selectedFilter
                            }
                            matchesSearch && matchesFilter
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
                    .padding(16.dp)
            ) {
                Text("Filter Options", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))

                filterOptions.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedFilter = if (selectedFilter == option) null else option
                                showFilters = false
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedFilter == option,
                            onClick = {
                                selectedFilter = if (selectedFilter == option) null else option
                                showFilters = false
                            }
                        )
                        Text(
                            text = option,
                            modifier = Modifier.padding(start = 12.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
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
            onUpdateStatus = { queueId, newStatusId ->
                viewModel.updateQueue(queueId, UpdateQueueRequest(statusId = newStatusId))
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
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 12.dp, end = 4.dp)
        ) {
            Text(text, style = MaterialTheme.typography.bodySmall)
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
fun EditQueueDialog(
    queue: DataItem,
    onDismiss: () -> Unit,
    onUpdateStatus: (Int, Int) -> Unit
) {
    val statusOptions = listOf(
        1 to "In queue",
        2 to "In process",
        3 to "Unpaid",
        4 to "Completed"
    )

    var selectedStatusId by remember {
        mutableStateOf(
            statusOptions.find { it.second == queue.status }?.first ?: 1
        )
    }

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
                            .clickable { selectedStatusId = id }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStatusId == id,
                            onClick = { selectedStatusId = id }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyMedium
                        )
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
                                onUpdateStatus(queueId, selectedStatusId)
                            }
                        }
                    ) {
                        Text("Update")
                    }
                }
            }
        }
    }
}