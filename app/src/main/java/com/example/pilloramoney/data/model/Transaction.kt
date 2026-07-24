package com.example.pilloramoney.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    ENTRADA, SAIDA, DIARIO, CARTAO, ECONOMIA
}

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long, // Epoch timestamp for the specific day
    val type: TransactionType,
    val value: Double,
    val description: String = "",
    val isRecurring: Boolean = false,
    val dayOfMonth: Int? = null // For monthly recurrence
)
