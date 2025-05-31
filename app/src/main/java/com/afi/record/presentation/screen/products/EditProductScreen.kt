package com.afi.record.presentation.screen.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.afi.record.domain.models.UpdateProductRequest
import com.afi.record.presentation.viewmodel.ProductViewModel
import java.math.BigDecimal


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen( viewModel: ProductViewModel, navController: NavController) {

    val productToEditState by viewModel.productToEdit.collectAsStateWithLifecycle()

    var productNama by rememberSaveable(productToEditState) { mutableStateOf(productToEditState?.nama ?: "") }

    var productPrice by rememberSaveable(productToEditState) { mutableStateOf(productToEditState?.price?.toString() ?: "") }

    var namaError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(productToEditState) {
        println("EditProductScreen productToEditState = $productToEditState")
    }

    DisposableEffect (Unit) {
        onDispose {
            viewModel.clearProductIdToEdit()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (productToEditState == null) "Edit Product" else "Loading Product...",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
             if (productToEditState == null) {
                    CircularProgressIndicator()
                    Text("Loading product details...")
                    Text(
                        "Please ensure a product was selected for editing.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Button(onClick = { navController.popBackStack() }) { Text("Go Back") }

            } else {
                val currentProduct = productToEditState!!

                Text(
                    "Editing: ${currentProduct.nama}",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = productNama,
                    onValueChange = { productNama = it; namaError = null },
                    label = { Text("Product Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = namaError != null,
                    supportingText = {
                        if (namaError != null) Text(
                            namaError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                )

                OutlinedTextField(
                    value = productPrice,
                    onValueChange = { productPrice = it; priceError = null },
                    label = { Text("Price (e.g., 55000)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = priceError != null,
                    supportingText = {
                        if (priceError != null) Text(
                            priceError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        var isValid = true
                        if (productNama.isBlank()) {
                            namaError = "Product name cannot be empty"; isValid = false
                        }
                        val priceDecimal = try {
                            BigDecimal(productPrice.trim().replace(",", ""))
                        } catch (e: NumberFormatException) {
                            null
                        }
                        if (priceDecimal == null || priceDecimal < BigDecimal.ZERO) {
                            priceError = "Please enter a valid positive price"; isValid = false
                        }

                        if (isValid && priceDecimal != null) {

                            if (productNama != null || priceDecimal != null) {
                                val updateRequest =
                                    UpdateProductRequest(nama = productNama,
                                        price = priceDecimal)

                                viewModel.updateProduct(currentProduct.id, updateRequest)
                                navController.popBackStack()

                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = productToEditState != null
                ) {
                    Text("Save Changes")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}