package io.gropp.fruehtau.io.map

data class MapPackage(val id: MapPackageId?, val maps: List<String>) {
    companion object {
        val WORLD_MAP = MapPackage(null, emptyList())
    }
}
