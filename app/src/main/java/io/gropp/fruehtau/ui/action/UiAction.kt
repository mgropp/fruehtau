package io.gropp.fruehtau.ui.action

import io.gropp.fruehtau.io.theme.ThemeId

sealed interface UiAction {
    object ToggleMainMenu : UiAction

    object HideMainMenu : UiAction

    @JvmInline value class SetMapTheme(val themeId: ThemeId) : UiAction
}
