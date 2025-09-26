package io.gropp.fruehtau.io.preferences

import io.gropp.fruehtau.io.map.MapPackageId
import io.gropp.fruehtau.io.theme.ThemeId

data class Settings(val mapPackageId: MapPackageId? = null, val themeIds: Map<MapPackageId?, ThemeId>? = null)
