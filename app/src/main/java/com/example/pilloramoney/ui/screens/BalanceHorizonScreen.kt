package com.example.pilloramoney.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pilloramoney.ui.theme.*
import com.example.pilloramoney.ui.viewmodels.BalanceHorizonViewModel
import java.util.*

@Composable
fun BalanceHorizonScreen(
    viewModel: BalanceHorizonViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val horizontalScrollState = rememberScrollState()

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Horizonte de Saldos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Grid Header (Month Names)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(horizontalScrollState)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(vertical = 12.dp)
            ) {
                uiState.months.forEach { month ->
                    Text(
                        text = month.monthName,
                        modifier = Modifier.width(140.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Grid Content
            val maxDays = 31 // Simplified
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(horizontalScrollState)
            ) {
                items((1..maxDays).toList()) { dayIndex ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        uiState.months.forEach { month ->
                            val dayData = month.days.find { it.day == dayIndex }
                            if (dayData != null) {
                                HorizonCell(dayData.day, dayData.balance)
                            } else {
                                Spacer(modifier = Modifier.width(140.dp).height(50.dp))
                            }
                        }
                    }
                    HorizontalDivider(thickness = 0.5.dp, color = BorderColor.copy(alpha = 0.2f))
                }
            }
        }
    }
}

@Composable
fun HorizonCell(day: Int, balance: Double) {
    val isDark = isSystemInDarkTheme()
    
    val bgColor = when {
        balance >= 2000 -> if (isDark) Color(0xFF2E7D32).copy(alpha = 0.7f) else Color(0xFFC8E6C9)
        balance >= 1000 -> if (isDark) Color(0xFF388E3C).copy(alpha = 0.5f) else Color(0xFFE8F5E9)
        balance > 0 -> if (isDark) Color(0xFF43A047).copy(alpha = 0.3f) else Color(0xFFF1F8E9)
        balance == 0.0 -> Color.Transparent
        balance >= -500 -> if (isDark) Color(0xFFFBC02D).copy(alpha = 0.3f) else Color(0xFFFFF9C4)
        else -> if (isDark) Color(0xFFD32F2F).copy(alpha = 0.3f) else Color(0xFFFFEBEE)
    }

    val textColor = when {
        balance >= 0 -> if (isDark) SuccessGreen else Color(0xFF1B5E20) // Much darker green for light mode
        else -> if (isDark) ErrorRed else Color(0xFFB71C1C) // Darker red for light mode
    }

    Row(
        modifier = Modifier
            .width(140.dp)
            .height(50.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = String.format(Locale.getDefault(), "%02d", day),
            modifier = Modifier.width(24.dp),
            fontSize = 11.sp,
            color = TextSecondary
        )
        
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(4.dp),
            color = bgColor,
            border = if (balance != 0.0) BorderStroke(0.5.dp, textColor.copy(alpha = 0.2f)) else null
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = formatHorizonValue(balance),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = textColor,
                    style = MaterialTheme.typography.bodySmall.copy(
                        shadow = if (isDark) androidx.compose.ui.graphics.Shadow(
                            color = Color.Black.copy(alpha = 0.3f),
                            offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                            blurRadius = 2f
                        ) else null
                    )
                )
            }
        }
    }
}

fun formatHorizonValue(value: Double): String {
    return when {
        Math.abs(value) >= 1000 -> String.format(Locale.getDefault(), "%.2fK", value / 1000.0)
        else -> String.format(Locale.getDefault(), "%.0f", value)
    }
}
