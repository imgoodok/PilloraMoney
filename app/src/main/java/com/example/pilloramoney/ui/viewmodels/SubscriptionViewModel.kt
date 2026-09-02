package com.example.pilloramoney.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pilloramoney.data.model.SubscriptionStatus
import com.example.pilloramoney.data.model.UserSubscription
import com.example.pilloramoney.data.repository.SubscriptionRepository
import com.example.pilloramoney.data.repository.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val syncRepository: SyncRepository
) : ViewModel() {

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    val subscriptionStatus: StateFlow<UserSubscription> = subscriptionRepository.getSubscriptionStatus()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSubscription())

    init {
        viewModelScope.launch {
            subscriptionStatus.collect { sub ->
                if (sub.status == SubscriptionStatus.EXPIRED && !sub.isGracePeriodActive()) {
                    syncRepository.deleteCloudData()
                }
            }
        }
    }

    fun updateSubscription(status: SubscriptionStatus, context: android.content.Context? = null) {
        viewModelScope.launch {
            try {
                _isSyncing.value = true
                android.util.Log.d("SubscriptionVM", "Updating subscription to $status")
                subscriptionRepository.updateSubscriptionStatus(status)
                
                if (status == SubscriptionStatus.PREMIUM) {
                    android.util.Log.d("SubscriptionVM", "Syncing data to cloud...")
                    try {
                        syncRepository.syncAllLocalDataToCloud()
                    } catch (e: Exception) {
                        android.util.Log.e("SubscriptionVM", "Sync failed, but subscription might be updated: ${e.message}")
                    }
                }
                android.util.Log.d("SubscriptionVM", "Update completed successfully")
                context?.let {
                    android.widget.Toast.makeText(it, "Plano atualizado para $status!", android.widget.Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                android.util.Log.e("SubscriptionVM", "Error updating subscription: ${e.message}", e)
                context?.let {
                    android.widget.Toast.makeText(it, "Erro ao atualizar: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                }
            } finally {
                _isSyncing.value = false
            }
        }
    }
}
