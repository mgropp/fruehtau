package io.gropp.fruehtau.ui.map

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import io.gropp.fruehtau.R
import io.gropp.fruehtau.service.Location
import io.gropp.fruehtau.service.LocationService
import io.gropp.fruehtau.ui.location.WithLocation
import io.gropp.fruehtau.ui.location.WithLocationPermission
import io.gropp.fruehtau.util.createMarker
import org.mapsforge.core.graphics.Canvas
import org.mapsforge.core.graphics.Style
import org.mapsforge.core.model.BoundingBox
import org.mapsforge.core.model.LatLong
import org.mapsforge.core.model.Point
import org.mapsforge.core.model.Rotation
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.layer.Layer
import org.mapsforge.map.layer.overlay.Circle
import org.mapsforge.map.view.MapView

@Composable
fun LocationIndicator(mapView: MapView, locationService: LocationService) {
    val context = LocalContext.current
    WithLocationPermission {
        val indicator =
            remember(mapView) { LocationIndicatorOverlay(context).also { mapView.layerManager.layers.add(it) } }

        locationService.WithLocation { location ->
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
    private val accuracyCircle = createAccuracyCircle(context)

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
            if (accuracy == null || accuracy < 3.0f) {
                accuracyCircle.isVisible = false
            } else {
                accuracyCircle.setLatLong(latLong)
                accuracyCircle.radius = accuracy
                accuracyCircle.isVisible = true
            }
            requestRedraw()
        }
    }

    private fun createLocationMarker(context: Context) =
        createMarker(context = context, resId = R.drawable.location_marker, offsetX = 0f, offsetY = -0.5f)

    private fun createAccuracyCircle(context: Context) =
        Circle(
            null,
            0f,
            getPaint(ContextCompat.getColor(context, R.color.accuracy_indicator_fill), 0, Style.FILL),
            getPaint(ContextCompat.getColor(context, R.color.accuracy_indicator_stroke), 2, Style.STROKE),
        )
}

private fun getPaint(color: Int, strokeWidth: Int, style: Style?) =
    AndroidGraphicFactory.INSTANCE.createPaint().apply {
        this.color = color
        this.strokeWidth = strokeWidth.toFloat()
        this.setStyle(style)
    }
