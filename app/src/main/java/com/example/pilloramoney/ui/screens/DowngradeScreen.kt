package com.example.pilloramoney.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pilloramoney.data.model.SubscriptionStatus
import com.example.pilloramoney.ui.viewmodels.SubscriptionViewModel

@Composable
fun DowngradeScreen(
    viewModel: SubscriptionViewModel,
    onNavigateToSubscription: () -> Unit,
    onContinueFree: () -> Unit
) {
    val subscription by viewModel.subscriptionStatus.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(80.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Sua assinatura expirou!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Seus dados não estão mais sendo sincronizados na nuvem. " +
                   "Você tem um prazo de 7 dias para renovar sua assinatura sem perder o acesso aos dados salvos no banco de dados.",
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (subscription.isGracePeriodActive()) {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Período de carência ativo: Renove agora para manter a sincronização.",
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Text(
                text = "Após 7 dias sem pagamento, os novos dados serão salvos apenas localmente.",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onNavigateToSubscription,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Renovar Assinatura")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onContinueFree,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continuar com Plano Gratuito", color = MaterialTheme.colorScheme.secondary)
        }
    }
}
