package com.example.pilloramoney.ui.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pilloramoney.data.model.Community
import com.example.pilloramoney.data.model.Post
import com.example.pilloramoney.data.repository.CommunityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val communityRepository: CommunityRepository
) : ViewModel() {

    val topPosts: StateFlow<List<Post>> = communityRepository.getTopPostsOfDay()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val communities: StateFlow<List<Community>> = communityRepository.getAllCommunities()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isUploading = MutableStateFlow(false)
    val isUploading = _isUploading.asStateFlow()

    private val _events = MutableSharedFlow<CommunityEvent>()
    val events: SharedFlow<CommunityEvent> = _events.asSharedFlow()

    fun createPost(content: String, communityId: String, bitmap: Bitmap? = null) {
        viewModelScope.launch {
            try {
                _isUploading.value = true
                val result = communityRepository.createPost(content, communityId, bitmap)
                if (result.isSuccess) {
                    _events.emit(CommunityEvent.PostCreated)
                } else {
                    _events.emit(CommunityEvent.Error(result.exceptionOrNull()?.message ?: "Erro desconhecido"))
                }
            } catch (e: Exception) {
                android.util.Log.e("CommunityVM", "Error creating post: ${e.message}")
                _events.emit(CommunityEvent.Error(e.message ?: "Falha ao criar post"))
            } finally {
                _isUploading.value = false
            }
        }
    }

    fun createCommunity(name: String, description: String, bitmap: Bitmap? = null) {
        viewModelScope.launch {
            try {
                _isUploading.value = true
                val result = communityRepository.createCommunity(name, description, bitmap)
                if (result.isSuccess) {
                    _events.emit(CommunityEvent.CommunityCreated)
                } else {
                    _events.emit(CommunityEvent.Error(result.exceptionOrNull()?.message ?: "Erro desconhecido"))
                }
            } catch (e: Exception) {
                android.util.Log.e("CommunityVM", "Error creating community: ${e.message}")
                _events.emit(CommunityEvent.Error(e.message ?: "Falha ao criar comunidade"))
            } finally {
                _isUploading.value = false
            }
        }
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            try {
                communityRepository.toggleLike(postId)
            } catch (e: Exception) {
                android.util.Log.e("CommunityVM", "Error toggling like: ${e.message}")
            }
        }
    }
}

sealed class CommunityEvent {
    data object PostCreated : CommunityEvent()
    data object CommunityCreated : CommunityEvent()
    data class Error(val message: String) : CommunityEvent()
}
