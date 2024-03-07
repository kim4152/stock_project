package com.project.stockproject.stockInform.openai

import com.google.gson.annotations.SerializedName

//응답
data class ChatCompletionResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage,
    @SerializedName("system_fingerprint")
    val systemFingerprint: String
)

data class Choice(
    val index: Int,
    val message: Message,
    val logprobs: Any?, // Type may need to be adjusted based on the actual data
    @SerializedName("finish_reason")
    val finishReason: String
)

data class Message(
    val role: String,
    val content: String
)

data class Usage(
    @SerializedName("prompt_tokens")
    val promptTokens: Int,
    @SerializedName("completion_tokens")
    val completionTokens: Int,
    @SerializedName("total_tokens")
    val totalTokens: Int
)

data class FocusAreasResponse(
    @SerializedName("focus_areas")
    val focusAreas: List<String>
)
//요청
data class ChatRequest(
    val model: String ="gpt-3.5-turbo",
    val messages: List<MessageRequest>,
    val temperature: Int =0,
    val max_tokens: Int =100,
    val top_p: Int =1,
)

data class MessageRequest(
    val role: String ="user",
    val content: String
)
