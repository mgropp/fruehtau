package io.gropp.fruehtau.service

import org.mapsforge.core.model.LatLong

data class Location(val latitude: Double, val longitude: Double, val accuracy: Float? = null) {
    fun toLatLong() = LatLong(latitude, longitude)
}
