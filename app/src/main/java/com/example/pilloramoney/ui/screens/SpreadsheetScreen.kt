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
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SpreadsheetScreen(
    viewModel: SpreadsheetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDetailsDialog by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf(1) }
    var selectedType by remember { mutableStateOf(TransactionType.SAIDA) }

    val calendar = uiState.currentMonth
    val monthName = SimpleDateFormat("MMMM yyyy", Locale("pt", "BR"))
        .format(calendar.time)
        .replaceFirstChar { it.uppercase() }

    val horizontalScrollState = rememberScrollState()
    // Localized currency formatter without the "R$" symbol for table density
    val currencyFormat = remember { NumberFormat.getNumberInstance(Locale("pt", "BR")).apply { 
        minimumFractionDigits = 2 
        maximumFractionDigits = 2
    } }

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
                        Text(
                            text = monthName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
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
                
                // Logic: In - (Out + Daily + Savings). Ignore Card (Cartão) per request for balance.
                val dayIn = dayTransactions.filter { it.type == TransactionType.ENTRADA }.sumOf { it.value }
                val dayOut = dayTransactions.filter { 
                    it.type == TransactionType.SAIDA || 
                    it.type == TransactionType.DIARIO || 
                    it.type == TransactionType.ECONOMIA 
                }.sumOf { it.value }
                
                cumulativeBalance += (dayIn - dayOut)

                CompactSpreadsheetRow(
                    day = day,
                    transactions = dayTransactions,
                    dailyBalance = cumulativeBalance,
                    scrollState = horizontalScrollState,
                    currencyFormat = currencyFormat,
                    onCellClick = { type ->
                        selectedDay = day
                        selectedType = type
                        showDetailsDialog = true
                    }
                )
                HorizontalDivider(
                    thickness = 0.5.dp, 
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
            }
        }
    }

    if (showDetailsDialog) {
        val filteredItems = uiState.transactions.filter {
            val cal = Calendar.getInstance().apply { timeInMillis = it.date }
            cal.get(Calendar.DAY_OF_MONTH) == selectedDay && it.type == selectedType
        }
        
        DayDetailsDialog(
            day = selectedDay,
            type = selectedType,
            existingItems = filteredItems,
            onDismiss = { showDetailsDialog = false },
            onDelete = { viewModel.deleteTransaction(it) },
            onAdd = { valValue, desc, repetition, numRep ->
                viewModel.addTransaction(selectedDay, selectedType, valValue, desc, repetition, numRep)
                showDetailsDialog = false
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
            HeaderCell("ECONOMIA")
            HeaderCell("CARTÃO") // RE-ADDED
        }
        
        Text(
            "SALDO",
            modifier = Modifier.width(100.dp).padding(end = 8.dp),
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
        modifier = Modifier.width(90.dp),
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
    currencyFormat: NumberFormat,
    onCellClick: (TransactionType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Fixed Day
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

        // Scrollable Values
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .horizontalScroll(scrollState),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DataCell(transactions.filter { it.type == TransactionType.ENTRADA }, SuccessGreen, currencyFormat) { onCellClick(TransactionType.ENTRADA) }
            DataCell(transactions.filter { it.type == TransactionType.SAIDA }, ErrorRed, currencyFormat) { onCellClick(TransactionType.SAIDA) }
            DataCell(transactions.filter { it.type == TransactionType.DIARIO }, Color.Magenta, currencyFormat) { onCellClick(TransactionType.DIARIO) }
            DataCell(transactions.filter { it.type == TransactionType.ECONOMIA }, PrimaryBlue, currencyFormat) { onCellClick(TransactionType.ECONOMIA) }
            DataCell(transactions.filter { it.type == TransactionType.CARTAO }, WarningOrange, currencyFormat) { onCellClick(TransactionType.CARTAO) }
        }

        // Fixed Saldo
        Box(
            modifier = Modifier.width(100.dp).padding(end = 8.dp).fillMaxHeight(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (dailyBalance < 0) ErrorRed.copy(alpha = 0.1f) else SuccessGreen.copy(alpha = 0.1f)
            ) {
                Text(
                    text = currencyFormat.format(dailyBalance),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (dailyBalance < 0) ErrorRed else SuccessGreen,
                    maxLines = 1,
                    overflow = TextOverflow.Visible
                )
            }
        }
    }
}

@Composable
fun DataCell(
    items: List<Transaction>,
    color: Color,
    currencyFormat: NumberFormat,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(90.dp)
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
                    text = currencyFormat.format(total),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = color,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
fun DayDetailsDialog(
    day: Int,
    type: TransactionType,
    existingItems: List<Transaction>,
    onDismiss: () -> Unit,
    onDelete: (Transaction) -> Unit,
    onAdd: (Double, String, String, Int) -> Unit
) {
    var value by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var repetition by remember { mutableStateOf("Apenas uma vez") }
    var numRepetitions by remember { mutableStateOf("1") }
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }

    val typeName = when(type) {
        TransactionType.ENTRADA -> "Entrada"
        TransactionType.SAIDA -> "Saída"
        TransactionType.DIARIO -> "Gasto Diário"
        TransactionType.ECONOMIA -> "Economia"
        TransactionType.CARTAO -> "Cartão"
        else -> "Lançamento"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Detalhes de $typeName - Dia $day") },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                if (existingItems.isNotEmpty()) {
                    Text("Lançamentos Existentes:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    existingItems.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.description.ifEmpty { "Sem descrição" }, style = MaterialTheme.typography.bodySmall)
                                Text(currencyFormat.format(item.value), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = { onDelete(item) }) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                }

                Text("Adicionar Novo:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
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
                onAdd(v, description, repetition, n)
            }) {
                Text("Salvar Novo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Fechar") }
        }
    )
}
