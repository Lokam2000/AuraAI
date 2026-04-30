package com.example.auraai.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
    val isUserMessage: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val conversationId: String = "default"
)