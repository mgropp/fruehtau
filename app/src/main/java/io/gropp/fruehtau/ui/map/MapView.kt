package io.gropp.fruehtau.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import io.gropp.fruehtau.ui.LoadingScreen
import io.gropp.fruehtau.util.DynamicData
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.layer.renderer.TileRendererLayer

@Composable
fun MapView(viewModel: MapViewModel = hiltViewModel()) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { viewModel.ensureLoaded(context) }

    val tileRendererLayerState by viewModel.tileRendererLayerLoader.state.collectAsState(DynamicData.Loading)
    when (val state = tileRendererLayerState) {
        DynamicData.Empty,
        DynamicData.Loading -> LoadingScreen()

        is DynamicData.Loaded -> MapViewControl(state.data, viewModel)
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
                viewModel.restoreMapCamera(this)
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
            if (event == Lifecycle.Event.ON_STOP) mapView.value?.let { viewModel.saveMapCamera(it) }
        }
        lifecycle.addObserver(obs)
        onDispose { lifecycle.removeObserver(obs) }
    }
}
