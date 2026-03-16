package com.example.auraai

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val text: String,
    val isUserMessage: Boolean, // true = user, false = AI
    val timestamp: Long = System.currentTimeMillis(),
    val conversationId: String = "default"
)