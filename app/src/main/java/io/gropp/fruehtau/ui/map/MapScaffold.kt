package io.gropp.fruehtau.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MapScaffold(
    topBarContent: @Composable (RowScope.() -> Unit)? = null,
    bottomBarContent: @Composable (RowScope.() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        content()

        if (topBarContent != null) {
            Toolbar(Alignment.TopCenter) { topBarContent() }
        }

        if (bottomBarContent != null) {
            Toolbar(Alignment.BottomCenter) { bottomBarContent() }
        }
    }
}

@Preview
@Composable
private fun MapScaffoldPreview() {
    Box(Modifier.height(240.dp)) {
        MapScaffold(
            topBarContent = {
                IconButton(onClick = {}) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                }
            },
            bottomBarContent = {
                Spacer(Modifier.weight(1f))
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Center on location",
                        tint = Color.White,
                    )
                }
            },
            content = {
                Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(0f to Color.Blue, 1f to Color.Green)))
            },
        )
    }
}
