package io.gropp.fruehtau.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import io.gropp.fruehtau.ui.LoadingScreen
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.layer.renderer.TileRendererLayer

@Composable
fun MapView(
    viewModel: MapViewModel =
        hiltViewModel(
            checkNotNull(LocalViewModelStoreOwner.current) {
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            },
            null,
        )
) {

    val tileRendererLayer by viewModel.tileRendererLayer.collectAsState(null)
    when (val renderer = tileRendererLayer) {
        null -> LoadingScreen()
        else -> MapViewControl(renderer, viewModel)
    }
}

@Composable
private fun MapViewControl(tileRendererLayer: TileRendererLayer, viewModel: MapViewModel) {
    val mapView = remember { mutableStateOf<MapView?>(null) }
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                mapView.value = this
                isClickable = true
                layerManager.layers.add(tileRendererLayer)
                viewModel.restoreMapViewPosition(this)
            }
        },
        update = { view ->
            if (mapView.value !== view) {
                mapView.value = view
            }
        },
    )

    mapView.value?.let { LocationIndicator(it, viewModel) }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(Unit) {
        val obs = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) mapView.value?.let { viewModel.saveMapViewPosition(it) }
        }
        lifecycle.addObserver(obs)
        onDispose { lifecycle.removeObserver(obs) }
    }
}
