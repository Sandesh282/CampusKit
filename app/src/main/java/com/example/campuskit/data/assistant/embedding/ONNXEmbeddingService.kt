package com.example.campuskit.data.assistant.embedding

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.LongBuffer
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

/**
 * Runs all-MiniLM-L6-v2 on-device via ONNX Runtime to produce 384-dimensional
 * sentence embeddings. Embeddings are L2-normalised so cosine similarity == dot product.
 *
 * Model file: app/src/main/assets/all_minilm_l6_v2.onnx (~86MB, Apache 2.0)
 *
 * Pipeline:
 *   text → BertTokenizer → ONNX session → mean pool over sequence → L2 normalise → FloatArray(384)
 */
@Singleton
class ONNXEmbeddingService @Inject constructor(
    private val context: Context,
) {
    private val env: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val session: OrtSession by lazy { loadSession() }
    private val tokenizer: BertTokenizer by lazy { BertTokenizer(context) }

    private fun loadSession(): OrtSession {
        val modelBytes = context.assets.open("all_minilm_l6_v2.onnx").readBytes()
        return env.createSession(modelBytes, OrtSession.SessionOptions())
    }

    /**
     * Embeds [text] into a 384-dim L2-normalised float vector.
     * Runs on [Dispatchers.Default] — do NOT call from the main thread.
     */
    suspend fun embed(text: String): FloatArray = withContext(Dispatchers.Default) {
        val encoding = tokenizer.encode(text)
        val seqLen = 128L

        val inputIdsTensor = OnnxTensor.createTensor(
            env,
            LongBuffer.wrap(encoding.inputIds),
            longArrayOf(1, seqLen),
        )
        val attentionMaskTensor = OnnxTensor.createTensor(
            env,
            LongBuffer.wrap(encoding.attentionMask),
            longArrayOf(1, seqLen),
        )
        val tokenTypeIdsTensor = OnnxTensor.createTensor(
            env,
            LongBuffer.wrap(encoding.tokenTypeIds),
            longArrayOf(1, seqLen),
        )

        val inputs = mapOf(
            "input_ids" to inputIdsTensor,
            "attention_mask" to attentionMaskTensor,
            "token_type_ids" to tokenTypeIdsTensor,
        )

        val output = session.run(inputs)

        // Output shape: [1, seq_len, 384] — last_hidden_state
        @Suppress("UNCHECKED_CAST")
        val lastHiddenState = (output[0].value as Array<Array<FloatArray>>)[0]

        // Mean pool: average across token dimension, weighted by attention mask
        val embedding = FloatArray(384)
        var tokenCount = 0
        for (i in 0 until seqLen.toInt()) {
            if (encoding.attentionMask[i] == 1L) {
                for (j in 0 until 384) embedding[j] += lastHiddenState[i][j]
                tokenCount++
            }
        }
        if (tokenCount > 0) {
            for (j in 0 until 384) embedding[j] /= tokenCount
        }

        // Cleanup tensors
        inputIdsTensor.close()
        attentionMaskTensor.close()
        tokenTypeIdsTensor.close()
        output.close()

        l2Normalize(embedding)
    }

    /** L2-normalise so that dot product == cosine similarity. */
    private fun l2Normalize(v: FloatArray): FloatArray {
        val norm = sqrt(v.sumOf { (it * it).toDouble() }).toFloat()
        if (norm > 0f) for (i in v.indices) v[i] /= norm
        return v
    }
}
