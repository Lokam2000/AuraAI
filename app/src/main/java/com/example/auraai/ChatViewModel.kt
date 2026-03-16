package com.example.auraai

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val messageDao = AppDatabase.getDatabase(application).messageDao()

    // Messages flow
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val conversationId = "default"

    init {
        loadMessages()
    }

    // Load messages from database
    private fun loadMessages() {
        viewModelScope.launch {
            messageDao.getMessagesForConversation(conversationId).collect { messageList ->
                _messages.value = messageList
            }
        }
    }

    // Send message and get AI response
    fun sendMessage(userMessage: String) {
        if (userMessage.isBlank()) return

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                // Save user message
                val userMsg = Message(
                    text = userMessage,
                    isUserMessage = true,
                    conversationId = conversationId
                )
                messageDao.insertMessage(userMsg)

                // Simulate API delay (like Claude is thinking)
                delay(1500)

                // Get contextual response based on user input
                val aiResponseText = getContextualResponse(userMessage)

                // Save AI message
                val aiMsg = Message(
                    text = aiResponseText,
                    isUserMessage = false,
                    conversationId = conversationId
                )
                messageDao.insertMessage(aiMsg)

                _isLoading.value = false

            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
                _isLoading.value = false
            }
        }
    }

    // Get contextual response based on user input
    private fun getContextualResponse(userMessage: String): String {
        val lowerMessage = userMessage.lowercase().trim()

        return when {
            // Greetings
            lowerMessage.contains("hi") || lowerMessage.contains("hello") || lowerMessage.contains("hey") -> {
                "Hello! 👋 I'm AuraAI, your AI assistant. How can I help you today?"
            }
            lowerMessage.contains("how are you") || lowerMessage.contains("how r u") -> {
                "I'm doing great, thanks for asking! 😊 I'm here to help with anything you need. What's on your mind?"
            }
            lowerMessage.contains("what is your name") || lowerMessage.contains("who are you") -> {
                "I'm AuraAI! 🌟 An AI chat assistant built with Kotlin, Compose, and modern Android technologies."
            }
            lowerMessage.contains("what can you do") || lowerMessage.contains("what can you help") -> {
                "I can help you with coding, writing, analysis, math, learning, creative projects, and much more! Ask me anything! 💡"
            }
            lowerMessage.contains("kotlin") || lowerMessage.contains("android") || lowerMessage.contains("compose") -> {
                "Great topic! Kotlin is amazing for Android development, and Jetpack Compose makes building beautiful UIs so much easier with its declarative approach. Are you learning Android development? 📱"
            }
            lowerMessage.contains("thank") || lowerMessage.contains("thanks") -> {
                "You're welcome! 😊 Happy to help. Is there anything else you'd like to know?"
            }
            lowerMessage.contains("bye") || lowerMessage.contains("goodbye") -> {
                "Goodbye! 👋 It was great chatting with you. Feel free to come back anytime! 🚀"
            }
            lowerMessage.contains("help") -> {
                "Of course! I'm here to help. You can ask me about coding, Android development, Kotlin, Compose, problem-solving, and much more. What do you need help with?"
            }
            lowerMessage.contains("app") || lowerMessage.contains("auraai") -> {
                "AuraAI is a beautiful AI chat application built with Jetpack Compose! It demonstrates modern Android architecture with MVVM, Room database, and stunning UI design. Pretty cool, right? 🎨"
            }
            lowerMessage.contains("?") -> {
                "Great question! Let me think about that... The key to mastering Android development is consistent practice, building projects, and understanding the architecture patterns. Keep learning! 🎓"
            }
            else -> {
                // Default responses for anything else
                listOf(
                    "That's interesting! Tell me more about that. 🤔",
                    "I love your curiosity! What else would you like to know? 💭",
                    "That's a great point! Keep exploring and learning. 📚",
                    "Fascinating! Do you want to dive deeper into this topic? 🔍",
                    "Absolutely! Learning is a never-ending journey. What's next? 🚀"
                ).random()
            }
        }
    }

    // Clear all messages
    fun clearChat() {
        viewModelScope.launch {
            messageDao.deleteConversation(conversationId)
        }
    }
}