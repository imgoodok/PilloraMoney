package com.example.pilloramoney.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.pilloramoney.R
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pilloramoney.data.model.Transaction
import com.example.pilloramoney.data.model.TransactionType
import com.example.pilloramoney.ui.components.RepetitionDropdown
import com.example.pilloramoney.ui.theme.*
import com.example.pilloramoney.ui.viewmodels.AddTransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddTransactionScreen(
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var description by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    var dateStr by remember { mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())) }
    var repetition by remember { mutableStateOf("Apenas uma vez") }
    var numRepetitions by remember { mutableStateOf("1") }
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.add_tx_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            ScrollableTabRow(
                selectedTabIndex = uiState.selectedType.ordinal,
                containerColor = Color.Transparent,
                divider = {},
                indicator = {},
                edgePadding = 0.dp
            ) {
                TransactionType.entries.forEach { type ->
                    val isSelected = uiState.selectedType == type
                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.setType(type) },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                RoundedCornerShape(12.dp)
                            )
                            .height(40.dp),
                        text = {
                            Text(
                                text = type.name.lowercase().capitalize(),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.add_tx_description_label)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                label = { Text(stringResource(R.string.add_tx_value_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = dateStr,
                onValueChange = { dateStr = it },
                label = { Text(stringResource(R.string.add_tx_date_label)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(stringResource(R.string.add_tx_repetition), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            RepetitionDropdown(repetition) { repetition = it }

            if (repetition.contains("N meses") || repetition.contains("N dias")) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = numRepetitions,
                    onValueChange = { numRepetitions = it },
                    label = { Text(if (repetition.contains("meses")) stringResource(R.string.add_tx_num_months) else stringResource(R.string.add_tx_num_days)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.saveTransaction(
                        description = description,
                        value = value.toDoubleOrNull() ?: 0.0,
                        date = Date().time,
                        repetition = repetition,
                        numRepetitions = numRepetitions.toIntOrNull() ?: 1
                    )
                    description = ""
                    value = ""
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.add_tx_save_button), fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val typeLabel = uiState.selectedType.name.lowercase().capitalize() + "s"
                Text(stringResource(R.string.add_tx_recent_title, typeLabel), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                TextButton(onClick = { showResetDialog = true }) {
                    Text(stringResource(R.string.reset), color = ErrorRed)
                }
            }
            
            uiState.lastTransactions.forEach { tx ->
                LastTransactionItem(tx, onDelete = { viewModel.deleteTransaction(it) })
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (uiState.lastTransactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.add_tx_no_records), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }

    if (showResetDialog) {
        val typeLabel = uiState.selectedType.name.lowercase().capitalize() + "s"
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(stringResource(R.string.add_tx_reset_confirm_title)) },
            text = { Text(stringResource(R.string.add_tx_reset_confirm_msg)) },
            confirmButton = {
                Button(onClick = { 
                    viewModel.deleteAll()
                    showResetDialog = false
                }, colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)) {
                    Text(stringResource(R.string.add_tx_reset_all))
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    viewModel.deleteCurrentType()
                    showResetDialog = false
                }) {
                    Text(stringResource(R.string.add_tx_reset_only_this, typeLabel))
                }
            }
        )
    }
}

@Composable
fun LastTransactionItem(transaction: Transaction, onDelete: (Transaction) -> Unit) {
    val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(transaction.date))
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.description, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(dateStr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                text = "R$ ${String.format(Locale.getDefault(), "%.2f", transaction.value)}",
                fontWeight = FontWeight.Bold,
                color = if (transaction.type == TransactionType.ENTRADA) SuccessGreen else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { onDelete(transaction) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(20.dp))
            }
        }
    }
}
