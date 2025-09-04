package io.gropp.fruehtau.io

import android.content.Context
import io.gropp.fruehtau.di.IoDispatcher
import io.gropp.fruehtau.io.test.loadTestMapFile
import io.gropp.fruehtau.util.DynamicData
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.mapsforge.map.datastore.MapDataStore

@Singleton
class MapRepository @Inject constructor(@param:IoDispatcher private val ioDispatcher: CoroutineDispatcher) {
    private val _state = MutableStateFlow<DynamicData<MapDataStore>>(DynamicData.Empty)
    val state = _state.asStateFlow()

    suspend fun ensureLoaded(context: Context) {
        if (_state.compareAndSet(DynamicData.Empty, DynamicData.Loading)) {
            withContext(ioDispatcher) {
                val mapDataStore = loadTestMapFile(context)
                _state.value = DynamicData.Loaded(mapDataStore)
            }
        }
    }
}
