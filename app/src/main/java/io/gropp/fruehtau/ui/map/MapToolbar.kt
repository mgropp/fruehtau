package io.gropp.fruehtau.ui.map

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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
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
    alignment: Alignment,
    modifier: Modifier = Modifier,
    height: Dp = 56.dp,
    alpha: Float = 0.5f,
    content: @Composable RowScope.() -> Unit = {},
) {
    Box(
        modifier
            .align(alignment)
            .fillMaxWidth()
            .height(height)
            .padding(WindowInsets.navigationBars.asPaddingValues())
            .background(Color.Black.copy(alpha = alpha))
    ) {
        Row(
            Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            content = content,
        )
    }
}

@Preview
@Composable
private fun MapToolbarPreview() {
    Box(Modifier.height(120.dp).background(Brush.horizontalGradient(0f to Color.Blue, 1f to Color.Red))) {
        Toolbar(Alignment.TopCenter) {
            Icon(imageVector = Icons.Default.Map, contentDescription = "Center on location", tint = Color.White)
        }
    }
}
