package com.example.pilloramoney.data.repository

import com.example.pilloramoney.data.local.CalculatorDao
import com.example.pilloramoney.data.local.GoalDao
import com.example.pilloramoney.data.local.MonthlyBalanceDao
import com.example.pilloramoney.data.local.TransactionDao
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val monthlyBalanceDao: MonthlyBalanceDao,
    private val calculatorDao: CalculatorDao,
    private val goalDao: GoalDao,
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore
) {
    private val userId: String?
        get() = authRepository.currentUser?.uid

    suspend fun syncAllLocalDataToCloud() {
        val uid = userId ?: return
        
        val transactions = transactionDao.getAllTransactionsForSync(uid)
        val balances = monthlyBalanceDao.getAllBalancesForSync(uid)
        val items = calculatorDao.getAllItemsForSync(uid)
        val goal = goalDao.getSavingsGoal(uid).first()

        val batch = firestore.batch()

        transactions.forEach {
            val ref = firestore.collection("users").document(uid).collection("transactions").document(it.syncId)
            batch.set(ref, it)
        }

        balances.forEach {
            val ref = firestore.collection("users").document(uid).collection("balances").document(it.monthKey)
            batch.set(ref, it)
        }

        items.forEach {
            val ref = firestore.collection("users").document(uid).collection("calculator_items").document(it.syncId)
            batch.set(ref, it)
        }
        
        goal?.let {
            val ref = firestore.collection("users").document(uid).collection("goals").document("current_goal")
            batch.set(ref, it)
        }

        batch.commit().await()
    }

    suspend fun deleteCloudData() {
        val uid = userId ?: return
        
        // For client-side, we delete collections by deleting their documents.
        // This is simplified. In a real app, you'd use a Cloud Function for efficiency.
        deleteCollection("users/$uid/transactions")
        deleteCollection("users/$uid/balances")
        deleteCollection("users/$uid/calculator_items")
    }

    private suspend fun deleteCollection(path: String) {
        val collection = firestore.collection(path)
        val snapshots = collection.get().await()
        val batch = firestore.batch()
        for (doc in snapshots.documents) {
            batch.delete(doc.reference)
        }
        batch.commit().await()
    }
}
