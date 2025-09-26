package io.gropp.fruehtau.ui.settings

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.gropp.fruehtau.io.theme.ThemeId
import io.gropp.fruehtau.ui.AppTheme
import io.gropp.fruehtau.ui.common.MenuListItem

@Composable
fun ThemePackageControl(
    packageName: String?,
    themes: List<ThemeId>,
    activeTheme: ThemeId?,
    onSetActiveTheme: (ThemeId) -> Unit,
    onDeleteThemePackage: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column {
            Row(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)) {
                MenuListItem(
                    icon = Icons.Filled.Palette,
                    title = packageName ?: "Built-In Themes",
                    trailingContent = {
                        IconButton(onClick = { onDeleteThemePackage() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete theme package")
                        }
                    },
                )
            }
            themes.forEach { theme ->
                Row(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceBright)) {
                    RadioButton(
                        selected = activeTheme == theme,
                        onClick = { onSetActiveTheme(theme) },
                        modifier = Modifier.padding(top = 4.dp),
                    )
                    MenuListItem(
                        icon = Icons.Outlined.Palette,
                        title = theme.title,
                        onClick = { onSetActiveTheme(theme) },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun ThemeControlPreview() {
    AppTheme {
        ThemePackageControl(
            packageName = "Sample Theme Package",
            themes =
                listOf(ThemeId("Sample Theme Package", "Light Theme"), ThemeId("Sample Theme Package", "Dark Theme")),
            activeTheme = ThemeId("Sample Theme Package", "Dark Theme"),
            onSetActiveTheme = {},
            onDeleteThemePackage = {},
        )
    }
}
