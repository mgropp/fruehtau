package io.gropp.fruehtau.util

fun String.toTitleCase(): String =
    this.lowercase().split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
