package com.example.pilloramoney.ui.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pilloramoney.data.model.Community
import com.example.pilloramoney.data.model.Post
import com.example.pilloramoney.data.repository.CommunityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityDetailViewModel @Inject constructor(
    private val communityRepository: CommunityRepository
) : ViewModel() {

    private val _communityId = MutableStateFlow<String?>(null)
    
    val community: StateFlow<Community?> = _communityId.flatMapLatest { id ->
        kotlinx.coroutines.flow.flow {
            if (id != null) {
                emit(communityRepository.getCommunityById(id).getOrNull())
            } else {
                emit(null)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val posts: StateFlow<List<Post>> = _communityId.flatMapLatest { id ->
        if (id != null) {
            communityRepository.getPostsByCommunity(id)
        } else {
            kotlinx.coroutines.flow.flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isUploading = MutableStateFlow(false)
    val isUploading = _isUploading.asStateFlow()

    private val _events = MutableSharedFlow<CommunityEvent>()
    val events: SharedFlow<CommunityEvent> = _events.asSharedFlow()

    fun setCommunityId(id: String) {
        _communityId.value = id
    }

    fun createPost(content: String, bitmap: Bitmap? = null) {
        val id = _communityId.value ?: return
        viewModelScope.launch {
            try {
                _isUploading.value = true
                val result = communityRepository.createPost(content, id, bitmap)
                if (result.isSuccess) {
                    _events.emit(CommunityEvent.PostCreated)
                } else {
                    _events.emit(CommunityEvent.Error(result.exceptionOrNull()?.message ?: "Erro ao postar"))
                }
            } catch (e: Exception) {
                _events.emit(CommunityEvent.Error(e.message ?: "Erro desconhecido"))
            } finally {
                _isUploading.value = false
            }
        }
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            communityRepository.toggleLike(postId)
        }
    }
}
