package com.example.pilloramoney.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pilloramoney.ui.theme.*
import com.example.pilloramoney.ui.viewmodels.HomeViewModel
import java.util.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Month Header (Matching screenshot style)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = TextSecondary)
            Text(
                text = "Julho 2026",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            "Cálculos do mês", 
            style = MaterialTheme.typography.labelMedium, 
            color = TextSecondary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        // 1. Performance
        DashboardMetricRow(
            label = "Performance",
            value = uiState.totalBalance,
            status = uiState.performanceStatus,
            color = if (uiState.totalBalance < 0) ErrorRed else SuccessGreen,
            icons = listOf(SuccessGreen, ErrorRed, PrimaryBlue, Color.Magenta) // Categories dots
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = BorderColor.copy(alpha = 0.2f))

        // 2. Economizado
        DashboardProgressMetric(
            label = "Economizado",
            percentage = uiState.savingsPercentage,
            status = if (uiState.monthSavings > 0) "Guardado" else "Nada guardado",
            color = SuccessGreen
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = BorderColor.copy(alpha = 0.2f))

        // 3. Custo de vida
        DashboardMetricRow(
            label = "Custo de vida",
            value = uiState.costOfLiving,
            status = if (uiState.costOfLiving > uiState.monthEntries) "Acima da renda" else "Dentro do esperado",
            color = if (uiState.costOfLiving > uiState.monthEntries) ErrorRed else TextPrimary,
            icons = listOf(ErrorRed, PrimaryBlue, Color.Yellow)
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = BorderColor.copy(alpha = 0.2f))

        // 4. Diário médio
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Diário médio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Box(modifier = Modifier.size(12.dp).background(Color.Magenta, CircleShape))
                    Text(" / O", style = MaterialTheme.typography.bodySmall, color = TextSecondary, modifier = Modifier.padding(start = 4.dp))
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format(Locale.getDefault(), "R$ %.2f", uiState.dailyAverageReal),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = String.format(Locale.getDefault(), "D R$ %.2f", uiState.dailyAveragePlanned),
                    style = MaterialTheme.typography.bodySmall,
                    color = ErrorRed
                )
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun DashboardMetricRow(
    label: String,
    value: Double,
    status: String,
    color: Color,
    icons: List<Color> = emptyList()
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (icons.isNotEmpty()) {
                Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    icons.forEach { dotColor ->
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(dotColor.copy(alpha = 0.2f), CircleShape)
                                .background(dotColor, CircleShape) // Simplified dots
                                .padding(2.dp)
                        )
                    }
                }
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = String.format(Locale.getDefault(), "R$ %.2f", value),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(status, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@Composable
fun DashboardProgressMetric(
    label: String,
    percentage: Float,
    status: String,
    color: Color
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(14.dp).background(color, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                LinearProgressIndicator(
                    progress = { percentage.coerceIn(0f, 1f) },
                    modifier = Modifier.width(100.dp).height(8.dp),
                    color = color,
                    trackColor = color.copy(alpha = 0.1f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${(percentage * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(status, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}
