package io.gropp.fruehtau.ui.map

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import io.gropp.fruehtau.ui.action.UiAction
import io.gropp.fruehtau.ui.common.AdjustStatusBar
import io.gropp.fruehtau.ui.toolbar.ToolbarScaffold
import io.gropp.fruehtau.util.WhenLoaded

@Composable
fun MapViewControl(
    onUiAction: (action: UiAction) -> Unit = {},
    viewModel: MapViewModel = hiltViewModel(checkNotNull(LocalViewModelStoreOwner.current), null),
) {
    ToolbarScaffold(
        topBarContent = {
            IconButton(onClick = { onUiAction(UiAction.ToggleMainMenu) }) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { viewModel.centerMapOnCurrentLocation() }) {
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
            MapViewContainer(tileRendererLayerProvider, viewModel)
        }
    }
}
