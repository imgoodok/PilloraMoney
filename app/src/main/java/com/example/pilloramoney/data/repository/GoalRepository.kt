package com.example.pilloramoney.data.repository

import com.example.pilloramoney.data.local.GoalDao
import com.example.pilloramoney.data.model.FinancialGoal
import com.example.pilloramoney.data.model.SubscriptionStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepository @Inject constructor(
    private val goalDao: GoalDao,
    private val subscriptionRepository: SubscriptionRepository,
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore
) {
    suspend fun upsertGoal(goal: FinancialGoal) {
        goalDao.upsertGoal(goal)
        
        val subscription = subscriptionRepository.getSubscriptionStatus().first()
        if (subscription.status == SubscriptionStatus.PREMIUM) {
            val userId = authRepository.currentUser?.uid ?: return
            firestore.collection("users").document(userId)
                .collection("goals").document("current_goal")
                .set(goal).await()
        }
    }
}
