package com.example.pilloramoney.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "monthly_balances",
    primaryKeys = ["userId", "monthKey"]
)
data class MonthlyBalance(
    val userId: String = "",
    val monthKey: String = "", // Format: "yyyy-MM"
    val initialBalance: Double = 0.0
)
