@file:OptIn(ExperimentalMaterial3Api::class)

package io.gropp.fruehtau.ui.menu

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.gropp.fruehtau.ui.AppTheme
import io.gropp.fruehtau.ui.action.UiAction

@Composable
fun Menu(onUiAction: (action: UiAction) -> Unit) {
    var mapsExpanded by remember { mutableStateOf(false) }
    var themesExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        item { MenuListItem(Icons.Default.Route, title = "Tracks", onClick = { onUiAction(UiAction.HideMainMenu) }) }

        item {
            ExpandableMenuSection(
                icon = Icons.Default.Map,
                title = "Maps",
                expanded = mapsExpanded,
                onToggle = { mapsExpanded = !mapsExpanded },
            ) {
                MenuListItem(Icons.Outlined.Map, "OpenAndroMaps") {}
            }
        }

        item {
            ExpandableMenuSection(
                icon = Icons.Default.Palette,
                title = "Themes",
                expanded = themesExpanded,
                onToggle = { themesExpanded = !themesExpanded },
            ) {
                MenuListItem(Icons.Outlined.Palette, "Elevate") {}
            }
        }

        item { MenuListItem(Icons.Default.Settings, title = "Settings", onClick = {}) }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun MenuPreviewLight() {
    AppTheme { Menu {} }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MenuPreviewDark() {
    AppTheme { Menu {} }
}
