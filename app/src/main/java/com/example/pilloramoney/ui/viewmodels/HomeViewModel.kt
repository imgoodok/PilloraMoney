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
    val monthSavings: Double = 0.0
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
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val start = calendar.timeInMillis
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val end = calendar.timeInMillis

        transactionDao.getTransactionsInRange(start, end).onEach { transactions ->
            val entries = transactions.filter { it.type == TransactionType.ENTRADA }.sumOf { it.value }
            val expenses = transactions.filter { 
                it.type == TransactionType.SAIDA || 
                it.type == TransactionType.DIARIO || 
                it.type == TransactionType.CARTAO 
            }.sumOf { it.value }
            val savings = transactions.filter { it.type == TransactionType.ECONOMIA }.sumOf { it.value }
            
            _uiState.update { 
                it.copy(
                    totalBalance = entries - expenses - savings,
                    monthEntries = entries,
                    monthExpenses = expenses,
                    monthSavings = savings
                )
            }
        }.launchIn(viewModelScope)
    }
}
