package io.gropp.fruehtau.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import timber.log.Timber

@Composable
fun AdjustStatusBar(mapMode: Boolean) {
    val controller = ViewCompat.getWindowInsetsController(LocalView.current)
    if (controller == null) {
        Timber.w("Unable to adjust status bar, no insets controller found")
        return
    }
    val toolbarColor = MaterialTheme.colorScheme.primary

    controller.isAppearanceLightStatusBars = !mapMode && (toolbarColor.luminance() > 0.5f)
}
