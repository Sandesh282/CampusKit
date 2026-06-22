package com.example.campuskit.data.assistant.embedding

import android.content.Context

/**
 * Minimal BERT WordPiece tokenizer for all-MiniLM-L6-v2.
 *
 * Loads vocab.txt from assets (30,522 tokens from the standard BERT vocabulary).
 * Implements: lowercasing → basic tokenization → WordPiece subword splitting → ID lookup.
 * Produces input_ids, attention_mask, and token_type_ids tensors ready for ONNX inference.
 *
 * Max sequence length is capped at 128 tokens (sufficient for short campus queries).
 */
class BertTokenizer(context: Context) {

    private val vocab: Map<String, Int>
    private val unkId = 100    // [UNK]
    private val clsId = 101    // [CLS]
    private val sepId = 102    // [SEP]
    private val padId = 0      // [PAD]
    private val maxSeqLen = 128

    init {
        vocab = context.assets.open("vocab.txt").bufferedReader().useLines { lines ->
            lines.withIndex().associate { (idx, token) -> token.trim() to idx }
        }
    }

    data class Encoding(
        val inputIds: LongArray,
        val attentionMask: LongArray,
        val tokenTypeIds: LongArray,
    )

    fun encode(text: String): Encoding {
        val tokens = tokenize(text.lowercase())
        // [CLS] + tokens (up to maxSeqLen - 2) + [SEP]
        val truncated = tokens.take(maxSeqLen - 2)
        val ids = LongArray(maxSeqLen) { padId.toLong() }
        val mask = LongArray(maxSeqLen) { 0L }

        ids[0] = clsId.toLong()
        mask[0] = 1L
        truncated.forEachIndexed { i, token ->
            ids[i + 1] = (vocab[token] ?: unkId).toLong()
            mask[i + 1] = 1L
        }
        ids[truncated.size + 1] = sepId.toLong()
        mask[truncated.size + 1] = 1L

        return Encoding(
            inputIds = ids,
            attentionMask = mask,
            tokenTypeIds = LongArray(maxSeqLen) { 0L }, // single-sequence, all zeros
        )
    }

    /**
     * Basic tokenization: split on whitespace + punctuation, then run WordPiece.
     */
    private fun tokenize(text: String): List<String> {
        return basicTokenize(text).flatMap { wordPiece(it) }
    }

    /**
     * Splits on whitespace and strips/separates punctuation characters.
     */
    private fun basicTokenize(text: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        for (char in text) {
            when {
                char.isWhitespace() -> {
                    if (current.isNotEmpty()) { result.add(current.toString()); current.clear() }
                }
                isPunctuation(char) -> {
                    if (current.isNotEmpty()) { result.add(current.toString()); current.clear() }
                    result.add(char.toString())
                }
                else -> current.append(char)
            }
        }
        if (current.isNotEmpty()) result.add(current.toString())
        return result
    }

    /**
     * WordPiece: greedily finds the longest vocab prefix, then continues with "##" suffix tokens.
     * Falls back to [UNK] if no subword found.
     */
    private fun wordPiece(word: String): List<String> {
        if (word.length > 200) return listOf("[UNK]")
        val subTokens = mutableListOf<String>()
        var start = 0
        while (start < word.length) {
            var end = word.length
            var found: String? = null
            val prefix = if (start == 0) "" else "##"
            while (start < end) {
                val substr = prefix + word.substring(start, end)
                if (vocab.containsKey(substr)) { found = substr; break }
                end--
            }
            if (found == null) return listOf("[UNK]")
            subTokens.add(found)
            start = end
        }
        return subTokens
    }

    private fun isPunctuation(c: Char): Boolean {
        val cp = c.code
        return cp in 33..47 || cp in 58..64 || cp in 91..96 || cp in 123..126 ||
            c.category in setOf(
                CharCategory.CONNECTOR_PUNCTUATION, CharCategory.DASH_PUNCTUATION,
                CharCategory.START_PUNCTUATION, CharCategory.END_PUNCTUATION,
                CharCategory.INITIAL_QUOTE_PUNCTUATION, CharCategory.FINAL_QUOTE_PUNCTUATION,
                CharCategory.OTHER_PUNCTUATION,
            )
    }
}
