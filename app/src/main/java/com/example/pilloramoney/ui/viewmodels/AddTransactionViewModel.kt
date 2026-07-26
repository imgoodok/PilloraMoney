package com.example.pilloramoney.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pilloramoney.data.local.TransactionDao
import com.example.pilloramoney.data.model.Transaction
import com.example.pilloramoney.data.model.TransactionType
import com.example.pilloramoney.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddTransactionUiState(
    val lastTransactions: List<Transaction> = emptyList(),
    val selectedType: TransactionType = TransactionType.ENTRADA
)

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionDao: TransactionDao,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            transactionDao.getLatestTransactions().collect { list ->
                _uiState.update { it.copy(lastTransactions = list) }
            }
        }
    }

    fun setType(type: TransactionType) {
        _uiState.update { it.copy(selectedType = type) }
    }

    fun saveTransaction(
        description: String,
        value: Double,
        date: Long,
        repetition: String,
        numRepetitions: Int = 1
    ) {
        viewModelScope.launch {
            transactionRepository.saveTransactionWithRepetition(
                description = description,
                value = value,
                date = date,
                type = _uiState.value.selectedType,
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

    fun deleteAll() {
        viewModelScope.launch {
            transactionDao.deleteAll()
        }
    }
}
