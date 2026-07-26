package com.example.pilloramoney.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
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
import com.example.pilloramoney.ui.viewmodels.BalanceHorizonViewModel
import com.example.pilloramoney.ui.viewmodels.MonthProjection
import java.util.*

@Composable
fun BalanceHorizonScreen(
    viewModel: BalanceHorizonViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Horizonte de Saldo",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Projeção dos próximos 12 meses",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Summary Statistics (Horizontal Cards)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HorizonStatCard("Saldo Final", uiState.projections.lastOrNull()?.finalBalance ?: 0.0, Icons.AutoMirrored.Filled.TrendingUp, SuccessGreen)
            HorizonStatCard("Melhor Mês", uiState.bestMonthValue, Icons.Default.Star, SuccessGreen)
            HorizonStatCard("Pior Saldo", uiState.worstBalance, Icons.AutoMirrored.Filled.TrendingDown, ErrorRed)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Month List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.projections) { projection ->
                MonthProjectionCard(projection)
            }
        }
    }
}

@Composable
fun HorizonStatCard(label: String, value: Double, icon: ImageVector, color: Color) {
    Surface(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "R$ ${String.format(Locale.getDefault(), "%.2f", value)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (value < 0) ErrorRed else SuccessGreen
            )
        }
    }
}

@Composable
fun MonthProjectionCard(projection: MonthProjection) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Month Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = projection.monthName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (projection.finalBalance < 0) ErrorRed.copy(alpha = 0.1f) else SuccessGreen.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "Saldo: R$ ${String.format(Locale.getDefault(), "%.2f", projection.finalBalance)}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (projection.finalBalance < 0) ErrorRed else SuccessGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            // Details Grid
            Row(modifier = Modifier.fillMaxWidth()) {
                DetailItem("Entradas", projection.entries, SuccessGreen, Modifier.weight(1f))
                DetailItem("Saídas", projection.expenses + projection.dailyExpenses, ErrorRed, Modifier.weight(1f))
                DetailItem("Cartão", projection.cards, WarningOrange, Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                DetailItem("Economias", projection.savings, SuccessGreen, Modifier.weight(1f))
                DetailItem("Min. Mês", projection.minBalanceOfMonth, if (projection.minBalanceOfMonth < 0) ErrorRed else SuccessGreen, Modifier.weight(1f))
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: Double, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = "R$ ${String.format(Locale.getDefault(), "%.2f", value)}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
