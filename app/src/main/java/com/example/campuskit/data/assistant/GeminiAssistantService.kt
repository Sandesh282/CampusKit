package com.example.campuskit.data.assistant

import com.example.campuskit.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wraps the Gemini SDK and handles all communication with the Gemini 1.5 Flash model.
 * Takes a user query and pre-built context string, streams tokens back as a [Flow].
 */
@Singleton
class GeminiAssistantService @Inject constructor() {

    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY,
        systemInstruction = content {
            text(
                """
                You are a helpful campus assistant for CampusKit, a student companion app.
                Answer questions ONLY based on the CONTEXT block provided in each message.
                If the answer is not in the context, say: "I don't have that information right now."
                Keep answers short, clear, and friendly. Use plain text — no markdown, no bullet symbols.
                """.trimIndent()
            )
        }
    )

    /**
     * Streams Gemini's response token-by-token as a [Flow<String>].
     * The [context] is pre-fetched from Room by [AssistantContextBuilder].
     */
    fun sendMessage(userQuery: String, context: String): Flow<String> = flow {
        val prompt = buildString {
            appendLine("CONTEXT:")
            appendLine(context)
            appendLine()
            appendLine("QUESTION: $userQuery")
        }
        model.generateContentStream(prompt).collect { chunk ->
            chunk.text?.let { emit(it) }
        }
    }
}
