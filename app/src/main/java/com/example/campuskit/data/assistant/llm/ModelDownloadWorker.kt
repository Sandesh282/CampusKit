package com.example.campuskit.data.assistant.llm

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Downloads the Gemma 1B-IT (INT4 GPU) model from Google AI Edge into [filesDir].
 * Reports download progress via [setProgress] so the UI can show a progress bar.
 *
 * Download URL: publicly accessible, no authentication required.
 * File size: ~1.5GB. Should only run on Wi-Fi (set constraints at enqueue site if needed).
 */
@HiltWorker
class ModelDownloadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME = "gemma_model_download"
        const val PROGRESS_KEY = "download_progress"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val destFile = File(applicationContext.filesDir, LLMModelManager.MODEL_FILENAME)
        val tempFile = File(applicationContext.filesDir, "${LLMModelManager.MODEL_FILENAME}.tmp")

        try {
            val url = URL(LLMModelManager.MODEL_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 15_000
            connection.readTimeout = 30_000
            connection.connect()

            val totalBytes = connection.contentLengthLong
            var downloadedBytes = 0L
            var lastReportedProgress = -1

            connection.inputStream.use { input ->
                FileOutputStream(tempFile).use { output ->
                    val buffer = ByteArray(8 * 1024)
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        downloadedBytes += bytesRead
                        if (totalBytes > 0) {
                            val progress = (downloadedBytes * 100 / totalBytes).toInt()
                            if (progress != lastReportedProgress) {
                                lastReportedProgress = progress
                                setProgress(workDataOf(PROGRESS_KEY to progress))
                            }
                        }
                    }
                }
            }

            // Atomic rename — prevents a partial file being treated as complete
            tempFile.renameTo(destFile)
            Result.success()
        } catch (e: Exception) {
            tempFile.delete()
            if (runAttemptCount < 2) Result.retry() else Result.failure()
        }
    }
}
