package io.gropp.fruehtau.ui.map

import android.content.Context
import io.gropp.fruehtau.io.map.MapService
import io.gropp.fruehtau.io.theme.ThemeService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.datastore.MapDataStore
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.model.DisplayModel
import org.mapsforge.map.model.FrameBufferModel
import org.mapsforge.map.model.MapViewPosition
import org.mapsforge.map.rendertheme.XmlRenderTheme
import timber.log.Timber

fun getTileRendererLayerFlow(
    mapService: MapService,
    themeService: ThemeService,
    context: Context,
    ioDispatcher: CoroutineDispatcher,
): Flow<TileRendererLayer?> {
    suspend fun createTileLayer(
        context: Context,
        mapDataStore: MapDataStore,
        theme: XmlRenderTheme,
    ): TileRendererLayer =
        withContext(ioDispatcher) {
            Timber.i("Creating TileRendererLayer")
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

    return combine(mapService.mapDataStore, themeService.theme) { mapDataStore, theme ->
        if (mapDataStore == null || theme == null) {
            null
        } else {
            createTileLayer(context, mapDataStore, theme)
        }
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
