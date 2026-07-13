package com.example.campuskit.data.assistant

import com.example.campuskit.data.assistant.embedding.ONNXEmbeddingService
import com.example.campuskit.data.assistant.vector.VectorStore
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Retrieval layer of the RAG pipeline.
 *
 * Embeds the user's [query] on-device using the ONNX MiniLM model,
 * then performs cosine similarity search over the local vector store
 * to return the top-k most semantically relevant campus data chunks.
 *
 * These chunks are injected into the Gemini prompt as grounded context.
 */
@Singleton
class AssistantContextBuilder @Inject constructor(
    private val embeddingService: ONNXEmbeddingService,
    private val vectorStore: VectorStore,
) {
    suspend fun buildContext(query: String): String {
        val queryEmbedding = embeddingService.embed(query)
        val topChunks = vectorStore.search(queryEmbedding, topK = 5)
        return if (topChunks.isEmpty())
            "No relevant campus data found. Answer based on general college knowledge if applicable."
        else
            topChunks.joinToString("\n\n")
    }
}
