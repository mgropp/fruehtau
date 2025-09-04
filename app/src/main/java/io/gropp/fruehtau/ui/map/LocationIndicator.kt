package io.gropp.fruehtau.ui.map

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
import io.gropp.fruehtau.service.Location
import io.gropp.fruehtau.ui.location.WithLocation
import org.mapsforge.core.graphics.Canvas
import org.mapsforge.core.graphics.Style
import org.mapsforge.core.model.BoundingBox
import org.mapsforge.core.model.LatLong
import org.mapsforge.core.model.Point
import org.mapsforge.core.model.Rotation
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.layer.Layer
import org.mapsforge.map.layer.overlay.Circle
import org.mapsforge.map.layer.overlay.Marker
import org.mapsforge.map.view.MapView

@Composable
fun LocationIndicator(mapView: MapView, viewModel: MapViewModel) {
    val context = LocalContext.current
    WithLocationPermission {
        val indicator = remember { LocationIndicatorOverlay(context) }
        LaunchedEffect(Unit) { mapView.layerManager.layers.add(indicator) }

        WithLocation(viewModel.locationService) { location ->
            if (location != null) {
                indicator.setLocation(location)
                indicator.isVisible = true
            } else {
                indicator.isVisible = false
            }
        }
    }
}

class LocationIndicatorOverlay(context: Context) : Layer() {
    private val locationMarker = createLocationMarker(context)
    private val accuracyCircle = createAccuracyCircle()

    override fun draw(
        boundingBox: BoundingBox?,
        zoomLevel: Byte,
        canvas: Canvas?,
        topLeftPoint: Point?,
        rotation: Rotation?,
    ) {
        synchronized(this) {
            accuracyCircle.draw(boundingBox, zoomLevel, canvas, topLeftPoint, Rotation.NULL_ROTATION)
            locationMarker.draw(boundingBox, zoomLevel, canvas, topLeftPoint, rotation)
        }
    }

    override fun onAdd() {
        accuracyCircle.setDisplayModel(this.displayModel)
        locationMarker.setDisplayModel(this.displayModel)
    }

    override fun onDestroy() {
        accuracyCircle.onDestroy()
        locationMarker.onDestroy()
    }

    fun setLocation(location: Location) = setLocation(location.latitude, location.longitude, location.accuracy)

    fun setLocation(latitude: Double, longitude: Double, accuracy: Float?) {
        synchronized(this) {
            val latLong = LatLong(latitude, longitude)
            locationMarker.latLong = latLong
            if (accuracy == null) {
                accuracyCircle.isVisible = false
            } else {
                accuracyCircle.setLatLong(latLong)
                accuracyCircle.radius = accuracy
                accuracyCircle.isVisible = true
            }
            requestRedraw()
        }
    }
}

private fun getPaint(color: Int, strokeWidth: Int, style: Style?) =
    AndroidGraphicFactory.INSTANCE.createPaint().apply {
        this.color = color
        this.strokeWidth = strokeWidth.toFloat()
        this.setStyle(style)
    }

private fun createLocationMarker(context: Context): Marker {
    val radius = 20
    val paint =
        Paint().apply {
            isAntiAlias = true
            color = Color.BLUE
            style = Paint.Style.FILL
        }

    val size = radius * 2

    val bitmap = createBitmap(size, size)
    val canvas = android.graphics.Canvas(bitmap)
    canvas.drawCircle(radius.toFloat(), radius.toFloat(), radius.toFloat(), paint)

    return Marker(null, AndroidGraphicFactory.convertToBitmap(bitmap.toDrawable(context.resources)), 0, 0)
}

private fun createAccuracyCircle() =
    Circle(
        null,
        0f,
        getPaint(AndroidGraphicFactory.INSTANCE.createColor(48, 0, 0, 255), 0, Style.FILL),
        getPaint(AndroidGraphicFactory.INSTANCE.createColor(160, 0, 0, 255), 2, Style.STROKE),
    )
