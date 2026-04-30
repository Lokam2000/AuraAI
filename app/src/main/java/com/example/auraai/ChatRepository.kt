package com.example.auraai.data

import kotlinx.coroutines.flow.Flow

class ChatRepository(
    private val messageDao: MessageDao,
    private val responder: ChatResponder = LocalChatResponder()
) {

    fun observeMessages(conversationId: String): Flow<List<Message>> {
        return messageDao.getMessagesForConversation(conversationId)
    }

    suspend fun sendMessage(userMessage: String, conversationId: String) {
        val cleanMessage = userMessage.trim()
        if (cleanMessage.isEmpty()) return

        messageDao.insertMessage(
            Message(
                text = cleanMessage,
                isUserMessage = true,
                conversationId = conversationId
            )
        )

        val history = messageDao.getMessagesSnapshot(conversationId)
        val reply = responder.generateReply(cleanMessage, history)

        messageDao.insertMessage(
            Message(
                text = reply,
                isUserMessage = false,
                conversationId = conversationId
            )
        )
    }

    suspend fun clearConversation(conversationId: String) {
        messageDao.deleteConversation(conversationId)
    }
}
