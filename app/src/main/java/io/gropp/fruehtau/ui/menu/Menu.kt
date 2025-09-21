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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import io.gropp.fruehtau.io.map.MapId
import io.gropp.fruehtau.io.theme.ThemeId
import io.gropp.fruehtau.ui.AppTheme
import io.gropp.fruehtau.ui.action.UiAction

@Composable
fun Menu(
    viewModel: MenuViewModel =
        hiltViewModel(
            checkNotNull(LocalViewModelStoreOwner.current) {
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            },
            null,
        ),
    onUiAction: (action: UiAction) -> Unit,
) {
    val maps by viewModel.availableMaps.collectAsState()
    val themes by viewModel.availableThemes.collectAsState()

    fun onUiActionLocal(action: UiAction) {
        when (action) {
            is UiAction.SetMapTheme -> viewModel.setTheme(action.themeId)
            else -> onUiAction(action)
        }
    }

    MenuControl(maps, themes, ::onUiActionLocal)
}

private enum class ExpandedMenuSection {
    None,
    Maps,
    Themes,
}

@Composable
private fun MenuControl(maps: Map<String, List<MapId>>, themes: List<ThemeId>, onUiAction: (action: UiAction) -> Unit) {
    var expanded by remember { mutableStateOf(ExpandedMenuSection.None) }

    LazyColumn(
        modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        item { MenuListItem(Icons.Default.Route, title = "Tracks", onClick = { onUiAction(UiAction.HideMainMenu) }) }

        item {
            ExpandableMenuSection(
                icon = Icons.Default.Map,
                title = "Maps",
                expanded = expanded == ExpandedMenuSection.Maps,
                onToggle = {
                    expanded =
                        if (expanded == ExpandedMenuSection.Maps) {
                            ExpandedMenuSection.None
                        } else {
                            ExpandedMenuSection.Maps
                        }
                },
            ) {
                maps.keys.map { packageName -> MenuListItem(Icons.Outlined.Map, packageName) {} }
            }
        }

        item {
            ExpandableMenuSection(
                icon = Icons.Default.Palette,
                title = "Themes",
                expanded = expanded == ExpandedMenuSection.Themes,
                onToggle = {
                    expanded =
                        if (expanded == ExpandedMenuSection.Themes) {
                            ExpandedMenuSection.None
                        } else {
                            ExpandedMenuSection.Themes
                        }
                },
            ) {
                themes.map { id ->
                    MenuListItem(Icons.Outlined.Palette, id.title) {
                        onUiAction(UiAction.SetMapTheme(id))
                        onUiAction(UiAction.HideMainMenu)
                    }
                }
            }
        }

        item {
            MenuListItem(
                Icons.Default.Settings,
                title = "Settings",
                onClick = { onUiAction(UiAction.OpenSettingsScreen) },
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun MenuPreviewLight() {
    AppTheme { MenuControl(emptyMap(), emptyList()) {} }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MenuPreviewDark() {
    AppTheme { MenuControl(emptyMap(), emptyList()) {} }
}
