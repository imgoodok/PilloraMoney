package com.example.pilloramoney.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pilloramoney.data.local.GoalDao
import com.example.pilloramoney.data.local.TransactionDao
import com.example.pilloramoney.data.model.FinancialGoal
import com.example.pilloramoney.data.model.Transaction
import com.example.pilloramoney.data.model.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SavingsUiState(
    val savingsHistory: List<Transaction> = emptyList(),
    val currentGoal: Double = 0.0,
    val totalSaved: Double = 0.0,
    val progressPercentage: Float = 0f
)

@HiltViewModel
class SavingsViewModel @Inject constructor(
    private val transactionDao: TransactionDao,
    private val goalDao: GoalDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavingsUiState())
    val uiState: StateFlow<SavingsUiState> = _uiState.asStateFlow()

    init {
        loadSavingsData()
    }

    private fun loadSavingsData() {
        // Load all time savings
        val savingsFlow = transactionDao.getAllSavings()

        val goalFlow = goalDao.getSavingsGoal().map { it?.targetValue ?: 0.0 }

        combine(savingsFlow, goalFlow) { history, goal ->
            val total = history.sumOf { it.value }
            SavingsUiState(
                savingsHistory = history,
                currentGoal = goal,
                totalSaved = total,
                progressPercentage = if (goal > 0) (total / goal).toFloat() else 0f
            )
        }.onEach { state ->
            _uiState.value = state
        }.launchIn(viewModelScope)
    }

    fun updateGoal(value: Double) {
        viewModelScope.launch {
            goalDao.upsertGoal(FinancialGoal(targetValue = value))
        }
    }

    fun deleteSaving(transaction: Transaction) {
        viewModelScope.launch {
            transactionDao.deleteTransaction(transaction)
        }
    }
}
