package io.gropp.fruehtau.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.gropp.fruehtau.ui.LoadingScreen
import kotlinx.coroutines.flow.StateFlow

@Composable
fun <T> WhenLoaded(
    stateFlow: StateFlow<T>,
    loadingContent: @Composable () -> Unit = { LoadingScreen() },
    content: @Composable (T & Any) -> Unit,
) {
    val value by stateFlow.collectAsState()
    value?.let { content(it) } ?: loadingContent()
}

@Composable
fun <T1, T2> WhenLoaded(
    stateFlow1: StateFlow<T1>,
    stateFlow2: StateFlow<T2>,
    loadingContent: @Composable () -> Unit = { LoadingScreen() },
    content: @Composable (T1 & Any, T2 & Any) -> Unit,
) {
    val value1 by stateFlow1.collectAsState()
    val value2 by stateFlow2.collectAsState()
    value1?.let { v1 -> value2?.let { v2 -> content(v1, v2) } } ?: loadingContent()
}
