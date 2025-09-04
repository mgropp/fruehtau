package io.gropp.fruehtau.ui.map

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.gropp.fruehtau.di.IoDispatcher
import io.gropp.fruehtau.io.MapRepository
import io.gropp.fruehtau.io.ThemeRepository
import io.gropp.fruehtau.service.LocationService
import io.gropp.fruehtau.util.DynamicData
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.view.MapView
import timber.log.Timber

@HiltViewModel
class MapViewModel
@Inject
constructor(
    val mapRepository: MapRepository,
    val themeRepository: ThemeRepository,
    private val dataStore: DataStore<Preferences>,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext appContext: Context,
    val locationService: LocationService,
) : ViewModel() {
    val tileRendererLayerLoader = TileRendererLayerLoader(this, ioDispatcher)

    suspend fun ensureLoaded(context: Context) {
        mapRepository.ensureLoaded(context)
        themeRepository.ensureLoaded(context)
        tileRendererLayerLoader.ensureLoaded(context)
    }

    fun saveMapCamera(mapView: MapView) {
        val center = mapView.model.mapViewPosition.center
        val zoomLevel = mapView.model.mapViewPosition.zoomLevel.toInt()
        viewModelScope.launch(ioDispatcher) {
            Timber.i("Saving map camera: $center, zoomLevel=$zoomLevel")
            dataStore.edit { pref ->
                pref[PREF_LATITUDE] = center.latitudeE6
                pref[PREF_LONGITUDE] = center.longitudeE6
                pref[PREF_ZOOM_LEVEL] = zoomLevel
            }
            Timber.i("Map camera saved")
        }
    }

    fun restoreMapCamera(mapView: MapView) {
        Timber.i("Restoring map camera")
        viewModelScope.launch(ioDispatcher) {
            val prefs =
                dataStore.data
                    .catch { e ->
                        Timber.e(e, "Failed to read map camera prefs")
                        emit(emptyPreferences())
                    }
                    .first()
            val latitude = prefs[PREF_LATITUDE]
            val longitude = prefs[PREF_LONGITUDE]
            val zoomLevel = prefs[PREF_ZOOM_LEVEL]
            if (latitude != null && longitude != null && zoomLevel != null) {
                val center = LatLong(latitude.e6ToDeg(), longitude.e6ToDeg())
                Timber.i("Restoring map camera: $center, zoomLevel=$zoomLevel")
                mapView.model.mapViewPosition.center = center
                mapView.model.mapViewPosition.zoomLevel = zoomLevel.toByte()
            } else {
                Timber.i("No saved map camera position")
                val mapDataStore = (mapRepository.state.value as? DynamicData.Loaded)?.data
                if (mapDataStore != null) {
                    mapView.model.mapViewPosition.center = mapDataStore.startPosition()
                    mapView.model.mapViewPosition.zoomLevel = mapDataStore.startZoomLevel()
                }
            }
        }
    }

    companion object {
        val PREF_LATITUDE = intPreferencesKey("latitude")
        val PREF_LONGITUDE = intPreferencesKey("longitude")
        val PREF_ZOOM_LEVEL = intPreferencesKey("zoom_level")
    }
}

private fun Int.e6ToDeg(): Double = this / 1_000_000.0
