package io.gropp.fruehtau.ui.screens

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import io.gropp.fruehtau.ui.map.MapViewControl
import io.gropp.fruehtau.ui.menu.MenuScaffold

class MapScreen : Screen {
    @Composable
    override fun Content() {
        MenuScaffold { onUiAction -> MapViewControl(onUiAction) }
    }
}
