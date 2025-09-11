package io.gropp.fruehtau.util

import java.io.File

fun findFilesWithExtension(dir: File, extension: String): List<File> {
    require(dir.isDirectory) { "${dir.absolutePath} is not a directory" }
    return dir.walkTopDown().filter { it.isFile && it.extension == extension }.toList()
}
