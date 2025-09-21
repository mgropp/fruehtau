package io.gropp.fruehtau.ui

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import io.gropp.fruehtau.ui.screens.MapScreen

@OptIn(ExperimentalVoyagerApi::class)
@Composable
fun Main() {
    AppTheme {
        Navigator(MapScreen()) { navigator ->
            SlideTransition(navigator = navigator, disposeScreenAfterTransitionEnd = true)
        }
    }
}
