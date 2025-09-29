package io.gropp.fruehtau.io.map

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import io.gropp.fruehtau.di.IoDispatcher
import io.gropp.fruehtau.io.download.DownloadPurpose
import io.gropp.fruehtau.io.download.DownloadService
import io.gropp.fruehtau.util.copyToFile
import io.gropp.fruehtau.util.findDirectories
import io.gropp.fruehtau.util.findFilesWithExtensionRec
import io.gropp.fruehtau.util.unzip
import java.io.BufferedInputStream
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.io.path.createTempFile
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

    private val worldMapDir: File
        get() = File(appContext.filesDir, "worldmap").also { it.mkdirs() }

    private val worldMapFile: File
        get() = File(worldMapDir, "world.map")

    private val _availableMaps = MutableStateFlow(listMaps())
    val availableMaps: StateFlow<List<MapPackage>> = _availableMaps.asStateFlow()

    private val _availableMapsOrWorldMap = MutableStateFlow(listOf<MapPackage>())
    val availableMapsOrWorldMap = _availableMapsOrWorldMap.asStateFlow()

    private fun listMaps(): List<MapPackage> = findMapPackages()

    private fun updateAvailableMaps() {
        val maps = listMaps()
        _availableMaps.value = maps
        _availableMapsOrWorldMap.value =
            when {
                maps.isNotEmpty() -> maps
                worldMapFile.exists() -> listOf(MapPackage.WORLD_MAP)
                else -> emptyList()
            }
    }

    suspend fun loadMapOrDefault(mapPackageId: MapPackageId?): MapDataStore? =
        mapPackageId?.let(::loadMapPackage) ?: loadWorldMap()

    private fun loadMapPackage(mapPackageId: MapPackageId): MapDataStore {
        Timber.i("Loading map package: $mapPackageId")
        val files = findMapFilesInPackage(mapPackageId)
        return MultiMapDataStore().apply {
            addMapDataStore(loadMapFile(worldMapFile), false, false)
            files.forEachIndexed { index, file ->
                val mapFile = loadMapFile(file)
                addMapDataStore(mapFile, index == 0, index == 0)
            }
        }
    }

    private suspend fun loadWorldMap(): MapDataStore? {
        Timber.i("Loading world map")
        return if (worldMapFile.exists()) {
            loadMapFile(worldMapFile)
        } else {
            Timber.i("Downloading world map")
            downloadWorldMap()
            null
        }
    }

    private fun loadMapFile(file: File): MapFile = MapFile(file).also { Timber.i("Loaded map file: ${file.name}") }

    suspend fun importFromUri(uri: Uri, source: String, target: String) {
        withContext(ioDispatcher) {
            Timber.i("Importing map from $uri to $target")

            val targetDir = File(baseDir, target)
            targetDir.mkdirs()

            appContext.contentResolver.openInputStream(uri)?.buffered().use { stream ->
                when {
                    stream == null -> {
                        Timber.e("Failed to open input stream for URI: $uri")
                    }

                    stream.isZipFile() -> {
                        Timber.i("Importing zip map file from $uri")
                        unzip(BufferedInputStream(stream), targetDir)
                    }

                    else -> {
                        Timber.i("Importing uncompressed map file from $uri (downloaded from $source)")
                        val destFile = getOutputFile(targetDir, source.toUri())
                        stream.copyToFile(destFile)
                        Timber.i("Copied map file to: ${destFile.absolutePath}")
                    }
                }
            }
        }

        updateAvailableMaps()
    }

    suspend fun importWorldMapFromUri(uri: Uri) {
        withContext(ioDispatcher) {
            Timber.i("Importing world map from $uri")
            worldMapDir.mkdirs()
            appContext.contentResolver.openInputStream(uri)?.buffered().use { stream ->
                when {
                    stream == null -> {
                        Timber.e("Failed to open input stream for URI: $uri")
                    }

                    else -> {
                        stream.copyToFile(worldMapFile)
                        Timber.i("Copied world map file to: ${worldMapFile.absolutePath}")
                    }
                }
            }
        }

        Timber.i("Import complete, reloading maps")
        updateAvailableMaps()
    }

    private fun getOutputFile(targetDir: File, sourceUri: Uri): File =
        sourceUri.lastPathSegment?.substringAfterLast('/')?.let { File(targetDir, it) }
            ?: createTempFile(directory = targetDir.toPath(), prefix = "imported_map", suffix = MAP_EXT).toFile()

    private fun BufferedInputStream.isZipFile(): Boolean {
        mark(4)
        val signature = ByteArray(4)
        val bytesRead = read(signature)
        reset()
        return bytesRead == 4 && signature.contentEquals(byteArrayOf(0x50, 0x4B, 0x03, 0x04))
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

    private suspend fun downloadWorldMap() {
        downloadService.enqueueDownload(
            "https://ftp-stud.hs-esslingen.de/pub/Mirrors/download.mapsforge.org/maps/v5/world/world.map",
            DownloadPurpose.WORLD_MAP,
            null,
        )
    }

    private fun getMapPackageDirectory(mapPackageId: MapPackageId): File = File(baseDir, mapPackageId.value)

    private fun getMapFile(mapPackageId: MapPackageId, mapId: String): File =
        File(getMapPackageDirectory(mapPackageId), "$mapId.$MAP_EXT")

    private fun findMapFilesInPackage(mapPackageId: MapPackageId): List<File> =
        findFilesWithExtensionRec(getMapPackageDirectory(mapPackageId), MAP_EXT)

    private fun findMapPackages(): List<MapPackage> =
        findDirectories(baseDir).mapNotNull { dir ->
            val mapPackageId = MapPackageId(dir.name)
            val maps = findMapFilesInPackage(mapPackageId).map { it.nameWithoutExtension }

            if (maps.isEmpty()) null else MapPackage(mapPackageId, maps)
        }

    companion object {
        private const val MAP_EXT = "map"
    }
}
