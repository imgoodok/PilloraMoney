package com.example.pilloramoney.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pilloramoney.data.local.TransactionDao
import com.example.pilloramoney.data.model.Transaction
import com.example.pilloramoney.data.model.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class SpreadsheetUiState(
    val currentMonth: Calendar = Calendar.getInstance(),
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class SpreadsheetViewModel @Inject constructor(
    private val transactionDao: TransactionDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpreadsheetUiState())
    val uiState: StateFlow<SpreadsheetUiState> = _uiState.asStateFlow()

    init {
        loadTransactions()
    }

    fun nextMonth() {
        val next = _uiState.value.currentMonth.clone() as Calendar
        next.add(Calendar.MONTH, 1)
        _uiState.update { it.copy(currentMonth = next) }
        loadTransactions()
    }

    fun previousMonth() {
        val prev = _uiState.value.currentMonth.clone() as Calendar
        prev.add(Calendar.MONTH, -1)
        _uiState.update { it.copy(currentMonth = prev) }
        loadTransactions()
    }

    private fun loadTransactions() {
        val calendar = _uiState.value.currentMonth
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        val start = calendar.timeInMillis
        
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        val end = calendar.timeInMillis

        viewModelScope.launch {
            transactionDao.getTransactionsInRange(start, end).collect { list ->
                _uiState.update { it.copy(transactions = list) }
            }
        }
    }

    fun addTransaction(
        day: Int,
        type: TransactionType,
        value: Double,
        description: String,
        replicateFuture: Boolean
    ) {
        viewModelScope.launch {
            val calendar = _uiState.value.currentMonth.clone() as Calendar
            calendar.set(Calendar.DAY_OF_MONTH, day)
            
            val transaction = Transaction(
                date = calendar.timeInMillis,
                type = type,
                value = value,
                description = description,
                isRecurring = replicateFuture,
                dayOfMonth = if (replicateFuture) day else null
            )
            transactionDao.insertTransaction(transaction)
            
            if (replicateFuture) {
                // In a real app, we might handle this with a worker or a more complex sync
                // For now, we'll just mark it as recurring in the DB
            }
        }
    }
}
