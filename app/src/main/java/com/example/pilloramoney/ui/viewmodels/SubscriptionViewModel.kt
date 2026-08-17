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

    fun updateSubscription(status: SubscriptionStatus) {
        viewModelScope.launch {
            _isSyncing.value = true
            subscriptionRepository.updateSubscriptionStatus(status)
            
            if (status == SubscriptionStatus.PREMIUM) {
                syncRepository.syncAllLocalDataToCloud()
            }
            
            _isSyncing.value = false
        }
    }
}
