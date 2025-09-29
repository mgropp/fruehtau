package io.gropp.fruehtau.util

import java.io.File
import java.io.InputStream

fun InputStream.copyToFile(targetFile: File) {
    targetFile.outputStream().buffered().use { output -> copyTo(output) }
}
