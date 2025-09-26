package io.gropp.fruehtau.io.map

import io.gropp.fruehtau.io.preferences.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.mapsforge.map.datastore.MapDataStore

@Singleton
class MapService @Inject constructor(mapRepository: MapRepository, settingsRepository: SettingsRepository) {
    val mapDataStore: Flow<MapDataStore?> =
        combine(mapRepository.availableMaps, settingsRepository.mapPackageId) { maps, id ->
            mapRepository.loadMapOrDefault(id)
        }
}
