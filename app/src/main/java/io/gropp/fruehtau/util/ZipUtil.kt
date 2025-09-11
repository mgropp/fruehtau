package io.gropp.fruehtau.util

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

fun unzip(inputStream: InputStream, targetDir: File) {
    if (!targetDir.exists()) {
        targetDir.mkdirs()
    }

    ZipInputStream(inputStream).use { zis ->
        var entry = zis.nextEntry
        while (entry != null) {
            val outFile = File(targetDir, entry.name)

            if (entry.isDirectory) {
                outFile.mkdirs()
            } else {
                outFile.parentFile?.mkdirs()

                FileOutputStream(outFile).use { output -> zis.copyTo(output) }
            }

            zis.closeEntry()
            entry = zis.nextEntry
        }
    }
}
