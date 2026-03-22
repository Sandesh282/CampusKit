package com.example.campuskit.data.assistant.llm

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * On-device LLM generation via MediaPipe LLM Inference API.
 * Loads Gemma 2B INT4 from internal storage and runs inference fully offline —
 * no network call required after the first-run model download.
 *
 * Uses [LlmInference.generateResponse] (synchronous) on [Dispatchers.Default]
 * so the main thread is never blocked, and wraps it in a Flow for consistency
 * with [GeminiAssistantService]'s streaming API.
 */
@Singleton
class MediaPipeLLMService @Inject constructor(
    private val context: Context,
    private val modelManager: LLMModelManager,
) {
    /** Lazy so the model is only loaded from disk on first inference call. */
    @Volatile private var _llmInference: LlmInference? = null

    private fun getLlmInference(): LlmInference = _llmInference ?: synchronized(this) {
        _llmInference ?: LlmInference.createFromOptions(
            context,
            LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelManager.modelFile.absolutePath)
                .setMaxTokens(1024)
                .setTopK(40)
                .setTemperature(0.8f)
                .setRandomSeed(101)
                .build(),
        ).also { _llmInference = it }
    }

    /**
     * Runs on-device generation for [userQuery] grounded in [ragContext].
     * Emits the full response as a single string (on-device generation does not
     * stream token-by-token in this API version).
     */
    fun generate(userQuery: String, ragContext: String): Flow<String> = flow {
        val prompt = buildPrompt(userQuery, ragContext)
        val response = withContext(Dispatchers.Default) {
            getLlmInference().generateResponse(prompt)
        }
        emit(response)
    }

    private fun buildPrompt(query: String, ragContext: String) = buildString {
        appendLine("<start_of_turn>user")
        appendLine("You are a helpful campus assistant for CampusKit, a student app.")
        appendLine("Answer ONLY based on the CONTEXT block below.")
        appendLine("If the answer is not in the context, say: I don't have that information right now.")
        appendLine("Keep answers short, clear, and friendly. Plain text only.")
        appendLine()
        appendLine("CONTEXT:")
        appendLine(ragContext)
        appendLine()
        appendLine("QUESTION: $query")
        appendLine("<end_of_turn>")
        appendLine("<start_of_turn>model")
    }

    fun close() {
        _llmInference?.close()
        _llmInference = null
    }
}
