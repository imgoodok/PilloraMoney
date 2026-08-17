package com.example.pilloramoney.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "financial_goals")
data class FinancialGoal(
    @PrimaryKey val userId: String, // One goal per user
    val targetValue: Double = 0.0
)
