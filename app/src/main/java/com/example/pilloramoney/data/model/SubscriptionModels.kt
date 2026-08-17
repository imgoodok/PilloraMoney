package com.example.pilloramoney.data.model

import java.util.Date

enum class SubscriptionStatus {
    FREE,
    PREMIUM,
    EXPIRED
}

data class UserSubscription(
    val status: SubscriptionStatus = SubscriptionStatus.FREE,
    val expiryDate: Long? = null,
    val lastSyncTimestamp: Long? = null
) {
    fun isGracePeriodActive(): Boolean {
        if (status != SubscriptionStatus.EXPIRED || expiryDate == null) return false
        val sevenDaysInMillis = 7 * 24 * 60 * 60 * 1000L
        return System.currentTimeMillis() < (expiryDate + sevenDaysInMillis)
    }
}
