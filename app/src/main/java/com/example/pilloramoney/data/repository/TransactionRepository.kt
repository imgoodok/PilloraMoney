package com.example.pilloramoney.data.repository

import com.example.pilloramoney.data.local.TransactionDao
import com.example.pilloramoney.data.model.SubscriptionStatus
import com.example.pilloramoney.data.model.Transaction
import com.example.pilloramoney.data.model.TransactionType
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val authRepository: AuthRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val firestore: FirebaseFirestore
) {
    private val currentUserId: String
        get() = authRepository.currentUser?.uid ?: "ANONYMOUS"

    suspend fun saveTransactionWithRepetition(
        description: String,
        value: Double,
        date: Long,
        type: TransactionType,
        repetition: String,
        numRepetitions: Int = 1
    ) {
        val userId = currentUserId
        val transactionsToSave = mutableListOf<Transaction>()
        val calendar = Calendar.getInstance().apply { timeInMillis = date }
        
        val isDaily = repetition.contains("todos os dias") || repetition.contains("Diário")
        val isMonthly = repetition.contains("mensalmente") || repetition.contains("Mensal")

        val iterations = when {
            repetition.contains("Para sempre") -> if (isDaily) 3650 else 120
            repetition.contains("N meses") || repetition.contains("N dias") -> numRepetitions
            else -> 1
        }

        val stepField = when {
            isMonthly -> Calendar.MONTH
            isDaily -> Calendar.DAY_OF_YEAR
            else -> null
        }

        val recurrenceId = if (iterations > 1) UUID.randomUUID().toString() else null

        for (i in 0 until iterations) {
            val txDate = calendar.timeInMillis
            transactionsToSave.add(
                Transaction(
                    userId = userId,
                    description = description,
                    value = value,
                    date = txDate,
                    type = type,
                    isRecurring = iterations > 1,
                    recurrenceId = recurrenceId,
                    dayOfMonth = if (stepField == Calendar.MONTH) calendar.get(Calendar.DAY_OF_MONTH) else null
                )
            )
            
            if (stepField != null) {
                calendar.add(stepField, 1)
            } else {
                break
            }
        }

        transactionDao.insertTransactions(transactionsToSave)
        
        // Sync to Firestore if Premium
        val subscription = subscriptionRepository.getSubscriptionStatus().first()
        if (subscription.status == SubscriptionStatus.PREMIUM) {
            syncTransactionsToFirestore(transactionsToSave)
        }
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
        
        val subscription = subscriptionRepository.getSubscriptionStatus().first()
        if (subscription.status == SubscriptionStatus.PREMIUM) {
            val userId = currentUserId
            if (userId != "ANONYMOUS") {
                firestore.collection("users").document(userId)
                    .collection("transactions").document(transaction.syncId)
                    .delete().await()
            }
        }
    }

    private suspend fun syncTransactionsToFirestore(transactions: List<Transaction>) {
        val userId = currentUserId
        if (userId == "ANONYMOUS") return
        
        val batch = firestore.batch()
        transactions.forEach { tx ->
            val docRef = firestore.collection("users").document(userId)
                .collection("transactions").document(tx.syncId)
            batch.set(docRef, tx)
        }
        batch.commit().await()
    }

    suspend fun applyCalculatorValueToProjection(value: Double) {
        val userId = currentUserId
        val desc = "Gasto Diário (Calculadora)"
        val type = TransactionType.DIARIO
        
        // 1. Clear previous ones locally
        transactionDao.deleteTransactionsByDescriptionAndType(userId, desc, type)
        
        // 2. Clear previous ones in Firestore if Premium
        val subscription = subscriptionRepository.getSubscriptionStatus().first()
        if (subscription.status == SubscriptionStatus.PREMIUM && userId != "ANONYMOUS") {
            val snapshots = firestore.collection("users").document(userId)
                .collection("transactions")
                .whereEqualTo("description", desc)
                .whereEqualTo("type", type.name)
                .get().await()
            
            val batch = firestore.batch()
            for (doc in snapshots.documents) {
                batch.delete(doc.reference)
            }
            batch.commit().await()
        }
        
        // 3. Generate new ones
        if (value > 0) {
            val startCalendar = Calendar.getInstance()
            startCalendar.set(Calendar.DAY_OF_MONTH, 1)
            startCalendar.set(Calendar.HOUR_OF_DAY, 0)
            startCalendar.set(Calendar.MINUTE, 0)
            startCalendar.set(Calendar.SECOND, 0)
            startCalendar.set(Calendar.MILLISECOND, 0)

            saveTransactionWithRepetition(
                description = desc,
                value = value,
                date = startCalendar.timeInMillis,
                type = type,
                repetition = "Diário (Para sempre)",
                numRepetitions = 0
            )
        }
    }

    suspend fun clearCalculatorProjection() {
        val userId = currentUserId
        val desc = "Gasto Diário (Calculadora)"
        val type = TransactionType.DIARIO
        
        transactionDao.deleteTransactionsByDescriptionAndType(userId, desc, type)
        
        val subscription = subscriptionRepository.getSubscriptionStatus().first()
        if (subscription.status == SubscriptionStatus.PREMIUM && userId != "ANONYMOUS") {
            val snapshots = firestore.collection("users").document(userId)
                .collection("transactions")
                .whereEqualTo("description", desc)
                .whereEqualTo("type", type.name)
                .get().await()
            
            val batch = firestore.batch()
            for (doc in snapshots.documents) {
                batch.delete(doc.reference)
            }
            batch.commit().await()
        }
    }
}
