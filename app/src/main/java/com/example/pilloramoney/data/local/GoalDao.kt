package com.example.pilloramoney.data.local

import androidx.room.*
import com.example.pilloramoney.data.model.FinancialGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM financial_goals WHERE id = 'SAVINGS_GOAL'")
    fun getSavingsGoal(): Flow<FinancialGoal?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGoal(goal: FinancialGoal)
}
