package io.gropp.fruehtau.ui.menu

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import io.gropp.fruehtau.ui.AppTheme

@Composable
fun MenuListItem(
    icon: ImageVector? = null,
    title: String,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(title) },
        leadingContent = icon?.let { { Icon(it, null) } },
        trailingContent = trailingContent,
        modifier = Modifier.Companion.fillMaxWidth().clickable(onClick = onClick),
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun MenuListItemPreviewLight() {
    AppTheme {
        MenuListItem(
            icon = Icons.Default.Settings,
            title = "Settings",
            trailingContent = { Icon(Icons.Default.ChevronRight, null) },
            onClick = {},
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MenuListItemPreviewDark() {
    AppTheme {
        MenuListItem(
            icon = Icons.Default.Settings,
            title = "Settings",
            trailingContent = { Icon(Icons.Default.ChevronRight, null) },
            onClick = {},
        )
    }
}
