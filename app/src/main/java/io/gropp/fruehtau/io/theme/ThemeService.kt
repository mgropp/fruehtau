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
        combine(themeRepository.availableThemes, settingsRepository.themeIds, settingsRepository.mapPackageId) {
            themes,
            themeIds,
            mapPackageId ->
            themeRepository.loadThemeOrDefault(themeIds?.get(mapPackageId) ?: themeIds?.get(null))
        }
}
