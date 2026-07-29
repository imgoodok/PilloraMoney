package com.example.pilloramoney.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "financial_goals")
data class FinancialGoal(
    @PrimaryKey val id: String = "SAVINGS_GOAL", // We only need one for now
    val targetValue: Double = 0.0
)
