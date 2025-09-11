package io.gropp.fruehtau.io.preferences

import androidx.datastore.preferences.core.intPreferencesKey

object Keys {
    val PREF_LATITUDE = intPreferencesKey("latitude")
    val PREF_LONGITUDE = intPreferencesKey("longitude")
    val PREF_ZOOM_LEVEL = intPreferencesKey("zoom_level")
}
