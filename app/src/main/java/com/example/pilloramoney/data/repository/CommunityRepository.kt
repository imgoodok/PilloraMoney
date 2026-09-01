package com.example.pilloramoney.data.repository

import android.graphics.Bitmap
import android.graphics.Matrix
import com.example.pilloramoney.data.model.Community
import com.example.pilloramoney.data.model.Post
import com.example.pilloramoney.data.model.CommunityUser
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommunityRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) {
    private val currentUserId: String get() = authRepository.currentUser?.uid ?: ""

    fun getTopPostsOfDay(): Flow<List<Post>> = callbackFlow {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.time

        val listener = firestore.collection("posts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("CommunityRepo", "Firestore Error: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val posts = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(Post::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        android.util.Log.e("CommunityRepo", "Post Mapping Error: ${e.message}")
                        null
                    }
                }?.filter { post -> 
                    val postDate = post.createdAt ?: Date() // Assume current date for local/pending posts
                    postDate.after(startOfDay) || postDate.equals(startOfDay)
                }
                ?.sortedWith(compareByDescending<Post> { it.likesCount }.thenByDescending { it.createdAt ?: Date() })
                ?: emptyList()
                trySend(posts)
            }
        awaitClose { listener.remove() }
    }

    fun getAllCommunities(): Flow<List<Community>> = callbackFlow {
        val listener = firestore.collection("communities")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("CommunityRepo", "Browse Error: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val communities = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(Community::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        android.util.Log.e("CommunityRepo", "Community Mapping Error: ${e.message}")
                        null
                    }
                }?.sortedByDescending { it.memberCount } ?: emptyList()
                trySend(communities)
            }
        awaitClose { listener.remove() }
    }

    fun getPostsByCommunity(communityId: String): Flow<List<Post>> = callbackFlow {
        val listener = firestore.collection("posts")
            .whereEqualTo("communityId", communityId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("CommunityRepo", "Filter Posts Error: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val posts = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(Post::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                trySend(posts)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getCommunityById(id: String): Result<Community> {
        return try {
            val doc = firestore.collection("communities").document(id).get().await()
            val community = doc.toObject(Community::class.java)?.copy(id = doc.id)
            if (community != null) Result.success(community)
            else Result.failure(Exception("Comunidade não encontrada"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createPost(content: String, communityId: String, bitmap: Bitmap? = null): Result<Unit> {
        return try {
            val user = authRepository.currentUser
            android.util.Log.d("CommunityRepo", "Creating post for user: ${user?.uid}")
            
            var imageBlob: Blob? = null
            if (bitmap != null) {
                android.util.Log.d("CommunityRepo", "Processing image for post...")
                imageBlob = compressAndResize(bitmap)
                android.util.Log.d("CommunityRepo", "Image processed. Size: ${imageBlob.toBytes().size} bytes")
            }

            val post = Post(
                communityId = communityId,
                authorId = user?.uid ?: "",
                authorName = user?.displayName ?: user?.email?.split("@")?.get(0) ?: "Usuário",
                authorPhotoUrl = user?.photoUrl?.toString() ?: "",
                content = content,
                imageBlob = imageBlob,
                likesCount = 0L,
                createdAt = null // Will be set by @ServerTimestamp
            )

            val docRef = firestore.collection("posts").add(post).await()
            android.util.Log.d("CommunityRepo", "Post added to Firestore with ID: ${docRef.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("CommunityRepo", "Error creating post: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun createCommunity(name: String, description: String, bitmap: Bitmap? = null): Result<Unit> {
        return try {
            val userId = currentUserId
            android.util.Log.d("CommunityRepo", "Creating community for user: $userId")
            
            if (userId.isEmpty()) throw Exception("Usuário não autenticado")

            var imageBlob: Blob? = null
            if (bitmap != null) {
                android.util.Log.d("CommunityRepo", "Processing image for community...")
                imageBlob = compressAndResize(bitmap)
                android.util.Log.d("CommunityRepo", "Image processed. Size: ${imageBlob.toBytes().size} bytes")
            }

            val community = Community(
                name = name,
                description = description,
                creatorId = userId,
                imageBlob = imageBlob,
                memberCount = 1L,
                createdAt = null
            )

            val docRef = firestore.collection("communities").add(community).await()
            android.util.Log.d("CommunityRepo", "Community added to Firestore with ID: ${docRef.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("CommunityRepo", "Error creating community: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun toggleLike(postId: String): Result<Unit> {
        return try {
            val userId = currentUserId
            val likeRef = firestore.collection("posts").document(postId).collection("likes").document(userId)
            val doc = likeRef.get().await()

            firestore.runTransaction { transaction ->
                val postRef = firestore.collection("posts").document(postId)
                val postSnapshot = transaction.get(postRef)
                val currentLikes = postSnapshot.getLong("likesCount") ?: 0

                if (doc.exists()) {
                    transaction.delete(likeRef)
                    transaction.update(postRef, "likesCount", (currentLikes - 1).coerceAtLeast(0))
                } else {
                    transaction.set(likeRef, mapOf("timestamp" to com.google.firebase.Timestamp.now()))
                    transaction.update(postRef, "likesCount", currentLikes + 1)
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun compressAndResize(bitmap: Bitmap): Blob {
        val maxWidth = 800f
        val maxHeight = 800f
        val ratio = Math.min(maxWidth / bitmap.width, maxHeight / bitmap.height)
        
        val matrix = Matrix()
        matrix.postScale(ratio, ratio)
        
        val resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        
        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
        return Blob.fromBytes(outputStream.toByteArray())
    }
}
