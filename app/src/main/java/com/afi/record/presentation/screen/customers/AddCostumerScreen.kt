package com.afi.record.presentation.screen.customers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
fun AddCustomerScreen() {
    var name by remember { mutableStateOf("") }
    var balance by remember { mutableIntStateOf(0) }
    var debt by remember { mutableIntStateOf(0) }
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
