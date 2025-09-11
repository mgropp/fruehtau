package io.gropp.fruehtau.io

import android.content.Context
import android.net.Uri
import io.gropp.fruehtau.di.IoDispatcher
import io.gropp.fruehtau.io.test.loadTestThemes
import io.gropp.fruehtau.util.DynamicData
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.mapsforge.map.rendertheme.XmlRenderTheme
import timber.log.Timber

@Singleton
class ThemeRepository @Inject constructor(@param:IoDispatcher private val ioDispatcher: CoroutineDispatcher) {
    private val _state = MutableStateFlow<DynamicData<Map<String, XmlRenderTheme>>>(DynamicData.Empty)
    val state = _state.asStateFlow()

    suspend fun ensureLoaded(context: Context) {
        if (_state.compareAndSet(DynamicData.Empty, DynamicData.Loading)) {
            withContext(ioDispatcher) {
                val themes = loadTestThemes(context)
                _state.value = DynamicData.Loaded(themes)
            }
        }
    }

    suspend fun importFromUri(uri: Uri) {
        withContext(ioDispatcher) { Timber.i("Importing theme from URI: $uri") }
    }
}
