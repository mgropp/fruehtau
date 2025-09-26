package io.gropp.fruehtau.io.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import io.gropp.fruehtau.io.map.MapPackageId
import io.gropp.fruehtau.io.theme.ThemeId
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber

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

    val mapPackageId = settings.map { it.mapPackageId }

    val themeIds = settings.map { it.themeIds }

    suspend fun setMap(mapId: MapPackageId?) {
        dataStore.edit { prefs ->
            if (mapId != null) {
                prefs[PREF_MAP_PACKAGE] = mapId.value
            } else {
                prefs.remove(PREF_MAP_PACKAGE)
            }
        }
    }

    suspend fun setTheme(mapPackageId: MapPackageId?, themeId: ThemeId?) {
        Timber.i("Changing theme to $themeId")
        dataStore.edit { prefs ->
            prefs.setTheme(mapPackageId, themeId)
            if (mapPackageId != null) {
                prefs.setTheme(null, themeId)
            }
        }
    }

    private fun MutablePreferences.setTheme(mapPackageId: MapPackageId?, themeId: ThemeId?) {
        if (themeId != null) {
            if (themeId.packageName != null) {
                this[getThemePackageKey(mapPackageId)] = themeId.packageName
            } else {
                remove(getThemePackageKey(mapPackageId))
            }
            this[getThemeNameKey(mapPackageId)] = themeId.themeName
        } else {
            remove(getThemePackageKey(mapPackageId))
            remove(getThemeNameKey(mapPackageId))
        }
    }

    private fun Preferences.toSettings() = Settings(mapPackageId = getMapPackageId(), themeIds = getThemeIds())

    private fun Preferences.getMapPackageId() = this[PREF_MAP_PACKAGE]?.let { MapPackageId(it) }

    private fun Preferences.getThemeIds(): Map<MapPackageId?, ThemeId> {
        val mapPackageIds = asMap().keys.filter(::isThemeNameKey).map(::getMapPackageIdFromThemeNameKey).toSet()

        return mapPackageIds
            .mapNotNull { mapPackageId ->
                this[getThemeNameKey(mapPackageId)]?.let { themeName ->
                    mapPackageId to ThemeId(this[getThemePackageKey(mapPackageId)], themeName)
                }
            }
            .toMap()
    }

    companion object {
        private val PREF_MAP_PACKAGE = stringPreferencesKey("map_package")
        private const val PREF_THEME_PACKAGE_PREFIX = "theme_package:"
        private const val PREF_THEME_NAME_PREFIX = "theme_name:"

        private fun isThemeNameKey(key: Preferences.Key<*>) = key.name.startsWith(PREF_THEME_NAME_PREFIX)

        private fun getMapPackageIdFromThemeNameKey(key: Preferences.Key<*>) =
            key.name.removePrefixOrNull(PREF_THEME_NAME_PREFIX)?.takeIf(String::isNotEmpty)?.let(::MapPackageId)

        private fun getThemePackageKey(mapPackageId: MapPackageId?) =
            stringPreferencesKey("$PREF_THEME_PACKAGE_PREFIX${mapPackageId?.value ?: ""}")

        private fun getThemeNameKey(mapPackageId: MapPackageId?) =
            stringPreferencesKey("$PREF_THEME_NAME_PREFIX${mapPackageId?.value ?: ""}")

        private fun String.removePrefixOrNull(prefix: String): String? =
            if (startsWith(prefix)) substring(prefix.length) else null
    }
}
