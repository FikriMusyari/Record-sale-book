package com.afi.record.presentation.screen.products

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.afi.record.domain.models.Products
import com.afi.record.domain.useCase.ProductResult
import com.afi.record.presentation.viewmodel.ProductViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    viewModel: ProductViewModel
) {
    val products by viewModel.productsState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var showCreateNew by remember { mutableStateOf(false) }
    var createName by remember { mutableStateOf("") }
    var createPrice by remember { mutableStateOf("") }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var snackbarIsError by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.getAllProducts()
    }

    // Show snackbar when needed
    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            scope.launch {
                snackbarHostState.showSnackbar(snackbarMessage)
                showSnackbar = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF0F172A)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Modern Top Bar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF1E293B),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🛍️ Produk",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )

                        // Add Product Button
                        Surface(
                            modifier = Modifier.clickable { showCreateNew = true },
                            color = Color(0xFF3B82F6),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Tambah Produk",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Tambah",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { query ->
                        if (query.isNotBlank()) {
                            viewModel.searchproducts(query)
                        } else {
                            viewModel.getAllProducts()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    placeholder = {
                        Text(
                            "Cari produk...",
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
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF3B82F6),
                        unfocusedBorderColor = Color(0xFF374151),
                        cursorColor = Color(0xFF3B82F6)
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Product List
                when (val state = products) {
                    is ProductResult.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    color = Color(0xFF3B82F6),
                                    modifier = Modifier.size(48.dp),
                                    strokeWidth = 4.dp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Memuat data produk...",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                    is ProductResult.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "❌ Gagal memuat data",
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
                            }
                        }
                    }

                    is ProductResult.Success -> {
                        val productList = state.data
                        if (productList.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "🛍️ Produk tidak ditemukan",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        if (searchQuery.isEmpty()) "Tambah produk untuk memulai" else "Coba kata kunci lain atau tambah produk baru",
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
                                items(productList.size, key = { productList[it].id.toString() }) { index ->
                                    val product = productList[index]
                                    ModernProductListItem(
                                        product = product,
                                        onDelete = {
                                            viewModel.deleteProduct(product.id)
                                            snackbarMessage = "🗑️ Produk berhasil dihapus"
                                            snackbarIsError = false
                                            showSnackbar = true
                                        },
                                        onUpdate = { id, newName, newPrice ->
                                            viewModel.updateProduct(id, newName, newPrice)
                                            snackbarMessage = "✅ Produk berhasil diperbarui"
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
                                "Selamat datang! Tambah atau cari produk.",
                                color = Color(0xFF9CA3AF),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { snackbarData ->
            Snackbar(
                snackbarData = snackbarData,
                containerColor = if (snackbarIsError) Color(0xFFEF4444) else Color(0xFF10B981),
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
        }



        // Create New Product Dialog
        if (showCreateNew) {
            Dialog(onDismissRequest = { showCreateNew = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1F2937),
                    modifier = Modifier.width(320.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            "🛍️ Tambah Produk Baru",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = createName,
                            onValueChange = { createName = it },
                            label = { Text("Nama Produk", color = Color(0xFF9CA3AF)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF3B82F6),
                                unfocusedBorderColor = Color(0xFF374151),
                                cursorColor = Color(0xFF3B82F6)
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = createPrice,
                            onValueChange = { input ->
                                if (input.matches(Regex("""^\d*\.?\d*$"""))) {
                                    createPrice = input
                                }
                            },
                            label = { Text("Harga", color = Color(0xFF9CA3AF)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF3B82F6),
                                unfocusedBorderColor = Color(0xFF374151),
                                cursorColor = Color(0xFF3B82F6)
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    showCreateNew = false
                                    createName = ""
                                    createPrice = ""
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF374151)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Batal", color = Color.White)
                            }
                            Button(
                                onClick = {
                                    val priceDecimal = createPrice.toBigDecimalOrNull()
                                    if (createName.isNotBlank() && priceDecimal != null) {
                                        viewModel.createProduct(createName.trim(), priceDecimal)
                                        showCreateNew = false
                                        createName = ""
                                        createPrice = ""
                                        snackbarMessage = "🎉 Produk berhasil ditambahkan!"
                                        snackbarIsError = false
                                        showSnackbar = true
                                    } else {
                                        snackbarMessage = "❌ Mohon isi nama dan harga yang valid"
                                        snackbarIsError = true
                                        showSnackbar = true
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF3B82F6)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Simpan", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernProductListItem(
    product: Products,
    onDelete: (Number) -> Unit,
    onUpdate: (Number, String?, BigDecimal?) -> Unit
) {
    var editMode by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(product.nama) }
    var editPrice by remember { mutableStateOf(product.price.toPlainString()) }
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
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Nama Produk", color = Color(0xFF9CA3AF)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF3B82F6),
                        unfocusedBorderColor = Color(0xFF374151),
                        cursorColor = Color(0xFF3B82F6)
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = editPrice,
                    onValueChange = { input ->
                        if (input.matches(Regex("""^\d*\.?\d*$"""))) {
                            editPrice = input
                        }
                    },
                    label = { Text("Harga", color = Color(0xFF9CA3AF)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
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
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            editMode = false
                            editName = product.nama
                            editPrice = product.price.toPlainString()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF374151)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Batal", color = Color.White)
                    }
                    Button(
                        onClick = {
                            val priceDecimal = editPrice.toBigDecimalOrNull()
                            if (editName.isNotBlank() && priceDecimal != null) {
                                onUpdate(product.id, editName.trim(), priceDecimal)
                                editMode = false
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Simpan", color = Color.White)
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
                            text = product.nama,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(7.dp))
                        Text(
                            text = "Harga: ${NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(product.price.toDouble())}",
                            fontSize = 14.sp,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Edit Button
                        Surface(
                            modifier = Modifier.clickable { editMode = true },
                            color = Color(0xFF3B82F6),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Edit",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Delete Button
                        Surface(
                            modifier = Modifier.clickable { showDeleteDialog = true },
                            color = Color(0xFFEF4444),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Hapus",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Hapus",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
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
            containerColor = Color(0xFF1F2937),
            title = {
                Text(
                    "🗑️ Hapus Produk",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Apakah Anda yakin ingin menghapus produk \"${product.nama}\"?",
                    color = Color(0xFF9CA3AF)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete(product.id)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Hapus", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF374151)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Batal", color = Color.White)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}
