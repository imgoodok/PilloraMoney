package com.example.pilloramoney.data.repository

import com.example.pilloramoney.data.local.TransactionDao
import com.example.pilloramoney.data.model.Transaction
import com.example.pilloramoney.data.model.TransactionType
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {
    suspend fun saveTransactionWithRepetition(
        description: String,
        value: Double,
        date: Long,
        type: TransactionType,
        repetition: String,
        numRepetitions: Int = 1
    ) {
        val transactionsToSave = mutableListOf<Transaction>()
        val calendar = Calendar.getInstance().apply { timeInMillis = date }
        
        val isDaily = repetition.contains("todos os dias") || repetition.contains("Diário")
        val isMonthly = repetition.contains("mensalmente") || repetition.contains("Mensal")

        val iterations = when {
            repetition.contains("Para sempre") -> if (isDaily) 3650 else 120
            repetition.contains("N meses") || repetition.contains("N dias") -> numRepetitions
            else -> 1
        }

        val stepField = when {
            isMonthly -> Calendar.MONTH
            isDaily -> Calendar.DAY_OF_YEAR
            else -> null
        }

        val recurrenceId = if (iterations > 1) UUID.randomUUID().toString() else null

        for (i in 0 until iterations) {
            val txDate = calendar.timeInMillis
            transactionsToSave.add(
                Transaction(
                    description = description,
                    value = value,
                    date = txDate,
                    type = type,
                    isRecurring = iterations > 1,
                    recurrenceId = recurrenceId,
                    dayOfMonth = if (stepField == Calendar.MONTH) calendar.get(Calendar.DAY_OF_MONTH) else null
                )
            )
            
            if (stepField != null) {
                calendar.add(stepField, 1)
            } else {
                break
            }
        }

        transactionDao.insertTransactions(transactionsToSave)
    }

    suspend fun applyCalculatorValueToProjection(value: Double) {
        val desc = "Gasto Diário (Calculadora)"
        val type = TransactionType.DIARIO
        
        // 1. Clear previous ones
        transactionDao.deleteTransactionsByDescriptionAndType(desc, type)
        
        // 2. Generate new ones for 10 years, starting from the 1st of the current month
        if (value > 0) {
            val startCalendar = Calendar.getInstance()
            startCalendar.set(Calendar.DAY_OF_MONTH, 1)
            startCalendar.set(Calendar.HOUR_OF_DAY, 0)
            startCalendar.set(Calendar.MINUTE, 0)
            startCalendar.set(Calendar.SECOND, 0)
            startCalendar.set(Calendar.MILLISECOND, 0)

            saveTransactionWithRepetition(
                description = desc,
                value = value,
                date = startCalendar.timeInMillis,
                type = type,
                repetition = "Diário (Para sempre)",
                numRepetitions = 0
            )
        }
    }

    suspend fun clearCalculatorProjection() {
        transactionDao.deleteTransactionsByDescriptionAndType("Gasto Diário (Calculadora)", TransactionType.DIARIO)
    }
}
