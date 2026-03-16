package com.example.auraai

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    // Insert a message
    @Insert
    suspend fun insertMessage(message: Message)

    // Get all messages for a conversation
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: String): Flow<List<Message>>

    // Get all messages (all conversations)
    @Query("SELECT * FROM messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<Message>>

    // Delete a message
    @Delete
    suspend fun deleteMessage(message: Message)

    // Delete all messages for a conversation
    @Query("DELETE FROM messages WHERE conversationId = :conversationId")
    suspend fun deleteConversation(conversationId: String)

    // Clear all messages
    @Query("DELETE FROM messages")
    suspend fun clearAllMessages()
}