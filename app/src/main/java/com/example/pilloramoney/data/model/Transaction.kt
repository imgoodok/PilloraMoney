package com.example.pilloramoney.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

import java.util.UUID

enum class TransactionType {
    ENTRADA, SAIDA, ECONOMIA, CARTAO, DIARIO
}

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "",
    val syncId: String = UUID.randomUUID().toString(),
    val date: Long = 0,
    val type: TransactionType = TransactionType.SAIDA,
    val value: Double = 0.0,
    val description: String = "",
    val category: String = "Geral",
    val isRecurring: Boolean = false,
    val recurrenceId: String? = null,
    val dayOfMonth: Int? = null 
)
