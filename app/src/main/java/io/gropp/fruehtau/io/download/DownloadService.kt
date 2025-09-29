package io.gropp.fruehtau.io.download

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import io.gropp.fruehtau.io.db.PendingDownloadRepository
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class DownloadService
@Inject
constructor(
    @param:ApplicationContext private val appContext: Context,
    private val pendingDownloadRepository: PendingDownloadRepository,
) {
    suspend fun enqueueDownload(url: String, purpose: DownloadPurpose, target: String?): Long {
        val uri = url.toUri()
        val request =
            DownloadManager.Request(url.toUri())
                .setTitle(purpose.title)
                .setDescription("Downloading…")
                .setAllowedOverMetered(false)
                .setAllowedOverRoaming(false)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalFilesDir(
                    appContext,
                    Environment.DIRECTORY_DOWNLOADS,
                    uri.pathSegments.lastOrNull() ?: "downloaded_file",
                )

        val dm = appContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val id = dm.enqueue(request)
        pendingDownloadRepository.add(id, purpose, url, target)
        Timber.i("Enqueued download of $url with id $id")
        return id
    }

    private val DownloadPurpose.title: String
        get() =
            when (this) {
                DownloadPurpose.MAP -> "Downloading map"
                DownloadPurpose.WORLD_MAP -> "Downloading world map"
                DownloadPurpose.THEME -> "Downloading theme"
            }
}
