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
import com.afi.record.domain.useCase.AuthResult
import com.afi.record.presentation.Screen
import com.afi.record.presentation.viewmodel.ProductViewModel
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(viewModel: ProductViewModel, navController: NavController) {
    var nama by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    val createState by viewModel.createProductState.collectAsStateWithLifecycle()

    LaunchedEffect(createState) {
        when (createState) {
            is AuthResult.Loading -> {
            }
            is AuthResult.Success<*> -> {
                navController.navigate(Screen.Product.route) {
                    popUpTo(Screen.AddProduct.route) { inclusive = true }
                }
            }
            is AuthResult.Error -> {
                val errorMessage = (createState as AuthResult.Error).message
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
            onValueChange = { price = it },
            label = { Text("Price") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        Button(
            onClick = {
                val priceDecimal = try {
                    BigDecimal(price)
                } catch (e: Exception) {
                    null
                }

                if (nama.isBlank()) {
                    return@Button
                }

                if (priceDecimal == null || priceDecimal <= BigDecimal.ZERO) {
                    return@Button
                }

                viewModel.createProduct(nama, priceDecimal)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }
    }
}
