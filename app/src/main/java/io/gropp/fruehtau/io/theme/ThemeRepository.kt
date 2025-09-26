package io.gropp.fruehtau.io.theme

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import io.gropp.fruehtau.di.IoDispatcher
import io.gropp.fruehtau.util.findFilesWithExtension
import io.gropp.fruehtau.util.nameWithoutExt
import io.gropp.fruehtau.util.toTitleCase
import java.io.BufferedInputStream
import java.io.File
import java.io.IOException
import java.util.zip.ZipInputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.mapsforge.map.rendertheme.XmlRenderTheme
import org.mapsforge.map.rendertheme.ZipRenderTheme
import org.mapsforge.map.rendertheme.ZipXmlThemeResourceProvider
import org.mapsforge.map.rendertheme.internal.MapsforgeThemes
import timber.log.Timber

@Singleton
class ThemeRepository
@Inject
constructor(
    @param:ApplicationContext private val appContext: Context,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    private val baseDir: File
        get() = File(appContext.filesDir, "themes").also { it.mkdirs() }

    private val _availableThemes = MutableStateFlow(listThemes())
    val availableThemes: StateFlow<List<ThemeId>> = _availableThemes.asStateFlow()

    private fun listThemes(): List<ThemeId> =
        MapsforgeThemes.entries.map { ThemeId(null, it.name.toTitleCase()) } +
            findFilesWithExtension(baseDir, THEME_EXT).flatMap(::listThemesInFile)

    private fun listThemesInFile(themeFile: File): List<ThemeId> =
        themeFile
            .inputStream()
            .use { stream -> ZipXmlThemeResourceProvider.scanXmlThemes(ZipInputStream(stream)) }
            .map { name -> ThemeId(themeFile.nameWithoutExt, name) }

    private fun updateAvailableThemes() {
        _availableThemes.value = listThemes()
    }

    suspend fun loadThemeOrDefault(themeId: ThemeId?): XmlRenderTheme? =
        themeId?.let { loadTheme(it) } ?: MapsforgeThemes.DEFAULT

    private suspend fun loadTheme(themeId: ThemeId): XmlRenderTheme =
        withContext(ioDispatcher) {
            Timber.i("Loading theme: $themeId")
            val themeFile = getThemeFile(themeId)
            if (themeFile == null) {
                getBuiltInTheme(themeId.themeName)
            } else {
                ZipRenderTheme(
                    themeId.themeName,
                    ZipXmlThemeResourceProvider(ZipInputStream(BufferedInputStream(themeFile.inputStream()))),
                )
            }
        }

    suspend fun importFromUri(uri: Uri, target: String) {
        withContext(ioDispatcher) {
            Timber.i("Importing theme from $uri to $target")

            val targetFile = File(baseDir, "$target.$THEME_EXT")
            appContext.contentResolver.openInputStream(uri)?.buffered().use { inputStream ->
                if (inputStream == null) {
                    throw IOException("Unable to open input stream for $uri")
                }

                targetFile.outputStream().buffered().use { outputStream -> inputStream.copyTo(outputStream) }
            }
            Timber.i("Import complete, reloading themes")
            updateAvailableThemes()
        }
    }

    fun deleteThemePackage(packageName: String) {
        val themeFile = getThemeFile(packageName)
        if (themeFile.exists()) {
            if (themeFile.delete()) {
                Timber.i("Deleted theme package: $packageName")
            } else {
                Timber.e("Failed to delete theme package: $packageName")
            }
            updateAvailableThemes()
        } else {
            Timber.w("No theme package found to delete with name: $packageName")
        }
    }

    private fun getThemeFile(themeId: ThemeId): File? = themeId.packageName?.let(::getThemeFile)

    private fun getThemeFile(packageName: String): File {
        val themeFile = File(baseDir, "$packageName.$THEME_EXT")
        if (!themeFile.exists()) {
            throw IllegalArgumentException("No theme package with name $packageName")
        }
        return themeFile
    }

    companion object {
        private const val THEME_EXT = "zip"
    }
}

private fun getBuiltInTheme(themeName: String): XmlRenderTheme =
    MapsforgeThemes.entries.firstOrNull { it.name.equals(themeName, ignoreCase = true) }
        ?: throw IllegalArgumentException("No built-in theme with name $themeName")
