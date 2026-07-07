package com.example.campuskit.data.assistant.vector

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Background worker that (re)builds the vector index from Room data.
 *
 * Enqueued by [CampusKitApplication] on every app start using [ExistingWorkPolicy.KEEP],
 * so it only runs if no other indexing job is already in progress.
 *
 * Embedding ~100-200 chunks takes 5-15 seconds on a mid-range device.
 * This runs on a background thread and does not block the UI.
 */
@HiltWorker
class VectorIndexingWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val vectorStore: VectorStore,
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME = "vector_index"
    }

    override suspend fun doWork(): Result {
        return try {
            vectorStore.indexAll()
            Result.success()
        } catch (e: Exception) {
            // Retry once on failure (e.g. model not yet loaded)
            if (runAttemptCount < 2) Result.retry() else Result.failure()
        }
    }
}
