package com.afi.record.presentation.screen.customers

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.afi.record.domain.models.Customers
import com.afi.record.domain.useCase.CustomerResult
import com.afi.record.presentation.viewmodel.CustomerViewModel
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerScreen(
    viewModel: CustomerViewModel
) {
    val customersState by viewModel.customers.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var showCreateNew by remember { mutableStateOf(false) }
    var createName by remember { mutableStateOf("") }
    var createBalance by remember { mutableStateOf("") }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var snackbarIsError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getAllCustomers()
    }

    // Handle customer result state changes
    LaunchedEffect(customersState) {
        when (val result = customersState) {
            is CustomerResult.Success -> {
                if (showCreateNew) {
                    snackbarMessage = "✅ Customer berhasil ditambahkan!"
                    snackbarIsError = false
                    showSnackbar = true
                }
            }
            is CustomerResult.Error -> {
                snackbarMessage = result.message
                snackbarIsError = true
                showSnackbar = true
            }
            else -> { /* Loading or Idle */ }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF0F172A) // Modern dark background matching dashboard
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Modern Top Bar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF1E293B),
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Kelola Customer",
                                    fontSize = 24.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = when (val state = customersState) {
                                        is CustomerResult.Success -> "${state.data.size} customer terdaftar"
                                        is CustomerResult.Loading -> "Memuat data customer..."
                                        is CustomerResult.Error -> "Gagal memuat data"
                                        else -> "Siap mengelola customer"
                                    },
                                    fontSize = 14.sp,
                                    color = Color(0xFF9CA3AF),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Add Customer Button
                            Surface(
                                modifier = Modifier.clickable { showCreateNew = true },
                                color = Color(0xFF10B981),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Tambah Customer",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Tambah",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Modern Search Bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { newQuery ->
                                if (newQuery.isNotBlank()) {
                                    viewModel.searchCustomers(newQuery)
                                } else {
                                    viewModel.getAllCustomers()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    "Cari customer...",
                                    color = Color(0xFF9CA3AF)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Cari",
                                    tint = Color(0xFF9CA3AF)
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = {
                                        viewModel.searchCustomers("")
                                    }) {
                                        Icon(
                                            Icons.Default.Clear,
                                            contentDescription = "Hapus pencarian",
                                            tint = Color(0xFF9CA3AF)
                                        )
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF3B82F6),
                                unfocusedBorderColor = Color(0xFF374151),
                                cursorColor = Color(0xFF3B82F6)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Content Area
                when (val state = customersState) {
                    is CustomerResult.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFF3B82F6),
                                    modifier = Modifier.size(48.dp),
                                    strokeWidth = 4.dp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Memuat data customer...",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                    is CustomerResult.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "❌ Gagal memuat data customer",
                                    color = Color(0xFFEF4444),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    state.message,
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Surface(
                                    modifier = Modifier.clickable { viewModel.getAllCustomers() },
                                    color = Color(0xFF3B82F6),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "Coba Lagi",
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    is CustomerResult.Success -> {
                        val customerList = state.data
                        if (customerList.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color(0xFF6B7280),
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = if (searchQuery.isEmpty()) "Belum ada customer" else "Customer tidak ditemukan",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (searchQuery.isEmpty()) "Tambahkan customer pertama Anda" else "Coba kata kunci lain atau hapus pencarian",
                                        color = Color(0xFF9CA3AF),
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(customerList.size, key = { customerList[it].id.toString() }) { index ->
                                    val customer = customerList[index]
                                    ModernCustomerListItem(
                                        customer = customer,
                                        onDelete = {
                                            viewModel.deleteCustomer(customer.id)
                                            snackbarMessage = "🗑️ Customer berhasil dihapus"
                                            snackbarIsError = false
                                            showSnackbar = true
                                        },
                                        onUpdate = { id, newName, newBalance ->
                                            viewModel.updateCustomer(id, newName, newBalance)
                                            snackbarMessage = "✅ Customer berhasil diperbarui"
                                            snackbarIsError = false
                                            showSnackbar = true
                                        }
                                    )
                                }
                            }
                        }
                    }

                    else -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Selamat datang! Mulai kelola customer Anda.",
                                color = Color(0xFF9CA3AF),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }



        // Modern Create Customer Dialog
        if (showCreateNew) {
            AlertDialog(
                onDismissRequest = {
                    showCreateNew = false
                    createName = ""
                    createBalance = ""
                },
                title = {
                    Text(
                        "👤 Tambah Customer Baru",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = createName,
                            onValueChange = { createName = it },
                            label = {
                                Text(
                                    "Nama Customer",
                                    color = Color(0xFF9CA3AF)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF3B82F6),
                                unfocusedBorderColor = Color(0xFF374151),
                                cursorColor = Color(0xFF3B82F6)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = createBalance,
                            onValueChange = { input ->
                                if (input.matches(Regex("""^\d*\.?\d*$"""))) {
                                    createBalance = input
                                }
                            },
                            label = {
                                Text(
                                    "Saldo Awal (Rp)",
                                    color = Color(0xFF9CA3AF)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(12.dp),
                            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF3B82F6),
                                unfocusedBorderColor = Color(0xFF374151),
                                cursorColor = Color(0xFF3B82F6)
                            )
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val balanceDecimal = createBalance.toBigDecimalOrNull()
                            if (createName.isNotBlank() && balanceDecimal != null && balanceDecimal >= BigDecimal.ZERO) {
                                viewModel.createCustomer(createName.trim(), balanceDecimal)
                                showCreateNew = false
                                createName = ""
                                createBalance = ""
                            } else {
                                snackbarMessage = "❌ Mohon isi nama dan saldo yang valid"
                                snackbarIsError = true
                                showSnackbar = true
                            }
                        }
                    ) {
                        Text(
                            "✅ Simpan",
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showCreateNew = false
                            createName = ""
                            createBalance = ""
                        }
                    ) {
                        Text(
                            "❌ Batal",
                            color = Color(0xFF9CA3AF)
                        )
                    }
                },
                containerColor = Color(0xFF1F2937),
                shape = RoundedCornerShape(16.dp)
            )
        }

        // Modern Snackbar
        if (showSnackbar) {
            LaunchedEffect(showSnackbar) {
                kotlinx.coroutines.delay(3000)
                showSnackbar = false
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                colors = CardDefaults.cardColors(
                    containerColor = if (snackbarIsError) Color(0xFFEF4444) else Color(0xFF10B981)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = snackbarMessage,
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = { showSnackbar = false }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Tutup",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernCustomerListItem(
    customer: Customers,
    onDelete: (Number) -> Unit,
    onUpdate: (Number, String?, BigDecimal?) -> Unit
) {
    var editMode by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(customer.nama) }
    var editBalance by remember { mutableStateOf(customer.balance.toPlainString()) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            if (editMode) {
                // Edit Mode
                Text(
                    "✏️ Edit Customer",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = {
                        Text(
                            "Nama Customer",
                            color = Color(0xFF9CA3AF)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF3B82F6),
                        unfocusedBorderColor = Color(0xFF374151),
                        cursorColor = Color(0xFF3B82F6)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = editBalance,
                    onValueChange = { input ->
                        if (input.matches(Regex("""^\d*\.?\d*$"""))) {
                            editBalance = input
                        }
                    },
                    label = {
                        Text(
                            "Saldo (Rp)",
                            color = Color(0xFF9CA3AF)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF3B82F6),
                        unfocusedBorderColor = Color(0xFF374151),
                        cursorColor = Color(0xFF3B82F6)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            editMode = false
                            editName = customer.nama
                            editBalance = customer.balance.toPlainString()
                        }
                    ) {
                        Text(
                            "❌ Batal",
                            color = Color(0xFF9CA3AF)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(
                        onClick = {
                            val balanceDecimal = editBalance.toBigDecimalOrNull()
                            if (editName.isNotBlank() && balanceDecimal != null && balanceDecimal >= BigDecimal.ZERO) {
                                onUpdate(customer.id, editName.trim(), balanceDecimal)
                                editMode = false
                            }
                        }
                    ) {
                        Text(
                            "✅ Simpan",
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                // Display Mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            customer.nama,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Saldo: ${NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(customer.balance.toDouble())}",
                            fontSize = 14.sp,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "ID: ${customer.id}",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }

                    Row {
                        // Edit Button
                        Surface(
                            modifier = Modifier.clickable { editMode = true },
                            color = Color(0xFF3B82F6).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = Color(0xFF3B82F6),
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Delete Button
                        Surface(
                            modifier = Modifier.clickable { showDeleteDialog = true },
                            color = Color(0xFFEF4444).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Hapus",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "🗑️ Hapus Customer",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Text(
                    "Apakah Anda yakin ingin menghapus customer \"${customer.nama}\"? Tindakan ini tidak dapat dibatalkan.",
                    color = Color(0xFF9CA3AF)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(customer.id)
                        showDeleteDialog = false
                    }
                ) {
                    Text(
                        "✅ Ya, Hapus",
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text(
                        "❌ Batal",
                        color = Color(0xFF9CA3AF)
                    )
                }
            },
            containerColor = Color(0xFF1F2937),
            shape = RoundedCornerShape(16.dp)
        )
    }
}
