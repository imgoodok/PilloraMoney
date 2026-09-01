package com.example.pilloramoney.auth

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CreateRestoreCredentialRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetRestoreCredentialOption
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestoreCredentialHelper @Inject constructor() {

    private companion object {
        const val TAG = "RestoreCredentialHelper"
    }

    /**
     * Creates a restore key for the user's account.
     * This should be called after a successful login.
     * 
     * @param context Activity context
     * @param requestJson The credential creation options sent by your app server
     */
    suspend fun createRestoreKey(context: Context, requestJson: String) {
        val credentialManager = CredentialManager.create(context)
        try {
            val createRestoreRequest = CreateRestoreCredentialRequest(requestJson)
            credentialManager.createCredential(context, createRestoreRequest)
            Log.d(TAG, "Restore key created successfully")
        } catch (e: CreateCredentialException) {
            Log.e(TAG, "Failed to create restore key", e)
        }
    }

    /**
     * Attempts to retrieve the restore key to perform a silent login.
     * This should be called during app startup on a new device.
     * 
     * @param context Activity context
     * @param authenticationJson The options required to get the restore key from the server
     * @return The public key credential response in JSON format, or null if failed
     */
    suspend fun getRestoreKey(context: Context, authenticationJson: String): String? {
        val credentialManager = CredentialManager.create(context)
        return try {
            val options = GetRestoreCredentialOption(authenticationJson)
            val getRequest = GetCredentialRequest(listOf(options))
            val response = credentialManager.getCredential(context, getRequest)
            
            // The response contains the public key credential response
            response.credential.data.toString() // Simplified for now
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Failed to get restore key", e)
            null
        }
    }

    /**
     * Clears the credential state. Should be called on sign out.
     */
    suspend fun clearRestoreKey(context: Context) {
        val credentialManager = CredentialManager.create(context)
        try {
            val clearRequest = ClearCredentialStateRequest()
            credentialManager.clearCredentialState(clearRequest)
            Log.d(TAG, "Credential state cleared")
        } catch (e: ClearCredentialException) {
            Log.e(TAG, "Failed to clear credential state", e)
        }
    }
}
