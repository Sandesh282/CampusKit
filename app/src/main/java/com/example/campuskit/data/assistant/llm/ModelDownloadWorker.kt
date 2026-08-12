package com.example.campuskit.data.assistant.llm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
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
 *
 * Runs as a foreground service so Android does not kill it while the app is backgrounded.
 * Shows a persistent notification with a progress bar in the system notification shade.
 * Reports download progress via [setProgress] so the in-app UI can also track it.
 *
 * File size: ~1.4 GB. Atomic rename ensures partial files are never treated as complete.
 */
@HiltWorker
class ModelDownloadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME = "gemma_model_download"
        const val PROGRESS_KEY = "download_progress"
        const val CHANNEL_ID = "model_download_channel"
        private const val NOTIFICATION_ID = 9001
    }

    override suspend fun getForegroundInfo(): ForegroundInfo =
        buildForegroundInfo(progress = 0, indeterminate = true)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        // Promote to foreground service immediately — prevents Android from killing us
        setForeground(buildForegroundInfo(progress = 0, indeterminate = true))

        val destFile = File(applicationContext.filesDir, LLMModelManager.MODEL_FILENAME)
        val tempFile = File(applicationContext.filesDir, "${LLMModelManager.MODEL_FILENAME}.tmp")

        try {
            val url = URL(LLMModelManager.MODEL_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 15_000
            connection.readTimeout = 60_000
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext if (runAttemptCount < 2) Result.retry() else Result.failure()
            }

            val totalBytes = connection.contentLengthLong
            var downloadedBytes = 0L
            var lastReportedProgress = -1

            connection.inputStream.use { input ->
                FileOutputStream(tempFile).use { output ->
                    val buffer = ByteArray(32 * 1024) // 32KB chunks
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        downloadedBytes += bytesRead

                        if (totalBytes > 0) {
                            val progress = (downloadedBytes * 100 / totalBytes).toInt()
                            if (progress != lastReportedProgress) {
                                lastReportedProgress = progress
                                // Update system notification
                                setForeground(buildForegroundInfo(progress, indeterminate = false))
                                // Update in-app progress bar
                                setProgress(workDataOf(PROGRESS_KEY to progress))
                            }
                        }
                    }
                }
            }

            // Atomic rename — prevents a partial file being treated as complete
            tempFile.renameTo(destFile)

            // Show completion notification
            notifyComplete()
            Result.success()
        } catch (e: Exception) {
            tempFile.delete()
            if (runAttemptCount < 2) Result.retry() else Result.failure()
        }
    }

    private fun buildForegroundInfo(progress: Int, indeterminate: Boolean): ForegroundInfo {
        ensureChannelExists()
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Downloading AI Model")
            .setContentText(
                if (indeterminate) "Starting download…" else "Downloading Gemma ($progress%)"
            )
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(100, progress, indeterminate)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .build()
        return ForegroundInfo(
            NOTIFICATION_ID,
            notification,
            android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
        )
    }

    private fun notifyComplete() {
        ensureChannelExists()
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("AI Model Ready")
            .setContentText("Gemma is downloaded. Campus Assistant now runs fully offline.")
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setAutoCancel(true)
            .build()
        nm.notify(NOTIFICATION_ID, notification)
    }

    private fun ensureChannelExists() {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (nm.getNotificationChannel(CHANNEL_ID) == null) {
            nm.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "AI Model Download",
                    NotificationManager.IMPORTANCE_LOW, // silent, no sound
                ).apply { description = "Shows progress while downloading the on-device AI model" }
            )
        }
    }
}
