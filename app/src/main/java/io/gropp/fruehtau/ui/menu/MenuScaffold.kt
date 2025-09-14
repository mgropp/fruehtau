package io.gropp.fruehtau.ui.menu

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.gropp.fruehtau.ui.action.UiAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScaffold(content: @Composable (onUiAction: (action: UiAction) -> Unit) -> Unit) {
    var mainMenuVisible by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    fun onUiAction(action: UiAction) {
        when (action) {
            UiAction.ToggleMainMenu -> mainMenuVisible = !mainMenuVisible

            UiAction.HideMainMenu -> mainMenuVisible = false
        }
    }

    content(::onUiAction)

    if (mainMenuVisible) {
        ModalBottomSheet(onDismissRequest = { onUiAction(UiAction.HideMainMenu) }, sheetState = sheetState) {
            Menu(::onUiAction)
        }
    }
}
