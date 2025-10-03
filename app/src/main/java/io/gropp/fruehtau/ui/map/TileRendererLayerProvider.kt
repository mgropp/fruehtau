package io.gropp.fruehtau.ui.map

import android.content.Context
import io.gropp.fruehtau.io.map.MapService
import io.gropp.fruehtau.io.theme.ThemeService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.datastore.MapDataStore
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.model.DisplayModel
import org.mapsforge.map.model.FrameBufferModel
import org.mapsforge.map.model.MapViewPosition
import org.mapsforge.map.rendertheme.XmlRenderTheme

class TileRendererLayerProvider
private constructor(
    private val context: Context,
    private val mapDataStore: MapDataStore,
    private val theme: XmlRenderTheme,
) {
    private var _instance: TileRendererLayer? = null

    val instance: TileRendererLayer
        get() = _instance ?: create()

    fun clear() {
        _instance = null
    }

    private fun create(): TileRendererLayer {
        val displayModel = DisplayModel()
        val frameBufferModel = FrameBufferModel()
        val mapViewPosition = MapViewPosition(displayModel)
        val androidGraphicFactory = AndroidGraphicFactory.INSTANCE

        val tileCache =
            AndroidUtil.createTileCache(context, "cache", displayModel.tileSize, 1f, frameBufferModel.overdrawFactor)

        return TileRendererLayer(tileCache, mapDataStore, mapViewPosition, androidGraphicFactory).apply {
            setXmlRenderTheme(theme)
            _instance = this
        }
    }

    companion object {
        fun createFlow(
            mapService: MapService,
            themeService: ThemeService,
            context: Context,
        ): Flow<TileRendererLayerProvider?> =
            combine(mapService.mapDataStore, themeService.theme) { mapDataStore, theme ->
                if (mapDataStore == null || theme == null) {
                    null
                } else {
                    TileRendererLayerProvider(context, mapDataStore, theme)
                }
            }
    }
}
