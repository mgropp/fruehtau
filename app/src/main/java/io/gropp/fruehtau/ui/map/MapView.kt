package io.gropp.fruehtau.ui.map

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import io.gropp.fruehtau.ui.action.UiAction
import io.gropp.fruehtau.ui.common.AdjustStatusBar
import io.gropp.fruehtau.ui.toolbar.ToolbarScaffold
import io.gropp.fruehtau.util.WhenLoaded
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.layer.renderer.TileRendererLayer

@Composable
fun MapView(
    onUiAction: (action: UiAction) -> Unit = {},
    viewModel: MapViewModel = hiltViewModel(checkNotNull(LocalViewModelStoreOwner.current), null),
) {
    ToolbarScaffold(
        topBarContent = {
            IconButton(onClick = { onUiAction(UiAction.ToggleMainMenu) }) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Center on location",
                    tint = Color.White,
                )
            }
        }
    ) {
        WhenLoaded(viewModel.tileRendererLayerProvider) { tileRendererLayerProvider ->
            AdjustStatusBar(mapMode = true)
            MapViewControl(tileRendererLayerProvider, viewModel)
        }
    }
}

@Composable
private fun MapViewControl(tileRendererLayerProvider: TileRendererLayerProvider, viewModel: MapViewModel) {
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
}
