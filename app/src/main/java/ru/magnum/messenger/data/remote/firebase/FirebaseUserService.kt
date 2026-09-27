package ru.magnum.messenger.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import ru.magnum.messenger.domain.model.UserProfile
import javax.inject.Inject

class FirebaseUserService @Inject constructor(
    private val firestore: FirebaseFirestore
){
    suspend fun createUserProfile(
        profile: UserProfile
    ) {
        firestore.collection("users").document(profile.uid)
            .set(
                mapOf(
                    "uid" to profile.uid,
                    "email" to profile.email,
                    "username" to profile.username,
                    "avatarUrl" to profile.avatarUrl,
                    "createdAt" to profile.createdAt
                )
            )
            .await()
    }

    suspend fun getProfile(
        uid: String
    ): UserProfile? {
        val dto = firestore.collection("users").document(uid).get().await()
        if(!dto.exists()){
            return null
        }
        return UserProfile(
            uid = dto.getString("uid") ?: "",
            email = dto.getString("email") ?: "",
            username = dto.getString("username") ?: "",
            avatarUrl = dto.getString("avatarUrl"),
            createdAt = dto.getLong("createdAt") ?: 0L
        )
    }

    suspend fun getUsers(): List<UserProfile> {
        val dto = firestore.collection("users").get().await()

        return dto.documents.map { document ->
            UserProfile(
                uid = document.getString("uid") ?: "",
                email = document.getString("email") ?: "",
                username = document.getString("username") ?: "",
                avatarUrl = document.getString("avatarUrl"),
                createdAt = document.getLong("createdAt") ?: 0L
            )
        }
    }
}