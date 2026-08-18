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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.example.pilloramoney.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    val locale = java.util.Locale.getDefault()
    var isPerformanceExpanded by remember { mutableStateOf(false) }
    var isCustoVidaExpanded by remember { mutableStateOf(false) }
    var isDiarioExpanded by remember { mutableStateOf(false) }

    val monthName = remember(uiState.selectedMonth, locale) {
        SimpleDateFormat("MMMM yyyy", locale)
            .format(uiState.selectedMonth.time)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
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
            stringResource(R.string.home_totals), 
            style = MaterialTheme.typography.headlineMedium, 
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            stringResource(R.string.home_month_calculations), 
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
                label = stringResource(R.string.home_performance),
                value = uiState.totalBalance,
                status = uiState.performanceStatus,
                color = if (uiState.totalBalance < 0) ErrorRed else SuccessGreen,
                iconsWithInitials = listOf(
                    SuccessGreen to "E",
                    ErrorRed to "S",
                    Color.Magenta to "D",
                    PrimaryOrange to "Ec",
                    WarningOrange to "C"
                )
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
                    ExpandedMetricItem(stringResource(R.string.home_entries), uiState.monthEntries, SuccessGreen, dotColor = SuccessGreen, initial = "E")
                    ExpandedMetricItem(stringResource(R.string.home_expenses), uiState.monthExpenses - uiState.cardExpenses - (uiState.dailyAverageReal * Calendar.getInstance().get(Calendar.DAY_OF_MONTH)), ErrorRed, dotColor = ErrorRed, initial = "S")
                    ExpandedMetricItem(stringResource(R.string.home_daily_average), uiState.dailyAverageReal * Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Color.Magenta, dotColor = Color.Magenta, initial = "D")
                    ExpandedMetricItem(stringResource(R.string.home_saved), uiState.monthSavings, PrimaryOrange, dotColor = PrimaryOrange, initial = "Ec")
                    ExpandedMetricItem(stringResource(R.string.home_card), uiState.cardExpenses, WarningOrange, dotColor = WarningOrange, initial = "C")
                    
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    ExpandedMetricItem(stringResource(R.string.home_final_balance), uiState.totalBalance, if (uiState.totalBalance < 0) ErrorRed else SuccessGreen, isBold = true)
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)

        // 2. Economizado (CLICKABLE)
        DashboardCard(
            onClick = { onNavigateToSavings() }
        ) {
            DashboardProgressMetric(
                label = stringResource(R.string.home_saved),
                percentage = uiState.savingsPercentage,
                status = if (uiState.totalSavingsAccumulated >= uiState.savingsGoal && uiState.savingsGoal > 0) stringResource(R.string.home_goal_met) else stringResource(R.string.home_saving),
                color = PrimaryOrange,
                currentValue = uiState.totalSavingsAccumulated,
                goalValue = uiState.savingsGoal,
                initial = "Ec"
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)

        // 3. Custo de vida (EXPANDABLE)
        DashboardCard(
            onClick = { isCustoVidaExpanded = !isCustoVidaExpanded }
        ) {
            DashboardMetricRow(
                label = stringResource(R.string.home_cost_of_living),
                value = uiState.costOfLiving,
                status = if (uiState.costOfLiving > uiState.monthEntries && uiState.monthEntries > 0) stringResource(R.string.home_above_income) else stringResource(R.string.home_within_expected),
                color = if (uiState.costOfLiving > uiState.monthEntries && uiState.monthEntries > 0) ErrorRed else MaterialTheme.colorScheme.onSurface,
                iconsWithInitials = listOf(
                    ErrorRed to "S",
                    Color.Magenta to "D",
                    WarningOrange to "C"
                )
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
                    ExpandedMetricItem(stringResource(R.string.home_bills_expenses), uiState.monthExpenses - uiState.cardExpenses - (uiState.dailyAverageReal * Calendar.getInstance().get(Calendar.DAY_OF_MONTH)), ErrorRed, dotColor = ErrorRed, initial = "S")
                    ExpandedMetricItem(stringResource(R.string.home_daily_spending), uiState.dailyAverageReal * Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Color.Magenta, dotColor = Color.Magenta, initial = "D")
                    ExpandedMetricItem(stringResource(R.string.home_card_invoices), uiState.cardExpenses, WarningOrange, dotColor = WarningOrange, initial = "C")
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    ExpandedMetricItem(stringResource(R.string.home_total_cost_of_living), uiState.costOfLiving, ErrorRed, isBold = true)
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
                    Text(stringResource(R.string.home_daily_average), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                                            Box(modifier = Modifier.size(20.dp).background(Color.Magenta, CircleShape), contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "D",
                                                    color = Color.Black,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.align(Alignment.Center)
                                                )
                                            }
                                            Text(" / O", style = MaterialTheme.typography.bodySmall, color = TextSecondary, modifier = Modifier.padding(start = 8.dp))
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
                    ExpandedMetricItem(stringResource(R.string.home_planned_average), uiState.dailyAveragePlanned, MaterialTheme.colorScheme.onSurface)
                    ExpandedMetricItem(stringResource(R.string.home_real_average), uiState.dailyAverageReal, if (diff > 0) ErrorRed else SuccessGreen)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(stringResource(R.string.home_difference), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text(
                            text = if (diff > 0) "R$ ${String.format(locale, "%.2f", diff)} ${stringResource(R.string.home_above)}" else "R$ ${String.format(locale, "%.2f", -diff)} ${stringResource(R.string.home_below)}",
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
fun ExpandedMetricItem(label: String, value: Double, color: Color, dotColor: Color? = null, initial: String? = null, isBold: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (dotColor != null) {
                            Box(modifier = Modifier.size(20.dp).background(dotColor, CircleShape), contentAlignment = Alignment.Center) {
                    if (initial != null) {
                                    Text(
                                        initial,
                                        color = Color.Black,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
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
    iconsWithInitials: List<Pair<Color, String>> = emptyList()
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (iconsWithInitials.isNotEmpty()) {
                Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    iconsWithInitials.forEach { (dotColor, initial) ->
                        Box(
                            modifier = Modifier
                                                        .size(20.dp)
                                .background(dotColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                                                    Text(
                                                        initial,
                                                        color = Color.Black,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        textAlign = TextAlign.Center,
                                                        modifier = Modifier.align(Alignment.Center)
                                                    )
                                                }
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
    goalValue: Double,
    initial: String
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(20.dp).background(color, CircleShape), contentAlignment = Alignment.Center) {
                                    Text(
                                        initial,
                                        color = Color.Black,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
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
                text = "R$ ${String.format(Locale.getDefault(), "%.2f", currentValue)} ${stringResource(R.string.home_of)} R$ ${String.format(Locale.getDefault(), "%.2f", goalValue)}",
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
