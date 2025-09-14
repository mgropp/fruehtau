package io.gropp.fruehtau.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import io.gropp.fruehtau.ui.map.MapView
import io.gropp.fruehtau.ui.menu.MenuScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main() {
    AppTheme { MenuScaffold { onUiAction -> MapView(onUiAction) } }
}
