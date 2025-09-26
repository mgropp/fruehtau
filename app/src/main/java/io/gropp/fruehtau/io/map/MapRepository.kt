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
    val availableMaps: StateFlow<List<MapPackage>> = _availableMaps.asStateFlow()

    private fun listMaps(): List<MapPackage> = findMapPackages()

    private fun updateAvailableMaps() {
        _availableMaps.value = listMaps()
    }

    suspend fun loadMapOrDefault(mapPackageId: MapPackageId?): MapDataStore? =
        mapPackageId?.let(::loadMap) ?: loadDefaultMap()

    private fun loadMap(mapPackageId: MapPackageId): MapDataStore {
        Timber.i("Loading map package: $mapPackageId")
        val files = findMapFilesInPackage(mapPackageId)
        return MultiMapDataStore().apply {
            files.forEachIndexed { index, file ->
                val mapFile = loadMapFile(file)
                addMapDataStore(mapFile, index == 0, index == 0)
            }
        }
    }

    private suspend fun loadDefaultMap(): MapDataStore? {
        Timber.i("Loading default map")
        return listMaps().firstOrNull()?.let { loadMap(it.id) }
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

    suspend fun deleteMap(mapPackageId: MapPackageId, mapId: String) {
        withContext(ioDispatcher) {
            Timber.i("Deleting map $mapId from package $mapPackageId")
            val mapFile = getMapFile(mapPackageId, mapId)
            if (mapFile.exists()) {
                if (mapFile.delete()) {
                    Timber.i("Deleted map file: ${mapFile.absolutePath}")
                } else {
                    Timber.e("Failed to delete map file: ${mapFile.absolutePath}")
                }
            } else {
                Timber.w("Map file does not exist: ${mapFile.absolutePath}")
            }
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

    private fun getMapPackageDirectory(mapPackageId: MapPackageId): File = File(baseDir, mapPackageId.value)

    private fun getMapFile(mapPackageId: MapPackageId, mapId: String): File =
        File(getMapPackageDirectory(mapPackageId), "$mapId.$MAP_EXT")

    private fun findMapFilesInPackage(mapPackageId: MapPackageId): List<File> =
        findFilesWithExtensionRec(getMapPackageDirectory(mapPackageId), MAP_EXT)

    private fun findMapPackages(): List<MapPackage> =
        findDirectories(baseDir).map { dir ->
            val mapPackageId = MapPackageId(dir.name)
            MapPackage(mapPackageId, findMapFilesInPackage(mapPackageId).map { it.nameWithoutExtension })
        }

    companion object {
        private const val MAP_EXT = "map"
    }
}
