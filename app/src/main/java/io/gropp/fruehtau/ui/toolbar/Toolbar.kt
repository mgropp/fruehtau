package io.gropp.fruehtau.ui.toolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BoxScope.Toolbar(
    alignment: Alignment.Vertical,
    modifier: Modifier = Modifier,
    height: Dp = 48.dp,
    alpha: Float = 0.5f,
    content: @Composable RowScope.() -> Unit = {},
) {
    Box(
        modifier.align(alignment.center).fillMaxWidth().background(Color.Black.copy(alpha = alpha)).run {
            if (alignment == Alignment.Top) {
                val statusBars = WindowInsets.statusBars.asPaddingValues()
                padding(top = statusBars.calculateTopPadding())
            } else this
        }
    ) {
        Row(
            Modifier.height(height).fillMaxSize().padding(horizontal = 8.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
            content = content,
        )
    }
}

private val Alignment.Vertical.center: Alignment
    get() =
        when (this) {
            Alignment.Top -> Alignment.TopCenter
            Alignment.Center -> Alignment.Center
            Alignment.Bottom -> Alignment.BottomCenter
            else -> throw IllegalArgumentException("Invalid vertical alignment")
        }

@Preview
@Composable
private fun MapToolbarPreview() {
    Box(Modifier.height(120.dp).background(Brush.horizontalGradient(0f to Color.Blue, 1f to Color.Red))) {
        Toolbar(Alignment.Top) {
            Icon(imageVector = Icons.Default.Map, contentDescription = "Center on location", tint = Color.White)
        }
    }
}
