package io.gropp.fruehtau.io.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import io.gropp.fruehtau.io.map.MapId
import io.gropp.fruehtau.io.theme.ThemeId
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

@Singleton
class SettingsRepository @Inject constructor(@param:ApplicationContext private val context: Context) {
    private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val dataStore: DataStore<Preferences> = context.settingsDataStore

    val settings: Flow<Settings> =
        dataStore.data
            .catch { e ->
                if (e is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw e
                }
            }
            .map { it.toSettings() }

    val mapId = settings.map { it.mapId }

    val themeId = settings.map { it.themeId }

    suspend fun setMap(mapId: MapId?) {
        dataStore.edit { prefs ->
            if (mapId != null) {
                prefs[PREF_MAP_PACKAGE] = mapId.packageName
                if (mapId.mapName != null) {
                    prefs[PREF_MAP_NAME] = mapId.mapName
                } else {
                    prefs.remove(PREF_MAP_NAME)
                }
            } else {
                prefs.remove(PREF_MAP_PACKAGE)
                prefs.remove(PREF_MAP_NAME)
            }
        }
    }

    suspend fun setTheme(themeId: ThemeId?) {
        dataStore.edit { prefs ->
            if (themeId != null) {
                if (themeId.packageName != null) {
                    prefs[PREF_THEME_PACKAGE] = themeId.packageName
                } else {
                    prefs.remove(PREF_THEME_PACKAGE)
                }
                prefs[PREF_THEME_NAME] = themeId.themeName
            } else {
                prefs.remove(PREF_THEME_PACKAGE)
                prefs.remove(PREF_THEME_NAME)
            }
        }
    }

    private fun Preferences.toSettings(): Settings {
        val mapPackage = this[PREF_MAP_PACKAGE]
        val mapName = this[PREF_MAP_NAME]
        val themePackage = this[PREF_THEME_PACKAGE]
        val themeName = this[PREF_THEME_NAME]

        return Settings(
            mapId =
                if (mapPackage != null) {
                    MapId(mapPackage, mapName)
                } else {
                    null
                },
            themeId =
                if (themeName != null) {
                    ThemeId(themePackage, themeName)
                } else {
                    null
                },
        )
    }

    companion object {
        private val PREF_MAP_PACKAGE = stringPreferencesKey("map_package")
        private val PREF_MAP_NAME = stringPreferencesKey("map_name")
        private val PREF_THEME_PACKAGE = stringPreferencesKey("theme_package")
        private val PREF_THEME_NAME = stringPreferencesKey("theme_name")
    }
}
