// SignUpScreen.kt
package com.afi.record.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.afi.record.presentation.AuthViewModel
import com.afi.record.presentation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    val authViewModel: AuthViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }

    var nama by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Handle Error Message
    LaunchedEffect(authViewModel.errorMessage) {
        authViewModel.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            authViewModel.errorMessage = null
        }
    }

    // Handle Success Register
    LaunchedEffect(authViewModel.currentUser) {
        if (authViewModel.currentUser != null) {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.SignUp.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF2F6FED))
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Daftar Akun",
                    fontSize = 28.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text("Username") },
                    leadingIcon = { Icon(Icons.Default.Person, "Username") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        focusedLeadingIconColor = Color.White,
                        unfocusedLeadingIconColor = Color.White.copy(alpha = 0.7f)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, "Email") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        focusedLeadingIconColor = Color.White,
                        unfocusedLeadingIconColor = Color.White.copy(alpha = 0.7f)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, "Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        focusedLeadingIconColor = Color.White,
                        unfocusedLeadingIconColor = Color.White.copy(alpha = 0.7f)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))



                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        authViewModel.register(
                            nama = nama,
                            email = email,
                            password = password
                        ) {}
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F2D7A),
                        contentColor = Color.White
                    ),
                    enabled = !authViewModel.isLoading
                ) {
                    if (authViewModel.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text("DAFTAR", fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Sudah punya akun? Masuk sekarang",
                    color = Color.White,
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.SignIn.route)
                    }
                )
            }
        }
    }
}