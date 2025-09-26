package io.gropp.fruehtau.ui.menu

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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MenuViewModel
@Inject
constructor(
    mapRepository: MapRepository,
    themeRepository: ThemeRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    val availableMaps =
        mapRepository.availableMaps.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = emptyList(),
        )

    val availableThemes =
        themeRepository.availableThemes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = emptyList(),
        )

    private val mapPackageId =
        settingsRepository.mapPackageId.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = null,
        )

    fun setMap(mapId: MapPackageId?) {
        viewModelScope.launch { settingsRepository.setMap(mapId) }
    }

    fun setTheme(themeId: ThemeId?) {
        viewModelScope.launch { settingsRepository.setTheme(mapPackageId.value, themeId) }
    }
}
