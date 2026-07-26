package com.example.pilloramoney.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pilloramoney.data.model.Transaction
import com.example.pilloramoney.data.model.TransactionType
import com.example.pilloramoney.ui.components.RepetitionDropdown
import com.example.pilloramoney.ui.theme.*
import com.example.pilloramoney.ui.viewmodels.SpreadsheetViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SpreadsheetScreen(
    viewModel: SpreadsheetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showBalanceDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf(1) }
    var selectedType by remember { mutableStateOf(TransactionType.SAIDA) }

    val calendar = uiState.currentMonth
    val monthName = SimpleDateFormat("MMMM yyyy", Locale("pt", "BR"))
        .format(calendar.time)
        .replaceFirstChar { it.uppercase() }

    val horizontalScrollState = rememberScrollState()

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 2.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = monthName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.clickable { showBalanceDialog = true },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Saldo Inicial: ",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "R$ ${String.format(Locale.getDefault(), "%.2f", uiState.initialBalance)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.initialBalance < 0) ErrorRed else SuccessGreen
                                )
                            }
                        }
                        Row {
                            IconButton(onClick = { viewModel.previousMonth() }, modifier = Modifier.size(40.dp)) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = null)
                            }
                            IconButton(onClick = { viewModel.nextMonth() }, modifier = Modifier.size(40.dp)) {
                                Icon(Icons.Default.ChevronRight, contentDescription = null)
                            }
                        }
                    }
                    
                    CompactSpreadsheetHeader(horizontalScrollState)
                }
            }
        }
    ) { innerPadding ->
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        var cumulativeBalance = uiState.initialBalance

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items((1..daysInMonth).toList()) { day ->
                val dayTransactions = uiState.transactions.filter {
                    val cal = Calendar.getInstance().apply { timeInMillis = it.date }
                    cal.get(Calendar.DAY_OF_MONTH) == day
                }
                
                val dayIn = dayTransactions.filter { it.type == TransactionType.ENTRADA }.sumOf { it.value }
                val dayOut = dayTransactions.filter { it.type != TransactionType.ENTRADA }.sumOf { it.value }
                cumulativeBalance += (dayIn - dayOut)

                CompactSpreadsheetRow(
                    day = day,
                    transactions = dayTransactions,
                    dailyBalance = cumulativeBalance,
                    scrollState = horizontalScrollState,
                    onCellClick = { type ->
                        selectedDay = day
                        selectedType = type
                        showAddDialog = true
                    }
                )
                HorizontalDivider(
                    thickness = 0.5.dp, 
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
            }
        }
    }

    if (showBalanceDialog) {
        InitialBalanceDialog(
            currentValue = uiState.initialBalance,
            onDismiss = { showBalanceDialog = false },
            onConfirm = {
                viewModel.updateInitialBalance(it)
                showBalanceDialog = false
            }
        )
    }

    if (showAddDialog) {
        AddTransactionDialog(
            initialDay = selectedDay,
            initialType = selectedType,
            onDismiss = { showAddDialog = false },
            onConfirm = { value, desc, repetition, numRep ->
                viewModel.addTransaction(selectedDay, selectedType, value, desc, repetition, numRep)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun CompactSpreadsheetHeader(scrollState: ScrollState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "DIA",
            modifier = Modifier.width(40.dp).padding(start = 8.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(scrollState)
        ) {
            HeaderCell("ENTRADA")
            HeaderCell("SAÍDA")
            HeaderCell("DIÁRIO")
            HeaderCell("CARTÃO")
            HeaderCell("ECONOMIA")
        }
        
        Text(
            "SALDO",
            modifier = Modifier.width(85.dp).padding(end = 8.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun HeaderCell(text: String) {
    Text(
        text = text,
        modifier = Modifier.width(80.dp),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
}

@Composable
fun CompactSpreadsheetRow(
    day: Int,
    transactions: List<Transaction>,
    dailyBalance: Double,
    scrollState: ScrollState,
    onCellClick: (TransactionType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.width(40.dp).fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = String.format(Locale.getDefault(), "%02d", day),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .horizontalScroll(scrollState),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DataCell(transactions.filter { it.type == TransactionType.ENTRADA }, SuccessGreen) { onCellClick(TransactionType.ENTRADA) }
            DataCell(transactions.filter { it.type == TransactionType.SAIDA }, ErrorRed) { onCellClick(TransactionType.SAIDA) }
            DataCell(transactions.filter { it.type == TransactionType.DIARIO }, ErrorRed) { onCellClick(TransactionType.DIARIO) }
            DataCell(transactions.filter { it.type == TransactionType.CARTAO }, WarningOrange) { onCellClick(TransactionType.CARTAO) }
            DataCell(transactions.filter { it.type == TransactionType.ECONOMIA }, SuccessGreen) { onCellClick(TransactionType.ECONOMIA) }
        }

        Box(
            modifier = Modifier.width(85.dp).padding(end = 8.dp).fillMaxHeight(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (dailyBalance < 0) ErrorRed.copy(alpha = 0.1f) else SuccessGreen.copy(alpha = 0.1f)
            ) {
                Text(
                    text = String.format(Locale.getDefault(), "%.2f", dailyBalance),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (dailyBalance < 0) ErrorRed else SuccessGreen,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun DataCell(
    items: List<Transaction>,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(80.dp)
            .fillMaxHeight()
            .clickable { onClick() }
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (items.isEmpty()) {
            Text("+", color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), fontSize = 14.sp)
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val total = items.sumOf { it.value }
                Text(
                    text = String.format(Locale.getDefault(), "%.2f", total),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = color
                )
                if (items.size > 1) {
                    Text(
                        text = "${items.size} itens",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun InitialBalanceDialog(
    currentValue: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var value by remember { mutableStateOf(currentValue.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajustar Saldo Inicial") },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                label = { Text("Valor (R$)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(value.toDoubleOrNull() ?: 0.0) }) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun AddTransactionDialog(
    initialDay: Int,
    initialType: TransactionType,
    onDismiss: () -> Unit,
    onConfirm: (Double, String, String, Int) -> Unit
) {
    var value by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var repetition by remember { mutableStateOf("Apenas uma vez") }
    var numRepetitions by remember { mutableStateOf("1") }

    val typeName = when(initialType) {
        TransactionType.ENTRADA -> "Entrada"
        TransactionType.SAIDA -> "Saída"
        TransactionType.DIARIO -> "Gasto Diário"
        TransactionType.CARTAO -> "Cartão"
        TransactionType.ECONOMIA -> "Economia"
        else -> "Lançamento"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo(a) $typeName - Dia $initialDay") },
        text = {
            Column {
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("Valor") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Repetição", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                RepetitionDropdown(repetition) { repetition = it }
                
                if (repetition.contains("N meses") || repetition.contains("N dias")) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = numRepetitions,
                        onValueChange = { numRepetitions = it },
                        label = { Text(if (repetition.contains("meses")) "Quantidade de meses" else "Quantidade de dias") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val v = value.toDoubleOrNull() ?: 0.0
                val n = numRepetitions.toIntOrNull() ?: 1
                onConfirm(v, description, repetition, n)
            }) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
