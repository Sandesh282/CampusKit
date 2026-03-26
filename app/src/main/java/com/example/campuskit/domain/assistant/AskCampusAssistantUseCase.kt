package com.example.campuskit.domain.assistant

import com.example.campuskit.data.assistant.AssistantContextBuilder
import com.example.campuskit.data.assistant.GeminiAssistantService
import com.example.campuskit.data.assistant.llm.LLMModelManager
import com.example.campuskit.data.assistant.llm.MediaPipeLLMService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Orchestrates the full RAG pipeline:
 *
 * 1. Retrieve: embed the query on-device → cosine similarity over [AssistantContextBuilder]
 * 2. Generate:
 *    - If the Gemma model is downloaded → [MediaPipeLLMService] (fully on-device, offline)
 *    - Otherwise → [GeminiAssistantService] (cloud fallback, requires network)
 *
 * The retrieval step is always on-device and offline.
 * The generation step is on-device once the model is downloaded.
 */
class AskCampusAssistantUseCase @Inject constructor(
    private val contextBuilder: AssistantContextBuilder,
    private val geminiService: GeminiAssistantService,
    private val mediaPipeService: MediaPipeLLMService,
    private val modelManager: LLMModelManager,
) {
    operator fun invoke(query: String): Flow<String> = flow {
        // Step 1: on-device semantic retrieval (always offline)
        val context = contextBuilder.buildContext(query)

        // Step 2: generation — prefer on-device if model is ready
        val responseFlow = if (modelManager.isModelReady) {
            mediaPipeService.generate(query, context)
        } else {
            geminiService.sendMessage(query, context)
        }
        emitAll(responseFlow)
    }
}
