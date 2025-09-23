package io.gropp.fruehtau.ui.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.gropp.fruehtau.di.IoDispatcher
import io.gropp.fruehtau.io.map.MapService
import io.gropp.fruehtau.io.preferences.MapViewPositionRepository
import io.gropp.fruehtau.io.theme.ThemeService
import io.gropp.fruehtau.service.LocationService
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.mapsforge.map.view.MapView
import timber.log.Timber

@HiltViewModel
class MapViewModel
@Inject
constructor(
    mapService: MapService,
    themeService: ThemeService,
    private val mapViewPositionRepository: MapViewPositionRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext appContext: Context,
    val locationService: LocationService,
) : ViewModel() {
    val mapDataStore =
        mapService.mapDataStore.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    val tileRendererLayerProvider =
        TileRendererLayerProvider.createFlow(mapService, themeService, appContext)
            .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = null)

    fun saveMapViewPosition(mapView: MapView) {
        viewModelScope.launch(ioDispatcher) { mapViewPositionRepository.save(mapView.model.mapViewPosition) }
    }

    fun restoreMapViewPosition(mapView: MapView) {
        Timber.i("Restoring map view position")
        viewModelScope.launch(ioDispatcher) {
            if (mapViewPositionRepository.restoreTo(mapView.model.mapViewPosition)) {
                Timber.i("Restored saved map view position.")
            } else {
                Timber.i("No saved map view position")
                mapDataStore.value?.apply {
                    mapView.model.mapViewPosition.center = startPosition()
                    mapView.model.mapViewPosition.zoomLevel = startZoomLevel()
                }
            }
        }
    }
}
