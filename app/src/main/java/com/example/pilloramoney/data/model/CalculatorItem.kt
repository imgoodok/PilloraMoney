package com.example.pilloramoney.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Frequency {
    DAILY, WEEKLY, MONTHLY
}

@Entity(tableName = "calculator_items")
data class CalculatorItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val name: String,
    val value: Double,
    val frequency: Frequency
) {
    val dailyValue: Double
        get() = when (frequency) {
            Frequency.DAILY -> value
            Frequency.WEEKLY -> value / 7.0
            Frequency.MONTHLY -> value / 30.0 // Simplified monthly average
        }
}
