package com.afi.record.presentation.screen

import CreateNewDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.afi.record.domain.models.Products
import com.afi.record.domain.useCase.ProductResult
import com.afi.record.presentation.Screen
import com.afi.record.presentation.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(viewModel: ProductViewModel, navController: NavController) {
    var showCreateNew by remember { mutableStateOf(false) }
    val productsUiState by viewModel.productsState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedProductForAction by remember { mutableStateOf<Products?>(null) }

    var selectedCreateOption by remember { mutableStateOf("Product") }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.getAllProducts()
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
                            text = "Products",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        TextField(
                            value = searchQuery,
                            onValueChange = { newQuery ->
                                viewModel.searchProducts(newQuery)
                            },
                            placeholder = { Text("Search products...") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Filled.Search, contentDescription = "Search Icon")
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.searchProducts("") }) {
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
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (searchQuery.isEmpty()) {
                        focusManager.clearFocus()
                    }
                }
        ) {
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

            when (val state = productsUiState) {
                is ProductResult.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Loading products...", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
                is ProductResult.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(16.dp)
                            )
                            Button(onClick = { viewModel.getAllProducts() }) {
                                Text("Try Again")
                            }
                        }
                    }
                }
                is ProductResult.Success -> {
                    val products = state.data
                    if (products.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (searchQuery.isEmpty()) "No products added" else "No products found",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            if (searchQuery.isEmpty()) {
                                Text("Add a product to manage your inventory", style = MaterialTheme.typography.bodyMedium)
                            } else {
                                Text("Try different keywords or clear the search.", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(products.size, key = { products[it].id.toString() }) { index ->
                                val product = products[index]
                                ProductListItem(
                                    product = product,
                                    onItemClick = {
                                        println("Product clicked: ${product.nama}")

                                    },
                                    onMoreActionsClick = { productForAction ->
                                        selectedProductForAction = productForAction
                                        showBottomSheet = true
                                    }
                                )
                            }
                        }
                    }
                }
                null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Welcome! Add or search for products.", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }

        if (showBottomSheet && selectedProductForAction != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                    selectedProductForAction = null
                },
                sheetState = bottomSheetState,
            ) {
                Column(modifier = Modifier.padding(bottom = 16.dp, start = 16.dp, end = 16.dp)) {
                    ListItem(
                        headlineContent = { Text("Edit") },
                        leadingContent = { Icon(Icons.Filled.Edit, contentDescription = "Edit Product") },
                        modifier = Modifier.clickable {
                            val productToEdit = selectedProductForAction!!
                            viewModel.setProductId(productToEdit.id)
                            navController.navigate(Screen.EditProduct.route)
                            showBottomSheet = false
                            selectedProductForAction = null
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Delete") },
                        leadingContent = { Icon(Icons.Filled.Delete, contentDescription = "Delete Product") },
                        modifier = Modifier.clickable {
                            val productToDelete = selectedProductForAction!!
                            viewModel.deleteProduct(productToDelete.id)
                            showBottomSheet = false
                            selectedProductForAction = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductListItem(
    product: Products,
    onItemClick: () -> Unit,
    onMoreActionsClick: (Products) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = product.nama.firstOrNull()?.uppercaseChar()?.toString() ?: "P",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.nama,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Price: Rp ${product.price}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))


            IconButton(onClick = { onMoreActionsClick(product) }) {
                Icon(
                    Icons.Filled.MoreVert,
                    contentDescription = "More options",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}