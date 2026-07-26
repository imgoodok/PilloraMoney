package com.example.pilloramoney.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    ENTRADA, SAIDA, ECONOMIA, CARTAO, DIARIO, TRANSFERENCIA
}

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long, // Epoch timestamp for the specific day
    val type: TransactionType,
    val value: Double,
    val description: String = "",
    val category: String = "Geral",
    val isRecurring: Boolean = false,
    val recurrenceId: String? = null, // To link generated recurring items
    val dayOfMonth: Int? = null 
)
