package com.example.auraai

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val messageDao = AppDatabase.getDatabase(application).messageDao()
    private val apiService = ApiClient.getApiService("your-api-key-here") // We'll replace this later

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

    // Send message to AI
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

                // Create request for Claude
                val messages = (_messages.value + userMsg).map { msg ->
                    ChatMessage(
                        role = if (msg.isUserMessage) "user" else "assistant",
                        content = msg.text
                    )
                }

                val request = MessageRequest(
                    messages = messages
                )

                // Get response from Claude API
                val response = apiService.sendMessage(request)

                // Extract AI response
                val aiResponseText = response.content.firstOrNull()?.text ?: "No response"

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

    // Clear all messages
    fun clearChat() {
        viewModelScope.launch {
            messageDao.deleteConversation(conversationId)
        }
    }

    // Set API key
    fun setApiKey(apiKey: String) {
        // TODO: Update API service with new key
    }
}