package com.example.pilloramoney.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pilloramoney.data.local.CalculatorDao
import com.example.pilloramoney.data.model.CalculatorItem
import com.example.pilloramoney.data.model.Frequency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CalculatorUiState(
    val items: List<CalculatorItem> = emptyList(),
    val dailyAverage: Double = 0.0
)

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val calculatorDao: CalculatorDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            calculatorDao.getAllItems().collect { list ->
                val average = list.sumOf { it.dailyValue }
                _uiState.update { it.copy(items = list, dailyAverage = average) }
            }
        }
    }

    fun addItem(name: String, value: Double, frequency: Frequency) {
        viewModelScope.launch {
            calculatorDao.insertItem(CalculatorItem(name = name, value = value, frequency = frequency))
        }
    }

    fun deleteItem(item: CalculatorItem) {
        viewModelScope.launch {
            calculatorDao.deleteItem(item)
        }
    }
}
