package com.example.pilloramoney.data.model

import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Community(
    val id: String = "",
    val name: String? = "",
    val description: String? = "",
    val creatorId: String? = "",
    val imageUrl: String? = "",
    val imageBlob: Blob? = null,
    val memberCount: Long = 0L,
    @ServerTimestamp val createdAt: Date? = null
)

data class Post(
    val id: String = "",
    val communityId: String? = "",
    val authorId: String? = "",
    val authorName: String? = "",
    val authorPhotoUrl: String? = "",
    val content: String? = "",
    val imageUrl: String? = "",
    val imageBlob: Blob? = null,
    val likesCount: Long = 0L,
    @ServerTimestamp val createdAt: Date? = null
)

data class CommunityUser(
    val id: String = "",
    val displayName: String? = "",
    val photoUrl: String? = "",
    val bio: String? = ""
)

data class Like(
    val userId: String = "",
    val postId: String = "",
    @ServerTimestamp val timestamp: Date? = null
)
