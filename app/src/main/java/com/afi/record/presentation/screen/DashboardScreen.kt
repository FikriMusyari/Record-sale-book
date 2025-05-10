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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun DashboardScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    var showDateFilter by remember { mutableStateOf(false) }
    var selectedDateRange by remember { mutableStateOf("All time") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF121212) // Dark background color
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
                Text(
                    text = "anjay",
                    fontSize = 34.sp,
                    color = Color.LightGray,
                    fontWeight = FontWeight.Normal
                )

                IconButton(onClick = { /* Settings action */ }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.LightGray,
                        modifier = Modifier.size(28.dp)
                    )
                }
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

                    // Revenue chart (placeholder)
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
    currentSelection: String
) {
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