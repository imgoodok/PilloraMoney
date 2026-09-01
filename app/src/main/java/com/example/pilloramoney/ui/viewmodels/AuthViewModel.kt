package com.example.pilloramoney.ui.viewmodels

import android.content.Context
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pilloramoney.auth.CredentialManagerHelper
import com.example.pilloramoney.auth.RestoreCredentialHelper
import com.example.pilloramoney.data.repository.AuthRepository
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val credentialManagerHelper: CredentialManagerHelper,
    private val restoreCredentialHelper: RestoreCredentialHelper
) : ViewModel() {

    val currentUser: StateFlow<FirebaseUser?> = authRepository.authStateFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), authRepository.currentUser)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signInWithEmail(email, password)
            if (result.isSuccess) {
                _authState.value = AuthState.Success
                // TODO: Obter requestJson do seu servidor para criar a chave de restauração
                // restoreCredentialHelper.createRestoreKey(context, "SUA_JSON_AQUI")
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Erro desconhecido")
            }
        }
    }

    fun signUpWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signUpWithEmail(email, password)
            _authState.value = if (result.isSuccess) AuthState.Success else AuthState.Error(result.exceptionOrNull()?.message ?: "Erro desconhecido")
        }
    }

    fun signInWithGoogle(context: Context, webClientId: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val googleIdOption = credentialManagerHelper.getGoogleIdOption(webClientId)
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManagerHelper.getCredential(context, request)
                val credential = result.credential
                
                if (credential is androidx.credentials.CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                    
                    val authResult = authRepository.signInWithCredential(firebaseCredential)
                    if (authResult.isSuccess) {
                        _authState.value = AuthState.Success
                        // TODO: Obter requestJson do seu servidor para criar a chave de restauração
                        // restoreCredentialHelper.createRestoreKey(context, "SUA_JSON_AQUI")
                    } else {
                        _authState.value = AuthState.Error(authResult.exceptionOrNull()?.message ?: "Erro no Firebase")
                    }
                } else {
                    _authState.value = AuthState.Error("Tipo de credencial inválido")
                }
            } catch (e: GetCredentialException) {
                _authState.value = AuthState.Error(e.message ?: "Erro no Credential Manager")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Erro inesperado")
            }
        }
    }

    fun signOut(context: Context) {
        viewModelScope.launch {
            authRepository.signOut()
            restoreCredentialHelper.clearRestoreKey(context)
        }
    }

    fun trySilentRestoreLogin(context: Context) {
        viewModelScope.launch {
            // TODO: Obter authenticationJson do seu servidor
            // val restoreJson = restoreCredentialHelper.getRestoreKey(context, "SUA_JSON_AQUI")
            // if (restoreJson != null) {
            //     // Realizar login no Firebase com o token restaurado
            // }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data object Success : AuthState()
    data class Error(val message: String) : AuthState()
}
