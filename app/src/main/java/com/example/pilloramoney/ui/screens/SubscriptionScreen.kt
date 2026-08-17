package com.example.pilloramoney.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pilloramoney.data.model.SubscriptionStatus
import com.example.pilloramoney.ui.viewmodels.SubscriptionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    viewModel: SubscriptionViewModel,
    onBack: () -> Unit
) {
    val subscription by viewModel.subscriptionStatus.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Planos e Assinatura") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isSyncing) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Escolha o melhor plano para você",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Tenha seus dados seguros na nuvem e livre-se dos anúncios.",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Plan Cards
            PlanCard(
                title = "Plano Gratuito",
                price = "R$ 0,00",
                isSelected = subscription.status == SubscriptionStatus.FREE,
                benefits = listOf(
                    Benefit("Planilha de Projeção", true),
                    Benefit("Calculadora Diária", true),
                    Benefit("Horizonte de Saldo", true),
                    Benefit("Informações salvas localmente", false, true),
                    Benefit("Contém Anúncios", false, true)
                ),
                onSelect = { viewModel.updateSubscription(SubscriptionStatus.FREE) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            PlanCard(
                title = "Plano Premium",
                price = "R$ 19,90 / mês",
                isSelected = subscription.status == SubscriptionStatus.PREMIUM,
                isHighlight = true,
                benefits = listOf(
                    Benefit("Tudo do Plano Gratuito", true),
                    Benefit("Dados salvos no Banco de Dados", true),
                    Benefit("Sincronização em Nuvem", true, icon = Icons.Default.CloudUpload),
                    Benefit("Sem Anúncios", true),
                    Benefit("Suporte Prioritário", true)
                ),
                onSelect = { viewModel.updateSubscription(SubscriptionStatus.PREMIUM) }
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            if (subscription.status == SubscriptionStatus.PREMIUM) {
                Button(
                    onClick = { viewModel.updateSubscription(SubscriptionStatus.EXPIRED) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Simular Expiração (Downgrade)")
                }
            }
        }
    }
}

data class Benefit(val text: String, val isPositive: Boolean, val isWarning: Boolean = false, val icon: ImageVector? = null)

@Composable
fun PlanCard(
    title: String,
    price: String,
    benefits: List<Benefit>,
    isSelected: Boolean,
    isHighlight: Boolean = false,
    onSelect: () -> Unit
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val backgroundColor = if (isHighlight) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, borderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                if (isHighlight) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "RECOMENDADO",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            
            Text(price, fontSize = 28.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
            
            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            benefits.forEach { benefit ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = benefit.icon ?: if (benefit.isPositive) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = when {
                            benefit.isWarning -> MaterialTheme.colorScheme.error
                            benefit.isPositive -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.secondary
                        },
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        benefit.text,
                        fontSize = 14.sp,
                        color = if (benefit.isWarning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onSelect,
                modifier = Modifier.fillMaxWidth(),
                colors = if (isSelected) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary) else ButtonDefaults.buttonColors(),
                enabled = !isSelected
            ) {
                Text(if (isSelected) "Plano Atual" else "Selecionar Plano")
            }
        }
    }
}
