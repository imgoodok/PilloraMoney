package com.example.pilloramoney.data.local

import androidx.room.*
import com.example.pilloramoney.data.model.MonthlyBalance
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthlyBalanceDao {
    @Query("SELECT * FROM monthly_balances WHERE monthKey = :monthKey")
    suspend fun getBalanceForMonth(monthKey: String): MonthlyBalance?

    @Query("SELECT * FROM monthly_balances WHERE monthKey = :monthKey")
    fun getBalanceFlowForMonth(monthKey: String): Flow<MonthlyBalance?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBalance(balance: MonthlyBalance)
}
