package com.example.pilloramoney.data.repository

import com.example.pilloramoney.data.model.SubscriptionStatus
import com.example.pilloramoney.data.model.UserSubscription
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
)
{
    private val userId: String?
        get() = authRepository.currentUser?.uid

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSubscriptionStatus(): Flow<UserSubscription> {
        return authRepository.authStateFlow().flatMapLatest { user ->
            val uid = user?.uid
            if (uid == null) {
                android.util.Log.d("SubscriptionRepo", "No user, returning default FREE status")
                flowOf(UserSubscription())
            } else {
                android.util.Log.d("SubscriptionRepo", "User logged in: $uid, starting Firestore listener")
                callbackFlow {
                    val listener = firestore.collection("users").document(uid)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                android.util.Log.e("SubscriptionRepo", "Firestore error: ${error.message}")
                                return@addSnapshotListener
                            }

                            val subscription = snapshot?.toObject(UserSubscription::class.java)
                                ?: UserSubscription()
                            
                            // Log real status from Firestore to verify it's correct
                            android.util.Log.d("SubscriptionRepo", "Subscription data from Firestore: status=${subscription.status}, expiry=${subscription.expiryDate}")
                            trySend(subscription)
                        }

                    awaitClose { 
                        android.util.Log.d("SubscriptionRepo", "Closing Firestore listener for $uid")
                        listener.remove() 
                    }
                }
            }
        }
    }
// ...

    suspend fun updateSubscriptionStatus(status: SubscriptionStatus, expiryDays: Int = 30) {
        val uid = userId ?: run {
            android.util.Log.e("SubscriptionRepo", "No user logged in, cannot update subscription")
            return
        }
        android.util.Log.d("SubscriptionRepo", "Updating Firestore for user $uid with status $status")
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

        try {
            firestore.collection("users").document(uid).set(data).await()
            android.util.Log.d("SubscriptionRepo", "Firestore update successful")
        } catch (e: Exception) {
            android.util.Log.e("SubscriptionRepo", "Firestore update failed: ${e.message}", e)
            throw e
        }
    }
}
