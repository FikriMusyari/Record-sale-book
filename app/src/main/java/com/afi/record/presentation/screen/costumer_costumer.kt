package com.afi.record.presentation.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                CustomerScreen()
            }
        }
    }
}

@Composable
fun MyAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF6200EE),
            secondary = Color(0xFF03DAC6),
            background = Color(0xFFFFFFFF)
        ),
        content = content
    )
}

@Composable
fun CustomerScreen() {
    var name by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf(0) }
    var debt by remember { mutableStateOf(0) }
    var showAddBalanceDialog by remember { mutableStateOf(false) }
    var amountToAdd by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Create customer",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Balance", modifier = Modifier.weight(1f))
            Text(text = "$$balance", fontWeight = FontWeight.Bold)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Debt", modifier = Modifier.weight(1f))
            Text(text = "$$debt", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showAddBalanceDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add balance")
        }

        Button(
            onClick = { /* Handle withdraw balance */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Withdraw balance")
        }
    }

    if (showAddBalanceDialog) {
        AlertDialog(
            onDismissRequest = { showAddBalanceDialog = false },
            title = { Text("Add balance") },
            text = {
                Column {
                    OutlinedTextField(
                        value = amountToAdd,
                        onValueChange = { amountToAdd = it },
                        label = { Text("Amount") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        amountToAdd.toIntOrNull()?.let {
                            balance += it
                            amountToAdd = ""
                            showAddBalanceDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showAddBalanceDialog = false
                        amountToAdd = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCustomerScreen() {
    MyAppTheme {
        CustomerScreen()
    }
}