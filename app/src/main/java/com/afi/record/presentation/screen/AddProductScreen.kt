package com.afi.record.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.afi.record.domain.models.CreateProductRequest
import com.afi.record.domain.useCase.ProductResult
import com.afi.record.presentation.Screen
import com.afi.record.presentation.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(viewModel: ProductViewModel, navController: NavController) {
    var nama by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val createState by viewModel.productsState.collectAsStateWithLifecycle()

    LaunchedEffect(createState) {
        when (createState) {
            is ProductResult.Loading -> {
            }
            is ProductResult.Success -> {
                navController.navigate(Screen.Product.route) {
                    popUpTo(Screen.AddProduct.route) { inclusive = true }
                }
            }
            is ProductResult.Error -> {
                val errorMessage = (createState as ProductResult.Error).message
            }
            else -> {  }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create product",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = nama,
            onValueChange = { nama = it },
            label = { Text("Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = price,
            onValueChange = { input ->
                if (input.matches(Regex("^\\d*\\.?\\d*\$"))) {
                    price = input
                } },
            label = { Text("Price") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        Button(
            onClick = {
                val priceDouble = price.toDoubleOrNull()
                if (nama.isBlank()) {
                    errorMessage = "Name tidak boleh kosong"
                } else if (priceDouble == null) {
                    errorMessage = "Harga tidak valid"
                } else {
                    errorMessage = null
                    viewModel.createProduct(CreateProductRequest(nama, priceDouble))
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }
    }
}
