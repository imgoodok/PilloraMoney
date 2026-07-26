package com.example.pilloramoney.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pilloramoney.data.local.TransactionDao
import com.example.pilloramoney.data.model.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

data class HomeUiState(
    val totalBalance: Double = 0.0,
    val monthEntries: Double = 0.0,
    val monthExpenses: Double = 0.0,
    val monthSavings: Double = 0.0,
    val costOfLiving: Double = 0.0,
    val savingsPercentage: Float = 0.0f,
    val dailyAverageReal: Double = 0.0,
    val dailyAveragePlanned: Double = 0.0,
    val performanceStatus: String = "Neutro"
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionDao: TransactionDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        val calendar = Calendar.getInstance()
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val start = calendar.timeInMillis
        calendar.set(Calendar.DAY_OF_MONTH, daysInMonth)
        val end = calendar.timeInMillis

        transactionDao.getTransactionsInRange(start, end).onEach { transactions ->
            val entries = transactions.filter { it.type == TransactionType.ENTRADA }.sumOf { it.value }
            val savings = transactions.filter { it.type == TransactionType.ECONOMIA }.sumOf { it.value }
            
            val directExpenses = transactions.filter { it.type == TransactionType.SAIDA }.sumOf { it.value }
            val dailyExpenses = transactions.filter { it.type == TransactionType.DIARIO }.sumOf { it.value }
            val cardExpenses = transactions.filter { it.type == TransactionType.CARTAO }.sumOf { it.value }
            
            val totalExpenses = directExpenses + dailyExpenses + cardExpenses
            val performance = entries - totalExpenses - savings
            
            val savingsPct = if (entries > 0) (savings / entries).toFloat() else 0.0f
            
            val plannedDaily = transactions
                .filter { it.type == TransactionType.DIARIO && it.description.contains("Calculadora") }
                .sumOf { it.value } / daysInMonth

            _uiState.update { 
                it.copy(
                    totalBalance = performance,
                    monthEntries = entries,
                    monthExpenses = totalExpenses,
                    monthSavings = savings,
                    costOfLiving = totalExpenses,
                    savingsPercentage = savingsPct,
                    dailyAverageReal = if (currentDay > 0) dailyExpenses / currentDay else 0.0,
                    dailyAveragePlanned = plannedDaily,
                    performanceStatus = if (performance < 0) "Faltou dinheiro" else "Saldo Positivo"
                )
            }
        }.launchIn(viewModelScope)
    }
}
