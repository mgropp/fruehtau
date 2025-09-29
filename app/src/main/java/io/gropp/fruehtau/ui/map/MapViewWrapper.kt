package io.gropp.fruehtau.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.layer.renderer.TileRendererLayer

@Composable
fun MapViewWrapper(tileRendererLayerProvider: TileRendererLayerProvider, viewModel: MapViewModel) {
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var previousTileRendererLayer by remember { mutableStateOf<TileRendererLayer?>(null) }

    AndroidView(
        factory = { context ->
            tileRendererLayerProvider.clear()
            MapView(context).apply {
                mapView = this
                isClickable = true
                layerManager.layers.add(0, tileRendererLayerProvider.instance)
                viewModel.restoreMapViewPosition(this)
            }
        },
        update = { view ->
            if (mapView !== view) {
                mapView = view
            }
            tileRendererLayerProvider.instance
                .takeIf { it != previousTileRendererLayer }
                ?.let { tileRendererLayer ->
                    previousTileRendererLayer?.let { view.layerManager.layers.remove(it) }
                    if (!view.layerManager.layers.contains(tileRendererLayer)) {
                        view.layerManager.layers.add(0, tileRendererLayer)
                    }
                    previousTileRendererLayer = tileRendererLayer
                }
        },
        onRelease = { view -> viewModel.saveMapViewPosition(view) },
    )

    mapView?.let { LocationIndicator(it, viewModel.locationService) }

    LaunchedEffect(Unit) {
        viewModel.mapCommands.collect { command ->
            when (command) {
                is MapCommand.SetMapPosition -> mapView?.setCenter(command.position)
            }
        }
    }
}
