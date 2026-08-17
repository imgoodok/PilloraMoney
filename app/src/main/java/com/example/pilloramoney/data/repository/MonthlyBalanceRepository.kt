package com.example.pilloramoney.data.repository

import com.example.pilloramoney.data.local.MonthlyBalanceDao
import com.example.pilloramoney.data.model.MonthlyBalance
import com.example.pilloramoney.data.model.SubscriptionStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MonthlyBalanceRepository @Inject constructor(
    private val monthlyBalanceDao: MonthlyBalanceDao,
    private val subscriptionRepository: SubscriptionRepository,
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) {
    suspend fun upsertBalance(balance: MonthlyBalance) {
        monthlyBalanceDao.upsertBalance(balance)
        
        val subscription = subscriptionRepository.getSubscriptionStatus().first()
        if (subscription.status == SubscriptionStatus.PREMIUM) {
            val userId = authRepository.currentUser?.uid ?: return
            firestore.collection("users").document(userId)
                .collection("balances").document(balance.monthKey)
                .set(balance).await()
        }
    }
}
