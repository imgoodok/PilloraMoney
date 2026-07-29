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

data class DayBalance(
    val day: Int,
    val balance: Double
)

data class MonthProjectionGrid(
    val monthName: String,
    val days: List<DayBalance>
)

data class HorizonUiState(
    val months: List<MonthProjectionGrid> = emptyList()
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

    fun calculateProjections() {
        viewModelScope.launch {
            val monthsToProject = 12
            val result = mutableListOf<MonthProjectionGrid>()
            val calendar = Calendar.getInstance()
            
            val currentMonthKey = String.format("%d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
            var runningBalance = monthlyBalanceDao.getBalanceForMonth(currentMonthKey)?.initialBalance ?: 0.0

            for (m in 0 until monthsToProject) {
                val monthName = String.format(Locale("pt", "BR"), "%s/%02d", 
                    calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale("pt", "BR")),
                    calendar.get(Calendar.YEAR) % 100
                ).replaceFirstChar { it.uppercase() }

                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val start = calendar.timeInMillis
                val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                calendar.set(Calendar.DAY_OF_MONTH, maxDay)
                val end = calendar.timeInMillis
                
                val monthTxs = transactionDao.getTransactionsInRange(start, end).first()
                val dayBalances = mutableListOf<DayBalance>()

                for (d in 1..maxDay) {
                    val dayTxs = monthTxs.filter {
                        val cal = Calendar.getInstance().apply { timeInMillis = it.date }
                        cal.get(Calendar.DAY_OF_MONTH) == d
                    }
                    
                    val dayIn = dayTxs.filter { it.type == TransactionType.ENTRADA }.sumOf { it.value }
                    val dayOut = dayTxs.filter { 
                        it.type == TransactionType.SAIDA || 
                        it.type == TransactionType.DIARIO || 
                        it.type == TransactionType.ECONOMIA 
                    }.sumOf { it.value }
                    
                    runningBalance += (dayIn - dayOut)
                    dayBalances.add(DayBalance(d, runningBalance))
                }
                
                result.add(MonthProjectionGrid(monthName, dayBalances))
                calendar.add(Calendar.MONTH, 1)
            }

            _uiState.update { it.copy(months = result) }
        }
    }
}
