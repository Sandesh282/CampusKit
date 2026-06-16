package com.example.campuskit.domain.assistant

import com.example.campuskit.data.assistant.AssistantContextBuilder
import com.example.campuskit.data.assistant.GeminiAssistantService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Domain use case that orchestrates the RAG-inspired pipeline:
 * 1. Retrieve relevant context from Room via [AssistantContextBuilder]
 * 2. Send query + context to Gemini via [GeminiAssistantService]
 * 3. Emit streaming response tokens back to the ViewModel
 */
class AskCampusAssistantUseCase @Inject constructor(
    private val contextBuilder: AssistantContextBuilder,
    private val service: GeminiAssistantService,
) {
    operator fun invoke(query: String): Flow<String> = flow {
        val context = contextBuilder.buildContext(query)
        emitAll(service.sendMessage(query, context))
    }
}
