package com.example.pilloramoney.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
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
import com.example.pilloramoney.data.model.Transaction
import com.example.pilloramoney.data.model.TransactionType
import com.example.pilloramoney.ui.viewmodels.SpreadsheetViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SpreadsheetScreen(
    viewModel: SpreadsheetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf(1) }
    var selectedType by remember { mutableStateOf(TransactionType.SAIDA) }

    val calendar = uiState.currentMonth
    val monthName = SimpleDateFormat("MMMM yyyy", Locale("pt", "BR"))
        .format(calendar.time)
        .replaceFirstChar { it.uppercase() }

    Column(modifier = Modifier.fillMaxSize()) {
        // Month Navigation Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.previousMonth() }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Mês anterior")
            }
            Text(
                text = monthName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { viewModel.nextMonth() }) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Próximo mês")
            }
        }

        // Spreadsheet Table
        SpreadsheetTable(
            transactions = uiState.transactions,
            daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH),
            onCellClick = { day, type ->
                selectedDay = day
                selectedType = type
                showAddDialog = true
            }
        )
    }

    if (showAddDialog) {
        AddTransactionDialog(
            initialDay = selectedDay,
            initialType = selectedType,
            onDismiss = { showAddDialog = false },
            onConfirm = { valValue, desc, replicate ->
                viewModel.addTransaction(selectedDay, selectedType, valValue, desc, replicate)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun SpreadsheetTable(
    transactions: List<Transaction>,
    daysInMonth: Int,
    onCellClick: (Int, TransactionType) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Table Header (Horizontal Scrollable)
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(vertical = 8.dp)
        ) {
            TableCell("Dia", weight = 0.5f, isHeader = true)
            TableCell("Entrada", isHeader = true)
            TableCell("Saída", isHeader = true)
            TableCell("Diário", isHeader = true)
            TableCell("Cartão", isHeader = true)
            TableCell("Economia", isHeader = true)
            TableCell("Saldo", isHeader = true)
        }

        // Table Body
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items((1..daysInMonth).toList()) { day ->
                val dayTransactions = transactions.filter {
                    val cal = Calendar.getInstance().apply { timeInMillis = it.date }
                    cal.get(Calendar.DAY_OF_MONTH) == day
                }

                Row(
                    modifier = Modifier
                        .horizontalScroll(scrollState)
                        .padding(vertical = 4.dp)
                ) {
                    val entrada = dayTransactions.filter { it.type == TransactionType.ENTRADA }.sumOf { it.value }
                    val saida = dayTransactions.filter { it.type == TransactionType.SAIDA }.sumOf { it.value }
                    val diario = dayTransactions.filter { it.type == TransactionType.DIARIO }.sumOf { it.value }
                    val cartao = dayTransactions.filter { it.type == TransactionType.CARTAO }.sumOf { it.value }
                    val economia = dayTransactions.filter { it.type == TransactionType.ECONOMIA }.sumOf { it.value }
                    val saldo = entrada - saida - diario - cartao - economia

                    TableCell(day.toString(), weight = 0.5f)
                    TableCell(formatValue(entrada), color = if (entrada > 0) Color(0xFF2E7D32) else Color.Unspecified, onClick = { onCellClick(day, TransactionType.ENTRADA) })
                    TableCell(formatValue(saida), color = if (saida > 0) Color(0xFFC62828) else Color.Unspecified, onClick = { onCellClick(day, TransactionType.SAIDA) })
                    TableCell(formatValue(diario), onClick = { onCellClick(day, TransactionType.DIARIO) })
                    TableCell(formatValue(cartao), onClick = { onCellClick(day, TransactionType.CARTAO) })
                    TableCell(formatValue(economia), onClick = { onCellClick(day, TransactionType.ECONOMIA) })
                    TableCell(
                        text = formatValue(saldo),
                        color = getSaldoColor(saldo),
                        fontWeight = FontWeight.Bold
                    )
                }
                HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun TableCell(
    text: String,
    weight: Float = 1f,
    isHeader: Boolean = false,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null,
    onClick: (() -> Unit)? = null
) {
    Text(
        text = text,
        modifier = Modifier
            .width(80.dp) // Fixed width for columns in horizontal scroll
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(4.dp),
        fontWeight = if (isHeader) FontWeight.Bold else fontWeight,
        textAlign = TextAlign.Center,
        fontSize = if (isHeader) 14.sp else 13.sp,
        color = color,
        maxLines = 1
    )
}

@Composable
fun AddTransactionDialog(
    initialDay: Int,
    initialType: TransactionType,
    onDismiss: () -> Unit,
    onConfirm: (Double, String, Boolean) -> Unit
) {
    var value by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var replicate by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adicionar ${initialType.name} - Dia $initialDay") },
        text = {
            Column {
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("Valor") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = replicate, onCheckedChange = { replicate = it })
                    Text("Replicar para meses futuros")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val v = value.toDoubleOrNull() ?: 0.0
                onConfirm(v, description, replicate)
            }) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}


fun formatValue(value: Double): String {
    return if (value == 0.0) "-" else String.format("%.2f", value)
}

fun getSaldoColor(saldo: Double): Color {
    return when {
        saldo <= -1000 -> Color(0xFFB71C1C) // Vermelho forte
        saldo < 0 -> Color(0xFFEF5350)     // Vermelho fraco
        saldo == 0.0 -> Color.Unspecified   // Neutro
        saldo <= 1000 -> Color(0xFF81C784)  // Verde claro
        else -> Color(0xFF2E7D32)           // Verde forte
    }
}
