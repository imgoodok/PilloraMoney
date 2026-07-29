package com.example.pilloramoney.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pilloramoney.ui.theme.*
import com.example.pilloramoney.ui.viewmodels.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onOpenDrawer: () -> Unit,
    onNavigateToSavings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var isPerformanceExpanded by remember { mutableStateOf(false) }
    var isCustoVidaExpanded by remember { mutableStateOf(false) }
    var isDiarioExpanded by remember { mutableStateOf(false) }

    val monthName = remember(uiState.selectedMonth) {
        SimpleDateFormat("MMMM yyyy", Locale("pt", "BR"))
            .format(uiState.selectedMonth.time)
            .replaceFirstChar { it.uppercase() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Bar Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Circular Drawer Button (TOP LEFT)
            Surface(
                modifier = Modifier.size(40.dp).clickable { onOpenDrawer() },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onSurface)
                }
            }

            // Month Selector (Centered)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.previousMonth() }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = TextSecondary)
                }
                Text(
                    text = monthName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                IconButton(onClick = { viewModel.nextMonth() }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
                }
            }

            // Empty space to balance
            Spacer(modifier = Modifier.width(40.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            "Totais!", 
            style = MaterialTheme.typography.headlineMedium, 
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            "Cálculos do mês", 
            style = MaterialTheme.typography.labelMedium, 
            color = TextSecondary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        // 1. Performance (EXPANDABLE)
        DashboardCard(
            onClick = { isPerformanceExpanded = !isPerformanceExpanded }
        ) {
            DashboardMetricRow(
                label = "Performance",
                value = uiState.totalBalance,
                status = uiState.performanceStatus,
                color = if (uiState.totalBalance < 0) ErrorRed else SuccessGreen,
                icons = listOf(SuccessGreen, ErrorRed, Color.Magenta, PrimaryBlue, WarningOrange)
            )
            
            AnimatedVisibility(visible = isPerformanceExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ExpandedMetricItem("Entradas", uiState.monthEntries, SuccessGreen, dotColor = SuccessGreen)
                    ExpandedMetricItem("Saídas", uiState.monthExpenses - uiState.cardExpenses - (uiState.dailyAverageReal * Calendar.getInstance().get(Calendar.DAY_OF_MONTH)), ErrorRed, dotColor = ErrorRed)
                    ExpandedMetricItem("Diário Médio", uiState.dailyAverageReal * Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Color.Magenta, dotColor = Color.Magenta)
                    ExpandedMetricItem("Economizado", uiState.monthSavings, PrimaryBlue, dotColor = PrimaryBlue)
                    ExpandedMetricItem("Cartão", uiState.cardExpenses, WarningOrange, dotColor = WarningOrange)
                    
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    ExpandedMetricItem("Saldo Final", uiState.totalBalance, if (uiState.totalBalance < 0) ErrorRed else SuccessGreen, isBold = true)
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)

        // 2. Economizado (CLICKABLE)
        DashboardCard(
            onClick = { onNavigateToSavings() }
        ) {
            DashboardProgressMetric(
                label = "Economizado",
                percentage = uiState.savingsPercentage,
                status = if (uiState.totalSavingsAccumulated >= uiState.savingsGoal && uiState.savingsGoal > 0) "Meta batida!" else "Guardando...",
                color = PrimaryBlue,
                currentValue = uiState.totalSavingsAccumulated,
                goalValue = uiState.savingsGoal
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)

        // 3. Custo de vida (EXPANDABLE)
        DashboardCard(
            onClick = { isCustoVidaExpanded = !isCustoVidaExpanded }
        ) {
            DashboardMetricRow(
                label = "Custo de vida",
                value = uiState.costOfLiving,
                status = if (uiState.costOfLiving > uiState.monthEntries && uiState.monthEntries > 0) "Acima da renda" else "Dentro do esperado",
                color = if (uiState.costOfLiving > uiState.monthEntries && uiState.monthEntries > 0) ErrorRed else MaterialTheme.colorScheme.onSurface,
                icons = listOf(ErrorRed, Color.Magenta, WarningOrange)
            )
            
            AnimatedVisibility(visible = isCustoVidaExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ExpandedMetricItem("Contas/Saídas", uiState.monthExpenses - uiState.cardExpenses - (uiState.dailyAverageReal * Calendar.getInstance().get(Calendar.DAY_OF_MONTH)), ErrorRed, dotColor = ErrorRed)
                    ExpandedMetricItem("Gastos Diários", uiState.dailyAverageReal * Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Color.Magenta, dotColor = Color.Magenta)
                    ExpandedMetricItem("Faturas Cartão", uiState.cardExpenses, WarningOrange, dotColor = WarningOrange)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    ExpandedMetricItem("Total Custo de Vida", uiState.costOfLiving, ErrorRed, isBold = true)
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)

        // 4. Diário médio (EXPANDABLE)
        DashboardCard(
            onClick = { isDiarioExpanded = !isDiarioExpanded }
        ) {
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
            
            AnimatedVisibility(visible = isDiarioExpanded) {
                val diff = uiState.dailyAverageReal - uiState.dailyAveragePlanned
                val locale = Locale.getDefault()
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExpandedMetricItem("Média Planejada", uiState.dailyAveragePlanned, MaterialTheme.colorScheme.onSurface)
                    ExpandedMetricItem("Média Real", uiState.dailyAverageReal, if (diff > 0) ErrorRed else SuccessGreen)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Diferença", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text(
                            text = if (diff > 0) "R$ ${String.format(locale, "%.2f", diff)} acima" else "R$ ${String.format(locale, "%.2f", -diff)} abaixo",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (diff > 0) ErrorRed else SuccessGreen
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun DashboardCard(onClick: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(modifier = Modifier.padding(horizontal = 4.dp), content = content)
    }
}

@Composable
fun ExpandedMetricItem(label: String, value: Double, color: Color, dotColor: Color? = null, isBold: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (dotColor != null) {
                Box(modifier = Modifier.size(10.dp).background(dotColor, CircleShape))
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        Text(
            text = String.format(Locale.getDefault(), "R$ %.2f", value),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = color
        )
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
                                .background(dotColor, CircleShape)
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
    color: Color,
    currentValue: Double,
    goalValue: Double
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
            Text(
                text = "R$ ${String.format(Locale.getDefault(), "%.2f", currentValue)} de ${String.format(Locale.getDefault(), "%.2f", goalValue)}",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )
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
