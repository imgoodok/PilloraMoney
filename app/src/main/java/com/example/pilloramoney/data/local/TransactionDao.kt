package com.example.pilloramoney.data.local

import androidx.room.*
import com.example.pilloramoney.data.model.Transaction
import com.example.pilloramoney.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE userId = :userId AND date >= :startDate AND date <= :endDate")
    fun getTransactionsInRange(userId: String, startDate: Long, endDate: Long): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE userId = :userId AND isRecurring = 1")
    suspend fun getRecurringTransactions(userId: String): List<Transaction>

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC LIMIT 100")
    fun getLatestTransactions(userId: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND type = :type ORDER BY date ASC LIMIT 100")
    fun getTransactionsByType(userId: String, type: TransactionType): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND type = 'ECONOMIA' ORDER BY date ASC")
    fun getAllSavings(userId: String): Flow<List<Transaction>>

    @Query("SELECT SUM(value) FROM transactions WHERE userId = :userId AND type = 'ECONOMIA'")
    fun getTotalSavingsSum(userId: String): Flow<Double?>

    @Query("DELETE FROM transactions WHERE userId = :userId")
    suspend fun deleteAll(userId: String)

    @Query("DELETE FROM transactions WHERE userId = :userId AND type = :type")
    suspend fun deleteByType(userId: String, type: TransactionType)

    @Query("DELETE FROM transactions WHERE userId = :userId AND description = :desc AND type = :type")
    suspend fun deleteTransactionsByDescriptionAndType(userId: String, desc: String, type: TransactionType)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<Transaction>)
}
