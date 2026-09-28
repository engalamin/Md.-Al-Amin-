package com.example.data.remote

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

data class GeminiRequest(
    val contents: List<ContentItem>,
    val systemInstruction: ContentItem? = null,
    val generationConfig: GenerationConfig? = null
)

data class ContentItem(
    val parts: List<PartItem>,
    val role: String? = null
)

data class PartItem(
    val text: String? = null,
    val inlineData: InlineBlob? = null
)

data class InlineBlob(
    val mimeType: String,
    val data: String
)

data class GenerationConfig(
    val temperature: Float? = 0.3f,
    val maxOutputTokens: Int? = 2500
)

data class GeminiResponse(
    val candidates: List<CandidateItem>? = null,
    val error: ErrorDetails? = null
)

data class CandidateItem(
    val content: ContentItem? = null,
    val finishReason: String? = null
)

data class ErrorDetails(
    val code: Int? = null,
    val message: String? = null,
    val status: String? = null
)

class GeminiClient {
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val requestAdapter = moshi.adapter(GeminiRequest::class.java)
    private val responseAdapter = moshi.adapter(GeminiResponse::class.java)

    suspend fun generateContent(
        prompt: String,
        systemPrompt: String? = null,
        imageBase64: String? = null,
        imageMimeType: String = "image/jpeg"
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(
                IllegalStateException("GEMINI_API_KEY is not configured in Secrets panel")
            )
        }

        val parts = mutableListOf<PartItem>()
        parts.add(PartItem(text = prompt))
        if (!imageBase64.isNullOrBlank()) {
            parts.add(PartItem(inlineData = InlineBlob(mimeType = imageMimeType, data = imageBase64)))
        }

        val systemInstruction = systemPrompt?.let {
            ContentItem(parts = listOf(PartItem(text = it)))
        }

        val requestPayload = GeminiRequest(
            contents = listOf(ContentItem(parts = parts)),
            systemInstruction = systemInstruction,
            generationConfig = GenerationConfig()
        )

        val jsonString = requestAdapter.toJson(requestPayload)
        val requestBody = jsonString.toRequestBody("application/json".toMediaType())

        // Use model gemini-3.5-flash as specified in guidelines
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    val parsedErr = try {
                        responseAdapter.fromJson(responseBody)?.error?.message
                    } catch (e: Exception) {
                        null
                    }
                    return@withContext Result.failure(
                        Exception(parsedErr ?: "API error (${response.code}): ${response.message}")
                    )
                }

                val geminiResp = responseAdapter.fromJson(responseBody)
                val text = geminiResp?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) {
                    Result.success(text)
                } else {
                    Result.failure(Exception("No explanation text returned from model"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
