package com.example.pilloramoney.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CredentialManagerHelper @Inject constructor() {

    fun getGoogleIdOption(webClientId: String): GetGoogleIdOption {
        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(true)
            .build()
    }

    suspend fun getCredential(
        context: Context,
        request: GetCredentialRequest
    ): GetCredentialResponse {
        return CredentialManager.create(context).getCredential(context, request)
    }
}
