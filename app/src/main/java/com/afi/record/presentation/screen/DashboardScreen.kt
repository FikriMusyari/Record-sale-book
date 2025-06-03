package com.afi.record.presentation.screen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.afi.record.domain.models.UpdateUserRequest
import com.afi.record.domain.models.UserResponse
import com.afi.record.domain.useCase.AuthResult
import com.afi.record.presentation.Screen
import com.afi.record.presentation.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel, navController: NavController) {
    val scrollState = rememberScrollState()
    val datauser by viewModel.userData.collectAsStateWithLifecycle()
    val dashboardResult by viewModel.dashboardResult.collectAsStateWithLifecycle()

    var showDateFilter by remember { mutableStateOf(false) }
    var selectedDateRange by remember { mutableStateOf("All time") }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var nama by remember { mutableStateOf("") }

    // Fun snackbar state for showing messages
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var snackbarIsError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadCurrentUser()
    }

    // Handle dashboard result state changes
    LaunchedEffect(dashboardResult) {
        when (val result = dashboardResult) {
            is AuthResult.Success<*> -> {
                snackbarMessage = result.message
                snackbarIsError = false
                showSnackbar = true

                // Handle logout success - navigate to login
                if (result.data == "logout_success") {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }

                // Close update dialog on success
                if (showChangePasswordDialog && result.data is UserResponse) {
                    showChangePasswordDialog = false
                    nama = ""
                    oldPassword = ""
                    newPassword = ""
                }
            }
            is AuthResult.Error -> {
                snackbarMessage = result.message
                snackbarIsError = true
                showSnackbar = true
            }
            else -> { /* Loading or Idle - handled in UI */ }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF1E293B) // Dark background color
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (val result = dashboardResult) {
                            is AuthResult.Loading -> "⏳ ${result.message}"
                            else -> when (val user = datauser) {
                                null -> "🔄 Memuat profil..."
                                else -> "👋 Halo, ${user.nama}!"
                            }
                        },
                        fontSize = 20.sp,
                        color = Color.LightGray,
                        fontWeight = FontWeight.Normal
                    )

                    // Show loading indicator when loading
                    if (dashboardResult is AuthResult.Loading) {
                        Spacer(modifier = Modifier.width(8.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.LightGray,
                            strokeWidth = 2.dp
                        )
                    }
                }

                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("🔧 Update Account") },
                            onClick = {
                                expanded = false
                                showChangePasswordDialog = true
                                // Pre-fill current user name
                                nama = datauser?.nama ?: ""
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("👋 Logout") },
                            onClick = {
                                expanded = false
                                showLogoutDialog = true
                            }
                        )
                    }
                }
            }

            if (showChangePasswordDialog) {
                AlertDialog(
                    onDismissRequest = {
                        if (dashboardResult !is AuthResult.Loading) {
                            showChangePasswordDialog = false
                            nama = ""
                            oldPassword = ""
                            newPassword = ""
                            viewModel.resetDashboardState()
                        }
                    },
                    title = { Text("🔧 Update Account") },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = nama,
                                onValueChange = { nama = it },
                                label = { Text("Nama User") },
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = oldPassword,
                                onValueChange = { oldPassword = it },
                                label = { Text("Password Lama") },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newPassword,
                                onValueChange = { newPassword = it },
                                label = { Text("Password Baru") },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (dashboardResult !is AuthResult.Loading) {
                                    val request = UpdateUserRequest(
                                        nama = if (nama.isNotBlank()) nama else null,
                                        oldPassword = if (oldPassword.isNotBlank()) oldPassword else null,
                                        newPassword = if (newPassword.isNotBlank()) newPassword else null
                                    )
                                    viewModel.updateUserProfile(request)
                                }
                            },
                            enabled = dashboardResult !is AuthResult.Loading
                        ) {
                            if (dashboardResult is AuthResult.Loading) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Updating...")
                                }
                            } else {
                                Text("💾 Submit")
                            }
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                if (dashboardResult !is AuthResult.Loading) {
                                    showChangePasswordDialog = false
                                    nama = ""
                                    oldPassword = ""
                                    newPassword = ""
                                    viewModel.resetDashboardState()
                                }
                            },
                            enabled = dashboardResult !is AuthResult.Loading
                        ) {
                            Text("❌ Cancel")
                        }
                    }
                )
            }

            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = {
                        if (dashboardResult !is AuthResult.Loading) {
                            showLogoutDialog = false
                        }
                    },
                    title = { Text(text = "👋 Logout") },
                    text = {
                        when (val result = dashboardResult) {
                            is AuthResult.Loading -> {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(result.message)
                                }
                            }
                            else -> {
                                Text("🤔 Apakah Anda yakin ingin keluar?")
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (dashboardResult !is AuthResult.Loading) {
                                    viewModel.logout()
                                    showLogoutDialog = false
                                }
                            },
                            enabled = dashboardResult !is AuthResult.Loading
                        ) {
                            Text("✅ Ya, Keluar")
                        }
                    },
                    dismissButton = {
                        if (dashboardResult !is AuthResult.Loading) {
                            TextButton(onClick = { showLogoutDialog = false }) {
                                Text("❌ Batal")
                            }
                        }
                    }
                )
            }

            // Summary Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF242424) // Darker card background
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Summary",
                            fontSize = 24.sp,
                            color = Color.White
                        )

                        // Date Filter Button
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFF333333),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .clickable { showDateFilter = true }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedDateRange,
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.size(4.dp))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Show date filter",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Graph area (placeholder for actual chart)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        // Horizontal lines
                        for (i in 0..5) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color(0xFF444444))
                                    .align(
                                        when (i) {
                                            0 -> Alignment.BottomCenter
                                            5 -> Alignment.TopCenter
                                            else -> Alignment.Center
                                        }
                                    )
                            )

                            // Y-axis labels
                            Text(
                                text = "${5-i}",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .align(
                                        when (i) {
                                            0 -> Alignment.BottomStart
                                            5 -> Alignment.TopStart
                                            else -> Alignment.CenterStart
                                        }
                                    )
                                    .padding(bottom = if (i == 0) 0.dp else 8.dp)
                            )
                        }

                        // X-axis labels
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Jan", color = Color.Gray, fontSize = 14.sp)
                            Text("Feb", color = Color.Gray, fontSize = 14.sp)
                            Text("Mar", color = Color.Gray, fontSize = 14.sp)
                            Text("Apr", color = Color.Gray, fontSize = 14.sp)
                            Text("May", color = Color.Gray, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Stats cards
                    StatItem(
                        icon = "clipboard",
                        title = "Total queues",
                        count = "0",
                        backgroundColor = Color(0xFF1E3954)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    StatItem(
                        icon = "warning",
                        title = "Uncompleted queues",
                        count = "0",
                        backgroundColor = Color(0xFF1E3954)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    StatItem(
                        icon = "person",
                        title = "Active customers",
                        count = "0",
                        backgroundColor = Color(0xFF1E3954)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    StatItem(
                        icon = "tag",
                        title = "Products sold",
                        count = "0",
                        backgroundColor = Color(0xFF1E3954)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Revenue Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF242424)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Revenue",
                            fontSize = 24.sp,
                            color = Color.White
                        )

                        // Date Filter Button
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFF333333),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .clickable { showDateFilter = true }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedDateRange,
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.size(4.dp))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Show date filter",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Revenue chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        // Horizontal lines
                        for (i in 0..5) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color(0xFF444444))
                                    .align(
                                        when (i) {
                                            0 -> Alignment.BottomCenter
                                            5 -> Alignment.TopCenter
                                            else -> Alignment.Center
                                        }
                                    )
                            )

                            // Y-axis labels
                            Text(
                                text = "$$i",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .align(
                                        when (i) {
                                            0 -> Alignment.BottomStart
                                            5 -> Alignment.TopStart
                                            else -> Alignment.CenterStart
                                        }
                                    )
                                    .padding(bottom = if (i == 0) 0.dp else 8.dp)
                            )
                        }

                        // X-axis labels
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Jan", color = Color.Gray, fontSize = 14.sp)
                            Text("Feb", color = Color.Gray, fontSize = 14.sp)
                            Text("Mar", color = Color.Gray, fontSize = 14.sp)
                            Text("Apr", color = Color.Gray, fontSize = 14.sp)
                            Text("May", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp)) // Reduced bottom spacing since navbar is removed
        }

        // Date Filter Bottom Sheet
        if (showDateFilter) {
            DateFilterBottomSheet(
                onDismiss = { showDateFilter = false },
                onDateRangeSelected = { range ->
                    selectedDateRange = range
                    showDateFilter = false
                },
                currentSelection = selectedDateRange
            )
        }

        // Fun Snackbar for showing messages
        if (showSnackbar) {
            LaunchedEffect(showSnackbar) {
                kotlinx.coroutines.delay(3000) // Show for 3 seconds
                showSnackbar = false
                viewModel.clearDashboardError()
            }
        }
    }

    // Snackbar positioned at bottom
    if (showSnackbar) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                colors = CardDefaults.cardColors(
                    containerColor = if (snackbarIsError) Color(0xFFD32F2F) else Color(0xFF388E3C)
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
                        onClick = {
                            showSnackbar = false
                            viewModel.clearDashboardError()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(icon: String, title: String, count: String, backgroundColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // We would typically use a real icon resource
                // For now using a placeholder Box as icon
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.White, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (icon) {
                            "clipboard" -> "📋"
                            "warning" -> "⚠️"
                            "person" -> "👤"
                            "tag" -> "🏷️"
                            else -> "📄"
                        },
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.size(16.dp))

                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            Text(
                text = count,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateFilterBottomSheet(
    onDismiss: () -> Unit,
    onDateRangeSelected: (String) -> Unit,
    currentSelection: String) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        containerColor = Color(0xFF242424)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 4.dp)
                    .background(Color.Gray, RoundedCornerShape(2.dp))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Select date",
                fontSize = 22.sp,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            DateRangeOption(
                title = "All time",
                isSelected = currentSelection == "All time",
                onClick = { onDateRangeSelected("All time") }
            )

            DateRangeOption(
                title = "Today",
                isSelected = currentSelection == "Today",
                onClick = { onDateRangeSelected("Today") }
            )

            DateRangeOption(
                title = "Yesterday",
                isSelected = currentSelection == "Yesterday",
                onClick = { onDateRangeSelected("Yesterday") }
            )

            DateRangeOption(
                title = "This week",
                isSelected = currentSelection == "This week",
                onClick = { onDateRangeSelected("This week") }
            )

            DateRangeOption(
                title = "This month",
                isSelected = currentSelection == "This month",
                onClick = { onDateRangeSelected("This month") }
            )

            DateRangeOption(
                title = "This year",
                isSelected = currentSelection == "This year",
                onClick = { onDateRangeSelected("This year") }
            )

            DateRangeOption(
                title = "Custom range",
                isSelected = currentSelection == "Custom range",
                onClick = { onDateRangeSelected("Custom range") },
                textColor = Color(0xFF4A90E2)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DateRangeOption(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    textColor: Color = Color.White
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(
                color = if (isSelected) Color(0xFF1E3954) else Color.Transparent
            )
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    color = Color(0xFF4A90E2),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.size(16.dp))
        }

        Text(
            text = title,
            color = if (isSelected) Color.White else textColor,
            fontSize = 18.sp
        )
    }
}