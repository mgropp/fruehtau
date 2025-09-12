package io.gropp.fruehtau.util

import java.io.File

fun findDirectories(dir: File): List<File> {
    require(dir.isDirectory) { "${dir.absolutePath} is not a directory" }
    return dir.listFiles()?.filter { it.isDirectory } ?: emptyList()
}

fun findFilesWithExtension(dir: File, extension: String): List<File> {
    require(dir.isDirectory) { "${dir.absolutePath} is not a directory" }
    return dir.listFiles()?.filter { it.isFile && it.extension == extension } ?: emptyList()
}

fun findFilesWithExtensionRec(dir: File, extension: String): List<File> {
    require(dir.isDirectory) { "${dir.absolutePath} is not a directory" }
    return dir.walkTopDown().filter { it.isFile && it.extension == extension }.toList()
}

val File.nameWithoutExt: String
    get() = name.apply { substring(0, lastIndexOf('.')) }
