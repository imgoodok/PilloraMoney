package com.example.pilloramoney.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pilloramoney.data.local.GoalDao
import com.example.pilloramoney.data.local.TransactionDao
import com.example.pilloramoney.data.model.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class HomeUiState(
    val selectedMonth: Calendar = Calendar.getInstance(),
    val totalBalance: Double = 0.0,
    val monthEntries: Double = 0.0,
    val monthExpenses: Double = 0.0,
    val monthSavings: Double = 0.0,
    val costOfLiving: Double = 0.0,
    val cardExpenses: Double = 0.0,
    val savingsPercentage: Float = 0.0f,
    val dailyAverageReal: Double = 0.0,
    val dailyAveragePlanned: Double = 0.0,
    val performanceStatus: String = "Neutro",
    val savingsGoal: Double = 0.0,
    val totalSavingsAccumulated: Double = 0.0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionDao: TransactionDao,
    private val goalDao: GoalDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    fun nextMonth() {
        val next = _uiState.value.selectedMonth.clone() as Calendar
        next.add(Calendar.MONTH, 1)
        _uiState.update { it.copy(selectedMonth = next) }
        observeData()
    }

    fun previousMonth() {
        val prev = _uiState.value.selectedMonth.clone() as Calendar
        prev.add(Calendar.MONTH, -1)
        _uiState.update { it.copy(selectedMonth = prev) }
        observeData()
    }

    private fun observeData() {
        val calendar = _uiState.value.selectedMonth.clone() as Calendar
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val isCurrentMonth = isSameMonth(calendar, Calendar.getInstance())
        val currentDay = if (isCurrentMonth) Calendar.getInstance().get(Calendar.DAY_OF_MONTH) else daysInMonth

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        val start = calendar.timeInMillis
        calendar.set(Calendar.DAY_OF_MONTH, daysInMonth)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        val end = calendar.timeInMillis

        val transactionsFlow = transactionDao.getTransactionsInRange(start, end)
        val goalFlow = goalDao.getSavingsGoal()
        val allTimeSavingsFlow = transactionDao.getTotalSavingsSum().map { it ?: 0.0 }

        combine(transactionsFlow, goalFlow, allTimeSavingsFlow) { transactions, goalObj, totalAccumulated ->
            val entries = transactions.filter { it.type == TransactionType.ENTRADA }.sumOf { it.value }
            val savings = transactions.filter { it.type == TransactionType.ECONOMIA }.sumOf { it.value }
            val card = transactions.filter { it.type == TransactionType.CARTAO }.sumOf { it.value }
            val directExpenses = transactions.filter { it.type == TransactionType.SAIDA }.sumOf { it.value }
            val dailyExpenses = transactions.filter { it.type == TransactionType.DIARIO }.sumOf { it.value }
            
            val totalExpenses = directExpenses + dailyExpenses + card
            val performance = entries - totalExpenses - savings
            
            val goal = goalObj?.targetValue ?: 0.0
            val savingsPct = if (goal > 0) (totalAccumulated / goal).toFloat() else 0.0f
            
            val plannedDaily = transactions
                .filter { it.type == TransactionType.DIARIO && it.description.contains("Calculadora") }
                .sumOf { it.value } / daysInMonth

            _uiState.value.copy(
                totalBalance = performance,
                monthEntries = entries,
                monthExpenses = totalExpenses,
                monthSavings = savings,
                costOfLiving = totalExpenses,
                cardExpenses = card,
                savingsPercentage = savingsPct,
                dailyAverageReal = if (currentDay > 0) dailyExpenses / currentDay else 0.0,
                dailyAveragePlanned = plannedDaily,
                performanceStatus = if (performance < 0) "Faltou dinheiro" else "Saldo Positivo",
                savingsGoal = goal,
                totalSavingsAccumulated = totalAccumulated
            )
        }.onEach { state ->
            _uiState.update { it.copy(
                totalBalance = state.totalBalance,
                monthEntries = state.monthEntries,
                monthExpenses = state.monthExpenses,
                monthSavings = state.monthSavings,
                costOfLiving = state.costOfLiving,
                cardExpenses = state.cardExpenses,
                savingsPercentage = state.savingsPercentage,
                dailyAverageReal = state.dailyAverageReal,
                dailyAveragePlanned = state.dailyAveragePlanned,
                performanceStatus = state.performanceStatus,
                savingsGoal = state.savingsGoal,
                totalSavingsAccumulated = state.totalSavingsAccumulated
            ) }
        }.launchIn(viewModelScope)
    }

    private fun isSameMonth(c1: Calendar, c2: Calendar): Boolean {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
    }
}
