package com.example.auraai.data

interface ChatResponder {
    suspend fun generateReply(userMessage: String, history: List<Message>): String
}