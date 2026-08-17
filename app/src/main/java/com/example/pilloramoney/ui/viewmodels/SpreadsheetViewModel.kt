package com.example.pilloramoney.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pilloramoney.data.local.MonthlyBalanceDao
import com.example.pilloramoney.data.local.TransactionDao
import com.example.pilloramoney.data.model.MonthlyBalance
import com.example.pilloramoney.data.model.Transaction
import com.example.pilloramoney.data.model.TransactionType
import com.example.pilloramoney.data.repository.AuthRepository
import com.example.pilloramoney.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class SpreadsheetUiState(
    val currentMonth: Calendar = Calendar.getInstance(),
    val transactions: List<Transaction> = emptyList(),
    val initialBalance: Double = 0.0,
    val isLoading: Boolean = false
)

@HiltViewModel
class SpreadsheetViewModel @Inject constructor(
    private val transactionDao: TransactionDao,
    private val monthlyBalanceDao: MonthlyBalanceDao,
    private val transactionRepository: TransactionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val currentUserId: String
        get() = authRepository.currentUser?.uid ?: "ANONYMOUS"

    private val _uiState = MutableStateFlow(SpreadsheetUiState())
    val uiState: StateFlow<SpreadsheetUiState> = _uiState.asStateFlow()

    private val monthFormatter = SimpleDateFormat("yyyy-MM", Locale.US)

    init {
        loadData()
    }

    fun nextMonth() {
        val next = _uiState.value.currentMonth.clone() as Calendar
        next.add(Calendar.MONTH, 1)
        _uiState.update { it.copy(currentMonth = next) }
        loadData()
    }

    fun previousMonth() {
        val prev = _uiState.value.currentMonth.clone() as Calendar
        prev.add(Calendar.MONTH, -1)
        _uiState.update { it.copy(currentMonth = prev) }
        loadData()
    }

    private fun loadData() {
        val userId = currentUserId
        val calendar = _uiState.value.currentMonth
        val monthKey = monthFormatter.format(calendar.time)

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis
        
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val end = calendar.timeInMillis

        viewModelScope.launch {
            // Load Initial Balance
            val balance = monthlyBalanceDao.getBalanceForMonth(userId, monthKey)?.initialBalance ?: 0.0
            _uiState.update { it.copy(initialBalance = balance) }

            // Load Transactions
            transactionDao.getTransactionsInRange(userId, start, end).collect { list ->
                _uiState.update { it.copy(transactions = list) }
            }
        }
    }

    fun updateInitialBalance(value: Double) {
        val userId = currentUserId
        val monthKey = monthFormatter.format(_uiState.value.currentMonth.time)
        viewModelScope.launch {
            monthlyBalanceDao.upsertBalance(MonthlyBalance(userId, monthKey, value))
            _uiState.update { it.copy(initialBalance = value) }
        }
    }

    fun addTransaction(
        day: Int,
        type: TransactionType,
        value: Double,
        description: String,
        repetition: String,
        numRepetitions: Int = 1
    ) {
        viewModelScope.launch {
            val calendar = _uiState.value.currentMonth.clone() as Calendar
            calendar.set(Calendar.DAY_OF_MONTH, day)
            
            transactionRepository.saveTransactionWithRepetition(
                description = description,
                value = value,
                date = calendar.timeInMillis,
                type = type,
                repetition = repetition,
                numRepetitions = numRepetitions
            )
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionDao.deleteTransaction(transaction)
        }
    }
}
