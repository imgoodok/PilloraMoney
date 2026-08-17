package com.example.pilloramoney.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pilloramoney.data.local.CalculatorDao
import com.example.pilloramoney.data.model.CalculatorItem
import com.example.pilloramoney.data.model.Frequency
import com.example.pilloramoney.data.repository.AuthRepository
import com.example.pilloramoney.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CalculatorUiState(
    val items: List<CalculatorItem> = emptyList(),
    val dailyAverage: Double = 0.0,
    val monthlyEquivalent: Double = 0.0
)

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val calculatorDao: CalculatorDao,
    private val transactionRepository: TransactionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val currentUserId: String
        get() = authRepository.currentUser?.uid ?: "ANONYMOUS"

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        val userId = currentUserId
        viewModelScope.launch {
            calculatorDao.getAllItems(userId).collect { list ->
                val daily = list.sumOf { it.dailyValue }
                _uiState.update { 
                    it.copy(
                        items = list, 
                        dailyAverage = daily,
                        monthlyEquivalent = daily * 30.0
                    ) 
                }
            }
        }
    }

    fun addItem(name: String, value: Double, frequency: Frequency) {
        val userId = currentUserId
        viewModelScope.launch {
            calculatorDao.insertItem(CalculatorItem(userId = userId, name = name, value = value, frequency = frequency))
        }
    }

    fun deleteItem(item: CalculatorItem) {
        viewModelScope.launch {
            calculatorDao.deleteItem(item)
        }
    }

    fun applyToProjection() {
        viewModelScope.launch {
            transactionRepository.applyCalculatorValueToProjection(_uiState.value.dailyAverage)
        }
    }

    fun clearProjection() {
        viewModelScope.launch {
            transactionRepository.clearCalculatorProjection()
        }
    }
}
