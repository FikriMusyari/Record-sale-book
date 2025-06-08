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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel, navController: NavController) {
    val scrollState = rememberScrollState()
    val datauser by viewModel.userData.collectAsStateWithLifecycle()
    val dashboardResult by viewModel.dashboardResult.collectAsStateWithLifecycle()
    val dashboardMetrics by viewModel.dashboardMetrics.collectAsStateWithLifecycle()

    // Loading state for refresh functionality
    val isRefreshing = dashboardResult is AuthResult.Loading

    var showDateFilter by remember { mutableStateOf(false) }
    var selectedDateRange by remember { mutableStateOf("Semua Waktu") }
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
        viewModel.loadDashboardData()
    }

    // Refresh data when returning to dashboard
    LaunchedEffect(navController.currentBackStackEntry) {
        if (navController.currentBackStackEntry?.destination?.route == "dashboard") {
            viewModel.loadDashboardData()
        }
    }

    // Auto-refresh handling - removed pull-to-refresh for compatibility

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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF0F172A) // Modern dark background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Dashboard",
                                fontSize = 24.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = when (val result = dashboardResult) {
                                    is AuthResult.Loading -> "⏳ ${result.message}"
                                    else -> when (val user = datauser) {
                                        null -> "Memuat profil..."
                                        else -> "Halo, ${user.nama}!"
                                    }
                                },
                                fontSize = 14.sp,
                                color = Color(0xFF9CA3AF),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Show loading indicator when loading
                            if (dashboardResult is AuthResult.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color(0xFF3B82F6),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                            }

                            // Refresh button
                            Surface(
                                modifier = Modifier.clickable {
                                    viewModel.loadDashboardData()
                                },
                                color = Color(0xFF374151),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh",
                                    tint = Color.White,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Settings button
                            Box {
                                Surface(
                                    modifier = Modifier.clickable { expanded = true },
                                    color = Color(0xFF374151),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Settings",
                                        tint = Color.White,
                                        modifier = Modifier.padding(8.dp)
                                    )
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
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Revenue Overview - Modern Minimal Design
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1F2937)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total Pendapatan",
                                fontSize = 16.sp,
                                color = Color(0xFF9CA3AF),
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(dashboardMetrics.revenue.toDouble()),
                                fontSize = 28.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Date Filter Button - Modern Style
                        Surface(
                            modifier = Modifier.clickable { showDateFilter = true },
                            color = Color(0xFF374151),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedDateRange,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Show date filter",
                                    tint = Color(0xFF9CA3AF),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Urgent Alert - Only show when needed
                if (dashboardMetrics.uncompletedQueues > 0) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .clickable { navController.navigate(Screen.Queue.route) },
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFEF2F2)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        Color(0xFFEF4444),
                                        RoundedCornerShape(20.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "⚠️",
                                    fontSize = 18.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Perhatian Diperlukan!",
                                    color = Color(0xFF991B1B),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${dashboardMetrics.uncompletedQueues} antrian belum selesai",
                                    color = Color(0xFF7F1D1D),
                                    fontSize = 14.sp
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Lihat antrian",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Business Metrics - Clean Grid
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1F2937)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Ringkasan Bisnis",
                            fontSize = 20.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            MetricCard(
                                modifier = Modifier.weight(1f),
                                icon = "📋",
                                title = "Total Antrian",
                                value = dashboardMetrics.totalQueues.toString(),
                                color = Color(0xFF3B82F6)
                            )
                            MetricCard(
                                modifier = Modifier.weight(1f),
                                icon = "⏳",
                                title = "Belum Selesai",
                                value = dashboardMetrics.uncompletedQueues.toString(),
                                color = Color(0xFFEF4444)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            MetricCard(
                                modifier = Modifier.weight(1f),
                                icon = "👥",
                                title = "Customer Aktif",
                                value = dashboardMetrics.activeCustomers.toString(),
                                color = Color(0xFF10B981)
                            )
                            MetricCard(
                                modifier = Modifier.weight(1f),
                                icon = "📦",
                                title = "Produk Terjual",
                                value = dashboardMetrics.productsSold.toString(),
                                color = Color(0xFFF59E0B)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Quick Actions - Modern Design
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1F2937)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Tindakan Cepat",
                            fontSize = 20.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            QuickActionButton(
                                modifier = Modifier.weight(1f),
                                icon = "➕",
                                title = "Antrian Baru",
                                subtitle = "Buat pesanan",
                                backgroundColor = Color(0xFF10B981),
                                onClick = { navController.navigate(Screen.AddQueue.route) }
                            )

                            QuickActionButton(
                                modifier = Modifier.weight(1f),
                                icon = "📋",
                                title = "Lihat Antrian",
                                subtitle = "Kelola status",
                                backgroundColor = Color(0xFF8B5CF6),
                                onClick = { navController.navigate(Screen.Queue.route) }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            QuickActionButton(
                                modifier = Modifier.weight(1f),
                                icon = "👥",
                                title = "Customer",
                                subtitle = "Kelola data",
                                backgroundColor = Color(0xFF3B82F6),
                                onClick = { navController.navigate(Screen.Customer.route) }
                            )

                            QuickActionButton(
                                modifier = Modifier.weight(1f),
                                icon = "🏷️",
                                title = "Produk",
                                subtitle = "Atur harga",
                                backgroundColor = Color(0xFFF59E0B),
                                onClick = { navController.navigate(Screen.Product.route) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp)) // Bottom spacing for scroll content
            }
        }

        // Dialog components - properly positioned within main Box scope
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

        // Snackbar positioned at bottom - properly within main Box scope
        if (showSnackbar) {
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

        // Refresh functionality available through refresh button in top bar
    }
}

@Composable
fun QuickActionButton(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    subtitle: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clickable { onClick() },
        color = backgroundColor.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(backgroundColor, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                color = Color(0xFF9CA3AF),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    value: String,
    color: Color
) {
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                color = Color(0xFF9CA3AF),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
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
                text = "Pilih Periode",
                fontSize = 22.sp,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            DateRangeOption(
                title = "Semua Waktu",
                isSelected = currentSelection == "Semua Waktu",
                onClick = { onDateRangeSelected("Semua Waktu") }
            )

            DateRangeOption(
                title = "Hari Ini",
                isSelected = currentSelection == "Hari Ini",
                onClick = { onDateRangeSelected("Hari Ini") }
            )

            DateRangeOption(
                title = "Kemarin",
                isSelected = currentSelection == "Kemarin",
                onClick = { onDateRangeSelected("Kemarin") }
            )

            DateRangeOption(
                title = "Minggu Ini",
                isSelected = currentSelection == "Minggu Ini",
                onClick = { onDateRangeSelected("Minggu Ini") }
            )

            DateRangeOption(
                title = "Bulan Ini",
                isSelected = currentSelection == "Bulan Ini",
                onClick = { onDateRangeSelected("Bulan Ini") }
            )

            DateRangeOption(
                title = "Tahun Ini",
                isSelected = currentSelection == "Tahun Ini",
                onClick = { onDateRangeSelected("Tahun Ini") }
            )

            DateRangeOption(
                title = "Rentang Kustom",
                isSelected = currentSelection == "Rentang Kustom",
                onClick = { onDateRangeSelected("Rentang Kustom") },
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