package ru.magnum.messenger.domain.model

data class Chat(
    val id: String,
    val participants: List<String>,
    val createdAt: Long
)
