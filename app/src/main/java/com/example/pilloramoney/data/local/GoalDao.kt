package com.example.pilloramoney.data.local

import androidx.room.*
import com.example.pilloramoney.data.model.FinancialGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM financial_goals WHERE userId = :userId")
    fun getSavingsGoal(userId: String): Flow<FinancialGoal?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGoal(goal: FinancialGoal)
}
