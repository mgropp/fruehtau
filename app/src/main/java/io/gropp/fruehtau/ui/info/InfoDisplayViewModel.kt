package io.gropp.fruehtau.ui.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.gropp.fruehtau.io.preferences.InfoDisplayMode
import io.gropp.fruehtau.io.preferences.SettingsRepository
import io.gropp.fruehtau.service.LocationService
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class InfoDisplayViewModel
@Inject
constructor(val locationService: LocationService, private val settingsRepository: SettingsRepository) : ViewModel() {
    val infoDisplayMode =
        settingsRepository.infoDisplayMode.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    fun setInfoDisplayMode(mode: InfoDisplayMode) {
        viewModelScope.launch { settingsRepository.setInfoDisplayMode(mode) }
    }
}
