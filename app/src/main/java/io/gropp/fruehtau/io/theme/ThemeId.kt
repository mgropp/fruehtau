package io.gropp.fruehtau.io.theme

/** Identifies a theme. [packageName] is null for built-in themes. */
data class ThemeId(val packageName: String?, val themeName: String)
