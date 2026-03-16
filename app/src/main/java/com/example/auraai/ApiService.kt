package com.example.auraai

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// Request data class
data class MessageRequest(
    val model: String = "claude-3-5-sonnet-20241022",
    val max_tokens: Int = 1024,
    val messages: List<ChatMessage>
)

data class ChatMessage(
    val role: String, // "user" or "assistant"
    val content: String
)

// Response data class
data class ApiResponse(
    val id: String,
    val type: String,
    val role: String,
    val content: List<ContentBlock>,
    val model: String,
    val stop_reason: String,
    val stop_sequence: String?,
    val usage: Usage
)

data class ContentBlock(
    val type: String,
    val text: String
)

data class Usage(
    val input_tokens: Int,
    val output_tokens: Int
)

// Retrofit API interface
interface ClaudeApiService {
    @POST("messages")
    @Headers(
        "Content-Type: application/json",
        "anthropic-version: 2023-06-01"
    )
    suspend fun sendMessage(
        @Body request: MessageRequest
    ): ApiResponse
}

// Retrofit instance
object ApiClient {
    private const val BASE_URL = "https://api.anthropic.com/v1/"

    fun getApiService(apiKey: String): ClaudeApiService {
        val retrofit = retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .client(
                okhttp3.OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .addHeader("x-api-key", apiKey)
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            )
            .build()

        return retrofit.create(ClaudeApiService::class.java)
    }
}