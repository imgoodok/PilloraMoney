package com.example.pilloramoney.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pilloramoney.data.local.MonthlyBalanceDao
import com.example.pilloramoney.data.local.TransactionDao
import com.example.pilloramoney.data.model.Transaction
import com.example.pilloramoney.data.model.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class MonthProjection(
    val monthName: String,
    val initialBalance: Double,
    val entries: Double,
    val expenses: Double,
    val dailyExpenses: Double,
    val cards: Double,
    val savings: Double,
    val finalBalance: Double,
    val minBalanceOfMonth: Double
)

data class HorizonUiState(
    val projections: List<MonthProjection> = emptyList(),
    val bestMonth: String = "",
    val bestMonthValue: Double = 0.0,
    val worstBalance: Double = 0.0,
    val firstNegativeMonth: String? = null
)

@HiltViewModel
class BalanceHorizonViewModel @Inject constructor(
    private val transactionDao: TransactionDao,
    private val monthlyBalanceDao: MonthlyBalanceDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(HorizonUiState())
    val uiState: StateFlow<HorizonUiState> = _uiState.asStateFlow()

    init {
        calculateProjections()
    }

    private fun calculateProjections() {
        viewModelScope.launch {
            // This is a complex calculation. We need to iterate through N months.
            // For now, let's do 12 months.
            val result = mutableListOf<MonthProjection>()
            val calendar = Calendar.getInstance()
            
            // Get current month's initial balance as starting point
            val currentMonthKey = String.format("%d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
            var runningBalance = monthlyBalanceDao.getBalanceForMonth(currentMonthKey)?.initialBalance ?: 0.0

            for (i in 0 until 12) {
                val monthName = String.format(Locale("pt", "BR"), "%s/%02d", 
                    calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale("pt", "BR")),
                    calendar.get(Calendar.YEAR) % 100
                ).uppercase()

                // Calculate current month's totals
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val start = calendar.timeInMillis
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                val end = calendar.timeInMillis
                
                // We use a simplified blocking call or just wait for flow first item for projection logic
                // In a real app, this would be a more efficient combined query.
                val monthTxs = transactionDao.getTransactionsInRange(start, end).first()

                val ent = monthTxs.filter { it.type == TransactionType.ENTRADA }.sumOf { it.value }
                val sai = monthTxs.filter { it.type == TransactionType.SAIDA }.sumOf { it.value }
                val dia = monthTxs.filter { it.type == TransactionType.DIARIO }.sumOf { it.value }
                val car = monthTxs.filter { it.type == TransactionType.CARTAO }.sumOf { it.value }
                val eco = monthTxs.filter { it.type == TransactionType.ECONOMIA }.sumOf { it.value }

                val final = runningBalance + ent - sai - dia - car - eco
                
                result.add(MonthProjection(
                    monthName = monthName,
                    initialBalance = runningBalance,
                    entries = ent,
                    expenses = sai,
                    dailyExpenses = dia,
                    cards = car,
                    savings = eco,
                    finalBalance = final,
                    minBalanceOfMonth = Math.min(runningBalance, final) // Simplified min
                ))

                runningBalance = final
                calendar.add(Calendar.MONTH, 1)
            }

            _uiState.update { 
                it.copy(
                    projections = result,
                    bestMonth = result.maxByOrNull { it.finalBalance }?.monthName ?: "",
                    bestMonthValue = result.maxByOrNull { it.finalBalance }?.finalBalance ?: 0.0,
                    worstBalance = result.minByOrNull { it.finalBalance }?.finalBalance ?: 0.0,
                    firstNegativeMonth = result.firstOrNull { it.finalBalance < 0 }?.monthName
                )
            }
        }
    }
}
