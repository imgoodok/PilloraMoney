package com.example.pilloramoney.data.repository

import com.example.pilloramoney.data.model.SubscriptionStatus
import com.example.pilloramoney.data.model.UserSubscription
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) {
    private val userId: String?
        get() = authRepository.currentUser?.uid

    fun getSubscriptionStatus(): Flow<UserSubscription> {
        val uid = userId

        // Um fluxo callbackFlow precisa sempre terminar com awaitClose. Para
        // usuários não autenticados, não há listener do Firestore a manter.
        if (uid == null) return flowOf(UserSubscription())

        return callbackFlow {
            val listener = firestore.collection("users").document(uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener

                    val subscription = snapshot?.toObject(UserSubscription::class.java)
                        ?: UserSubscription()
                    trySend(subscription)
                }

            awaitClose { listener.remove() }
        }
    }

    suspend fun updateSubscriptionStatus(status: SubscriptionStatus, expiryDays: Int = 30) {
        val uid = userId ?: return
        val expiryDate = if (status == SubscriptionStatus.PREMIUM) {
            System.currentTimeMillis() + (expiryDays * 24 * 60 * 60 * 1000L)
        } else if (status == SubscriptionStatus.EXPIRED) {
            System.currentTimeMillis() - (1 * 24 * 60 * 60 * 1000L) // Expired yesterday
        } else {
            null
        }

        val data = mapOf(
            "status" to status.name,
            "expiryDate" to expiryDate
        )

        firestore.collection("users").document(uid).set(data).await()
    }
}
