package io.gropp.fruehtau.io.test

import android.content.Context
import java.io.BufferedInputStream
import java.io.File
import java.util.zip.ZipInputStream
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.XmlRenderTheme
import org.mapsforge.map.rendertheme.ZipRenderTheme
import org.mapsforge.map.rendertheme.ZipXmlThemeResourceProvider

fun loadTestMapFile(context: Context) = MapFile(context.copyAssetFileIfNotExists("test.map"))

fun loadTestThemes(context: Context): Map<String, XmlRenderTheme> {
    val file = context.copyAssetFileIfNotExists("theme.zip")
    return file
        .inputStream()
        .use { stream -> ZipXmlThemeResourceProvider.scanXmlThemes(ZipInputStream(stream)) }
        .associateWith { name ->
            ZipRenderTheme(name, ZipXmlThemeResourceProvider(ZipInputStream(BufferedInputStream(file.inputStream()))))
        }
}

private fun Context.copyAssetFileIfNotExists(filename: String): File {
    val file = File(filesDir, filename)
    if (!file.exists()) {
        assets.open(filename).use { input -> file.outputStream().use { output -> input.copyTo(output) } }
    }
    return file
}
