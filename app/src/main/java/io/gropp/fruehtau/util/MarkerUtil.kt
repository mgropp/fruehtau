package io.gropp.fruehtau.util

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import kotlin.math.roundToInt
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.layer.overlay.Marker

fun createMarker(context: Context, @DrawableRes resId: Int, offsetX: Float, offsetY: Float): Marker {
    val drawable = requireNotNull(AppCompatResources.getDrawable(context, resId))
    val bitmap = AndroidGraphicFactory.convertToBitmap(drawable)

    val offsetPxX = (bitmap.width * offsetX).roundToInt()
    val offsetPxY = (bitmap.height * offsetY).roundToInt()

    return Marker(null, bitmap, offsetPxX, offsetPxY)
}
