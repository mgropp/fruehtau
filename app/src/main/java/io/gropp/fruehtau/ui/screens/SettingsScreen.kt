package io.gropp.fruehtau.ui.screens

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import io.gropp.fruehtau.ui.settings.SettingsView

class SettingsScreen : Screen {
    @Composable
    override fun Content() {
        SettingsView()
    }
}
