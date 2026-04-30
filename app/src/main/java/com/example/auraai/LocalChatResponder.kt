package com.example.auraai.data

import kotlinx.coroutines.delay

class LocalChatResponder : ChatResponder {

    override suspend fun generateReply(userMessage: String, history: List<Message>): String {
        delay(700)

        val cleanMessage = userMessage.trim()
        val normalizedMessage = cleanMessage.lowercase()
        val words = normalizedMessage
            .split(Regex("[^a-z0-9]+"))
            .filter { it.isNotBlank() }
            .toSet()

        return when {
            normalizedMessage.contains("how are you") || normalizedMessage.contains("how r u") -> {
                "I am doing well and ready to help. What would you like to work on?"
            }

            words.any { it in greetingWords } -> {
                "Hello! I am AuraAI. Ask me a question, share an idea, or tell me what you want help with."
            }

            words.contains("ai") || normalizedMessage.contains("artificial intelligence") -> {
                "AI, or artificial intelligence, is software that can perform tasks that usually need human thinking. It can understand text, answer questions, summarize information, recognize patterns, and help generate ideas or code. Modern chatbots use AI models trained on large amounts of text so they can predict helpful responses from your message."
            }

            words.contains("android") || words.contains("kotlin") || words.contains("compose") -> {
                "For Android development, Kotlin handles the app logic and Jetpack Compose builds the UI. A clean pattern is to keep UI in composables, state in a ViewModel, and storage or network work in a repository."
            }

            words.contains("auraai") || words.contains("app") -> {
                "AuraAI is a simple chat app using Jetpack Compose, MVVM, Room for local chat history, and a local response engine while no real AI API is connected."
            }

            words.contains("help") -> {
                "Sure. Tell me the topic and the kind of help you want: explanation, steps, code, debugging, or a short summary."
            }

            cleanMessage.endsWith("?") -> {
                "Good question. Based on your message, I would start by identifying the main topic, then explain it in simple terms with an example. Could you share whether you want a short answer or a detailed one?"
            }

            cleanMessage.length > 120 -> {
                "I understand. Your message has a few details, so the best next step is to separate the main point from the supporting context. What outcome do you want from this: advice, a summary, or an action plan?"
            }

            else -> {
                "I understand: \"$cleanMessage\". Tell me a little more about what you want to do with it, and I will help from there."
            }
        }
    }

    private companion object {
        val greetingWords = setOf("hi", "hello", "hey")
    }
}