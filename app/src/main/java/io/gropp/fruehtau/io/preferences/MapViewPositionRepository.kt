package io.gropp.fruehtau.io.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import org.mapsforge.core.model.LatLong
import org.mapsforge.core.util.LatLongUtils
import org.mapsforge.map.model.MapViewPosition

@Singleton
class MapViewPositionRepository @Inject constructor(@param:ApplicationContext private val context: Context) {
    private val Context.mapViewPositionDataStore: DataStore<Preferences> by
        preferencesDataStore(name = "map_view_position")

    suspend fun save(mapPosition: MapViewPosition) {
        save(mapPosition.center, mapPosition.zoomLevel)
    }

    suspend fun save(center: LatLong, zoomLevel: Byte) {
        context.mapViewPositionDataStore.edit { pref ->
            pref[PREF_LATITUDE] = center.latitudeE6
            pref[PREF_LONGITUDE] = center.longitudeE6
            pref[PREF_ZOOM_LEVEL] = zoomLevel.toInt()
        }
    }

    suspend fun restoreTo(mapViewPosition: MapViewPosition): Boolean {
        val prefs = context.mapViewPositionDataStore.data.first()
        val latitude = prefs[PREF_LATITUDE] ?: return false
        val longitude = prefs[PREF_LONGITUDE] ?: return false
        val zoomLevel = prefs[PREF_ZOOM_LEVEL] ?: return false

        mapViewPosition.center =
            LatLong(LatLongUtils.microdegreesToDegrees(latitude), LatLongUtils.microdegreesToDegrees(longitude))
        mapViewPosition.zoomLevel = zoomLevel.toByte()
        return true
    }

    companion object {
        private val PREF_LATITUDE = intPreferencesKey("latitude")
        private val PREF_LONGITUDE = intPreferencesKey("longitude")
        private val PREF_ZOOM_LEVEL = intPreferencesKey("zoom_level")
    }
}
