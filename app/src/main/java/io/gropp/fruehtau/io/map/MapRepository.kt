package io.gropp.fruehtau.io.map

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import io.gropp.fruehtau.di.IoDispatcher
import io.gropp.fruehtau.io.download.DownloadPurpose
import io.gropp.fruehtau.io.download.DownloadService
import io.gropp.fruehtau.util.findDirectories
import io.gropp.fruehtau.util.findFilesWithExtensionRec
import io.gropp.fruehtau.util.unzip
import java.io.BufferedInputStream
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _availableMaps = MutableStateFlow(listMaps())
    val availableMaps: StateFlow<Map<String, List<MapId>>> = _availableMaps.asStateFlow()

    private fun listMaps(): Map<String, List<MapId>> =
        findDirectories(baseDir).associate { dir ->
            dir.name to findFilesWithExtensionRec(dir, MAP_EXT).map { MapId(dir.name, it.nameWithoutExtension) }
        }

    private fun updateAvailableMaps() {
        _availableMaps.value = listMaps()
    }

    suspend fun loadMapOrDefault(mapId: MapId?): MapDataStore? = mapId?.let { loadMap(it) } ?: loadDefaultMap()

    private fun loadMap(mapId: MapId): MapDataStore {
        Timber.i("Loading map: $mapId")
        val (packageName, mapName) = mapId
        val dir = File(baseDir, packageName)

        return if (mapId.mapName != null) {
            val file = File(dir, "${mapName}.$MAP_EXT")
            loadMapFile(file)
        } else {
            val files = findFilesWithExtensionRec(dir, MAP_EXT)
            MultiMapDataStore().apply {
                files.forEachIndexed { index, file ->
                    val mapFile = loadMapFile(file)
                    addMapDataStore(mapFile, index == 0, index == 0)
                }
            }
        }
    }

    private suspend fun loadDefaultMap(): MapDataStore? {
        Timber.i("Loading default map")
        return listMaps().keys.firstOrNull()?.let { loadMap(MapId(it, null)) }
            ?: run {
                Timber.i("No maps found, downloading test map")
                downloadTestMap()
                null
            }
    }

    private fun loadMapFile(file: File) = MapFile(file).also { Timber.i("Loaded map file: ${file.name}") }

    suspend fun importFromUri(uri: Uri, target: String) {
        withContext(ioDispatcher) {
            Timber.i("Importing map from $uri to $target")

            val targetDir = File(baseDir, target)
            targetDir.mkdirs()

            appContext.contentResolver.openInputStream(uri).use { stream ->
                unzip(BufferedInputStream(stream), targetDir)
            }
            Timber.i("Import complete, reloading maps")
            updateAvailableMaps()
        }
    }

    private suspend fun downloadTestMap() {
        downloadService.enqueueDownload(
            "https://ftp.gwdg.de/pub/misc/openstreetmap/openandromaps/mapsV5/germany/Ruegen.zip",
            DownloadPurpose.MAP,
            "test",
        )
    }

    companion object {
        private const val MAP_EXT = "map"
    }
}
