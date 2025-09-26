package io.gropp.fruehtau.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import cafe.adriel.voyager.navigator.LocalNavigator
import io.gropp.fruehtau.ui.common.AdjustStatusBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(viewModel: SettingsViewModel = hiltViewModel(checkNotNull(LocalViewModelStoreOwner.current), null)) {
    val availableMapPackages by viewModel.availableMaps.collectAsState()
    val activeMapPackage by viewModel.activeMapPackage.collectAsState()
    val availableThemes by viewModel.availableThemes.collectAsState()
    val activeTheme by viewModel.activeTheme.collectAsState()

    AdjustStatusBar(mapMode = false)

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            val navigator = LocalNavigator.current
            Surface(color = MaterialTheme.colorScheme.primary) {
                TopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    title = { Text("Settings") },
                    navigationIcon = {
                        IconButton(onClick = { navigator?.pop() }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier.padding(innerPadding)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            MapSection(
                availableMapPackages = availableMapPackages,
                activeMapPackage = activeMapPackage,
                onSetActiveMapPackage = { viewModel.setActiveMapPackage(it) },
                onDeleteMap = { mapPackageId, mapId -> viewModel.deleteMap(mapPackageId, mapId) },
            )
            ThemeSection(
                availableThemes = availableThemes,
                activeTheme = activeTheme,
                onSetActiveTheme = { viewModel.setActiveTheme(it) },
                onDeleteThemePackage = { packageName -> viewModel.deleteThemePackage(packageName) },
            )
        }
    }
}
