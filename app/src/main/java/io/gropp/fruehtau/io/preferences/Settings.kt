package io.gropp.fruehtau.io.preferences

import io.gropp.fruehtau.io.map.MapPackageId
import io.gropp.fruehtau.io.theme.ThemeId

data class Settings(
    val mapPackage: MapPackageId? = null,
    val mapThemes: Map<MapPackageId?, ThemeId>? = null,
    val infoDisplayMode: InfoDisplayMode? = null,
)
