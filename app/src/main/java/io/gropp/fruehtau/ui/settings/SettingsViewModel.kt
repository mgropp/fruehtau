package io.gropp.fruehtau.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.gropp.fruehtau.io.map.MapPackageId
import io.gropp.fruehtau.io.map.MapRepository
import io.gropp.fruehtau.io.preferences.SettingsRepository
import io.gropp.fruehtau.io.theme.ThemeId
import io.gropp.fruehtau.io.theme.ThemeRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel
@Inject
constructor(
    private val settingsRepository: SettingsRepository,
    private val mapRepository: MapRepository,
    private val themeRepository: ThemeRepository,
) : ViewModel() {
    val activeMapPackage =
        settingsRepository.mapPackageId.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    val activeTheme =
        combine(settingsRepository.mapPackageId, settingsRepository.themeIds) { mapPackageId, themeIds ->
                themeIds?.get(mapPackageId)
            }
            .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = null)

    val availableMaps =
        mapRepository.availableMaps.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    val availableThemes =
        themeRepository.availableThemes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    fun setActiveMapPackage(mapPackageId: MapPackageId) {
        viewModelScope.launch { settingsRepository.setMap(mapPackageId) }
    }

    fun setActiveTheme(theme: ThemeId) {
        viewModelScope.launch { settingsRepository.setTheme(activeMapPackage.value, theme) }
    }

    fun deleteMap(mapPackageId: MapPackageId, mapId: String) {
        viewModelScope.launch { mapRepository.deleteMap(mapPackageId, mapId) }
    }

    fun deleteThemePackage(packageName: String) {
        if (packageName != activeTheme.value?.packageName) {
            viewModelScope.launch { themeRepository.deleteThemePackage(packageName) }
        }
    }
}
