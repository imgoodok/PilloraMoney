package com.example.pilloramoney.data.repository

import com.example.pilloramoney.data.local.CalculatorDao
import com.example.pilloramoney.data.model.CalculatorItem
import com.example.pilloramoney.data.model.SubscriptionStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalculatorRepository @Inject constructor(
    private val calculatorDao: CalculatorDao,
    private val subscriptionRepository: SubscriptionRepository,
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) {
    suspend fun insertItem(item: CalculatorItem) {
        calculatorDao.insertItem(item)
        
        val subscription = subscriptionRepository.getSubscriptionStatus().first()
        if (subscription.status == SubscriptionStatus.PREMIUM) {
            val userId = authRepository.currentUser?.uid ?: return
            firestore.collection("users").document(userId)
                .collection("calculator_items").document(item.syncId)
                .set(item).await()
        }
    }

    suspend fun deleteItem(item: CalculatorItem) {
        calculatorDao.deleteItem(item)
        
        val subscription = subscriptionRepository.getSubscriptionStatus().first()
        if (subscription.status == SubscriptionStatus.PREMIUM) {
            val userId = authRepository.currentUser?.uid ?: return
            firestore.collection("users").document(userId)
                .collection("calculator_items").document(item.syncId)
                .delete().await()
        }
    }
}
