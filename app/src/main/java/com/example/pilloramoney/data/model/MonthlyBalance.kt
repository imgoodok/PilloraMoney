package com.example.pilloramoney.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monthly_balances")
data class MonthlyBalance(
    @PrimaryKey val monthKey: String, // Format: "yyyy-MM"
    val initialBalance: Double = 0.0
)
