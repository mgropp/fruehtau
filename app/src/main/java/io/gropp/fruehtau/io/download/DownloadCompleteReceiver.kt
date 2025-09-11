package io.gropp.fruehtau.io.download

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.AndroidEntryPoint
import io.gropp.fruehtau.io.MapRepository
import io.gropp.fruehtau.io.ThemeRepository
import io.gropp.fruehtau.io.db.PendingDownloadRepository
import javax.inject.Inject
import timber.log.Timber

@AndroidEntryPoint
class DownloadCompleteReceiver : BroadcastReceiver() {
    @Inject lateinit var workManager: WorkManager

    override fun onReceive(context: Context, intent: Intent) {
        Timber.i("Download complete: %s", intent)
        if (intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            Timber.i("Download %d complete", downloadId)
            if (downloadId != -1L) {
                workManager.processDownloadedFile(downloadId)
            }
        }
    }

    private fun WorkManager.processDownloadedFile(downloadId: Long) {
        val req =
            OneTimeWorkRequestBuilder<PostDownloadWorker>()
                .setInputData(workDataOf(PostDownloadWorker.PARAM_DOWNLOAD_ID to downloadId))
                .build()
        enqueue(req)
    }
}

@HiltWorker
class PostDownloadWorker
@AssistedInject
constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val pendingDownloadRepository: PendingDownloadRepository,
    private val mapRepository: MapRepository,
    private val themeRepository: ThemeRepository,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val downloadId = inputData.getLong(PARAM_DOWNLOAD_ID, -1L)
        if (downloadId == -1L) {
            Timber.w("Invalid or missing download id")
            return Result.failure()
        }
        Timber.i("Processing download %d", downloadId)

        val dm = applicationContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        try {
            try {
                val uri = dm.getUriForDownloadedFile(downloadId)
                val downloadInfo = pendingDownloadRepository.get(downloadId)
                if (downloadInfo == null) {
                    Timber.w("No pending download info for id %d", downloadId)
                    return Result.failure()
                }

                when (downloadInfo.purpose) {
                    DownloadPurpose.MAP -> mapRepository.importFromUri(uri, downloadInfo.target)
                    DownloadPurpose.THEME -> themeRepository.importFromUri(uri)
                }
            } finally {
                dm.remove(downloadId)
            }
        } finally {
            pendingDownloadRepository.delete(downloadId)
        }

        return Result.success()
    }

    companion object {
        const val PARAM_DOWNLOAD_ID = "downloadId"
    }
}
