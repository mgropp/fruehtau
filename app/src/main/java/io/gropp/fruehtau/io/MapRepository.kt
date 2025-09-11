package io.gropp.fruehtau.io

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import io.gropp.fruehtau.di.IoDispatcher
import io.gropp.fruehtau.io.download.DownloadPurpose
import io.gropp.fruehtau.io.download.DownloadService
import io.gropp.fruehtau.util.DynamicData
import io.gropp.fruehtau.util.findFilesWithExtension
import io.gropp.fruehtau.util.unzip
import java.io.BufferedInputStream
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.mapsforge.map.datastore.MapDataStore
import org.mapsforge.map.datastore.MultiMapDataStore
import org.mapsforge.map.reader.MapFile
import timber.log.Timber

@Singleton
class MapRepository
@Inject
constructor(
    @param:ApplicationContext private val appContext: Context,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val downloadService: DownloadService,
) {
    private val baseDir: File
        get() = File(appContext.filesDir, "maps").also { it.mkdirs() }

    private val _state = MutableStateFlow<DynamicData<MapDataStore>>(DynamicData.Empty)
    val state = _state.asStateFlow()

    suspend fun ensureLoaded() {
        if (_state.compareAndSet(DynamicData.Empty, DynamicData.Loading)) {
            loadMaps()
        }
    }

    private suspend fun loadMaps() {
        withContext(ioDispatcher) {
            val mapFiles = loadMapFiles()
            Timber.i("Loaded ${mapFiles.size} map files")
            if (mapFiles.isEmpty()) {
                loadTestMap()
            } else {
                val mapDataStore =
                    MultiMapDataStore().apply {
                        mapFiles.forEachIndexed { index, mapFile -> addMapDataStore(mapFile, index == 0, index == 0) }
                    }
                _state.value = DynamicData.Loaded(mapDataStore)
            }
        }
    }

    private suspend fun loadTestMap() {
        Timber.i("No map files found, loading test map")
        downloadService.enqueueDownload(
            "https://ftp.gwdg.de/pub/misc/openstreetmap/openandromaps/mapsV5/germany/Ruegen.zip",
            DownloadPurpose.MAP,
            "test",
        )
    }

    private fun loadMapFiles(): List<MapFile> {
        val files = findFilesWithExtension(baseDir, "map")
        Timber.i("Found ${files.size} map files in $baseDir")
        return files.mapNotNull { file ->
            try {
                MapFile(file).also { Timber.i("Loaded map file: ${file.name}") }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load map file: ${file.name}")
                null
            }
        }
    }

    suspend fun importFromUri(uri: Uri, target: String) {
        withContext(ioDispatcher) {
            Timber.i("Importing map from $uri to $target")

            val targetDir = File(baseDir, target)
            targetDir.mkdirs()

            appContext.contentResolver.openInputStream(uri).use { stream ->
                unzip(BufferedInputStream(stream), targetDir)
            }
            Timber.i("Import complete, reloading maps")
            loadMaps()
        }
    }
}
