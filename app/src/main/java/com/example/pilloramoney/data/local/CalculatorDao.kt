package com.example.pilloramoney.data.local

import androidx.room.*
import com.example.pilloramoney.data.model.CalculatorItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculatorDao {
    @Query("SELECT * FROM calculator_items")
    fun getAllItems(): Flow<List<CalculatorItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CalculatorItem)

    @Delete
    suspend fun deleteItem(item: CalculatorItem)
}
