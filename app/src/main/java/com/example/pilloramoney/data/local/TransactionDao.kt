package com.example.pilloramoney.data.local

import androidx.room.*
import com.example.pilloramoney.data.model.Transaction
import com.example.pilloramoney.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE date >= :startDate AND date <= :endDate")
    fun getTransactionsInRange(startDate: Long, endDate: Long): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE isRecurring = 1")
    suspend fun getRecurringTransactions(): List<Transaction>

    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT 100")
    fun getLatestTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date ASC LIMIT 100")
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE type = 'ECONOMIA' ORDER BY date ASC")
    fun getAllSavings(): Flow<List<Transaction>>

    @Query("SELECT SUM(value) FROM transactions WHERE type = 'ECONOMIA'")
    fun getTotalSavingsSum(): Flow<Double?>

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()

    @Query("DELETE FROM transactions WHERE type = :type")
    suspend fun deleteByType(type: TransactionType)

    @Query("DELETE FROM transactions WHERE description = :desc AND type = :type")
    suspend fun deleteTransactionsByDescriptionAndType(desc: String, type: TransactionType)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<Transaction>)
}
