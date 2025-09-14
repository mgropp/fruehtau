package io.gropp.fruehtau.io.map

/** Identifies a map. [mapName] can be set to null to use all maps in the package. */
data class MapId(val packageName: String, val mapName: String? = null) {
    val title: String
        get() = if (mapName != null) "$packageName: $mapName" else packageName
}
