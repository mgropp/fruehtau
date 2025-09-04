package io.gropp.fruehtau.ui.map

import android.content.Context
import androidx.lifecycle.viewModelScope
import io.gropp.fruehtau.util.DynamicData
import io.gropp.fruehtau.util.combineDynamicData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.datastore.MapDataStore
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.model.DisplayModel
import org.mapsforge.map.model.FrameBufferModel
import org.mapsforge.map.model.MapViewPosition
import org.mapsforge.map.rendertheme.XmlRenderTheme
import org.mapsforge.map.rendertheme.internal.MapsforgeThemes

class TileRendererLayerLoader(mapViewModel: MapViewModel, private val ioDispatcher: CoroutineDispatcher) {
    private val themeNameState = MutableStateFlow<DynamicData<String?>>(DynamicData.Loaded(null))
    private val contextState = MutableStateFlow<DynamicData<Context>>(DynamicData.Empty)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<DynamicData<TileRendererLayer>> =
        combineDynamicData(
                mapViewModel.mapRepository.state,
                mapViewModel.themeRepository.state,
                themeNameState,
                contextState,
            ) { mapDataStore, themes, themeName, context ->
                val theme =
                    (if (themeName == null) themes.values.firstOrNull() else themes[themeName])
                        ?: MapsforgeThemes.DEFAULT
                createTileLayer(context, mapDataStore, theme)
            }
            .stateIn(
                scope = mapViewModel.viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = DynamicData.Empty,
            )

    fun ensureLoaded(context: Context) {
        this.contextState.value = DynamicData.Loaded(context)
    }

    private suspend fun createTileLayer(
        context: Context,
        mapDataStore: MapDataStore,
        theme: XmlRenderTheme,
    ): TileRendererLayer =
        withContext(ioDispatcher) {
            val displayModel = DisplayModel()
            createTileLayer(
                context,
                mapDataStore,
                theme,
                displayModel,
                FrameBufferModel(),
                MapViewPosition(displayModel),
                AndroidGraphicFactory.INSTANCE,
            )
        }
}

private fun createTileLayer(
    context: Context,
    mapDataStore: MapDataStore,
    theme: XmlRenderTheme,
    displayModel: DisplayModel,
    frameBufferModel: FrameBufferModel,
    mapViewPosition: MapViewPosition,
    androidGraphicFactory: AndroidGraphicFactory,
): TileRendererLayer {
    val tileCache =
        AndroidUtil.createTileCache(context, "cache", displayModel.tileSize, 1f, frameBufferModel.overdrawFactor)
    val layer = TileRendererLayer(tileCache, mapDataStore, mapViewPosition, androidGraphicFactory)

    layer.setXmlRenderTheme(theme)

    return layer
}
