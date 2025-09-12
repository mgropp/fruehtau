package io.gropp.fruehtau.io.theme

import io.gropp.fruehtau.io.preferences.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.mapsforge.map.rendertheme.XmlRenderTheme

@Singleton
class ThemeService @Inject constructor(themeRepository: ThemeRepository, settingsRepository: SettingsRepository) {
    val theme: Flow<XmlRenderTheme?> =
        combine(themeRepository.availableThemes, settingsRepository.themeId) { themes, id ->
            themeRepository.loadThemeOrDefault(id)
        }
}
