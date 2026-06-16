package com.example.campuskit.data.assistant

import com.example.campuskit.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
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
        modelName = "gemini-3.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY,
    )

    /**
     * Streams Gemini's response token-by-token as a [Flow<String>].
     * System instructions are embedded directly in the prompt for maximum
     * SDK version compatibility.
     */
    fun sendMessage(userQuery: String, context: String): Flow<String> = flow {
        val prompt = buildString {
            appendLine("You are a helpful campus assistant for CampusKit, a student app.")
            appendLine("Answer ONLY based on the CONTEXT block below.")
            appendLine("If the answer is not in the context, say: I don't have that information right now.")
            appendLine("Keep answers short, clear, and friendly. Use plain text — no bullet symbols or markdown.")
            appendLine()
            appendLine("CONTEXT:")
            appendLine(context)
            appendLine()
            appendLine("QUESTION: $userQuery")
            appendLine("ANSWER:")
        }
        model.generateContentStream(prompt).collect { chunk ->
            chunk.text?.let { emit(it) }
        }
    }
}
