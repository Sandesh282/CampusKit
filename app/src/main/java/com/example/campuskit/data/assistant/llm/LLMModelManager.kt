package com.example.campuskit.data.assistant.llm

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the lifecycle of the on-device Gemma model file.
 *
 * - Exposes [downloadState] so the UI can show download progress or prompt.
 * - [enqueueDownload] kicks off [ModelDownloadWorker] via WorkManager.
 * - [modelFile] returns the path once the model is ready.
 */
@Singleton
class LLMModelManager @Inject constructor(
    private val context: Context,
) {
    companion object {
        const val MODEL_FILENAME = "gemma-1.1-2b-it-gpu-int4.bin"
        // Publicly accessible from Google AI Edge
        const val MODEL_URL =
            "https://storage.googleapis.com/mediapipe-models/llm_inference/" +
            "gemma-1.1-2b-it-gpu-int4/float16/1/gemma-1.1-2b-it-gpu-int4.bin"
    }

    val modelFile: File
        get() = File(context.filesDir, MODEL_FILENAME)

    val isModelReady: Boolean
        get() = modelFile.exists() && modelFile.length() > 0

    private val _downloadState = MutableStateFlow<ModelDownloadState>(
        if (isModelReady) ModelDownloadState.Ready else ModelDownloadState.NotDownloaded
    )
    val downloadState: StateFlow<ModelDownloadState> = _downloadState.asStateFlow()

    fun enqueueDownload() {
        if (isModelReady) {
            _downloadState.value = ModelDownloadState.Ready
            return
        }
        _downloadState.value = ModelDownloadState.Downloading(0)
        val request = OneTimeWorkRequestBuilder<ModelDownloadWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            ModelDownloadWorker.WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request,
        )
        // Observe WorkManager state to update our flow
        WorkManager.getInstance(context)
            .getWorkInfosForUniqueWorkLiveData(ModelDownloadWorker.WORK_NAME)
            .observeForever { workInfos ->
                val info = workInfos?.firstOrNull() ?: return@observeForever
                when (info.state) {
                    WorkInfo.State.SUCCEEDED -> _downloadState.value = ModelDownloadState.Ready
                    WorkInfo.State.FAILED -> _downloadState.value =
                        ModelDownloadState.Failed("Download failed. Please try again.")
                    WorkInfo.State.RUNNING -> {
                        val progress = info.progress.getInt(ModelDownloadWorker.PROGRESS_KEY, 0)
                        _downloadState.value = ModelDownloadState.Downloading(progress)
                    }
                    else -> Unit
                }
            }
    }
}

sealed class ModelDownloadState {
    data object NotDownloaded : ModelDownloadState()
    data class Downloading(val progressPercent: Int) : ModelDownloadState()
    data object Ready : ModelDownloadState()
    data class Failed(val message: String) : ModelDownloadState()
}
