package io.gropp.fruehtau.io.preferences

import io.gropp.fruehtau.io.map.MapId
import io.gropp.fruehtau.io.theme.ThemeId

data class Settings(val mapId: MapId? = null, val themeId: ThemeId? = null)
