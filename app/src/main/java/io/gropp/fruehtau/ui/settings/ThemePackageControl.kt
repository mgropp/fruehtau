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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var showDeleteDialog by remember { mutableStateOf(false) }

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
                        if (packageName != null) {
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete theme package")
                            }
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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Theme Package") },
            text = { Text("Are you sure you want to delete this theme package? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteThemePackage()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } },
        )
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
