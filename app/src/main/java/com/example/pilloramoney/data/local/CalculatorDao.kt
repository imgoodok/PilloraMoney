package com.example.pilloramoney.data.local

import androidx.room.*
import com.example.pilloramoney.data.model.CalculatorItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculatorDao {
    @Query("SELECT * FROM calculator_items WHERE userId = :userId")
    fun getAllItems(userId: String): Flow<List<CalculatorItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CalculatorItem)

    @Delete
    suspend fun deleteItem(item: CalculatorItem)

    @Query("SELECT * FROM calculator_items WHERE userId = :userId")
    suspend fun getAllItemsForSync(userId: String): List<CalculatorItem>
}
