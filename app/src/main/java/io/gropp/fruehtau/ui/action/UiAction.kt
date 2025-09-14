package io.gropp.fruehtau.ui.action

sealed interface UiAction {
    object ToggleMainMenu : UiAction

    object HideMainMenu : UiAction
}
