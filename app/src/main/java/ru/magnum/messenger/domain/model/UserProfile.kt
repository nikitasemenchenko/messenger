package ru.magnum.messenger.domain.model

data class UserProfile(
    val uid: String,
    val email: String,
    val username: String,
    val avatarUrl: String?,
    val createdAt: Long
)