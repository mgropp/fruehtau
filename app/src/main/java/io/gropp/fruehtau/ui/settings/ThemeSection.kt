package io.gropp.fruehtau.ui.settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.gropp.fruehtau.io.theme.ThemeId
import io.gropp.fruehtau.ui.AppTheme
import io.gropp.fruehtau.ui.common.SectionTitle

@Composable
fun ThemeSection(
    availableThemes: List<ThemeId>,
    activeTheme: ThemeId?,
    onSetActiveTheme: (ThemeId) -> Unit,
    onDeleteThemePackage: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle("Themes")
        availableThemes
            .groupBy { it.packageName }
            .forEach { (packageName, themes) ->
                ThemePackageControl(
                    packageName = packageName,
                    themes = themes,
                    activeTheme = activeTheme,
                    onSetActiveTheme = onSetActiveTheme,
                    onDeleteThemePackage = { packageName?.let(onDeleteThemePackage) },
                )
            }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun ThemeSectionPreviewLight() {
    ThemeSectionPreview()
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ThemeSectionPreviewDark() {
    ThemeSectionPreview()
}

@Composable
private fun ThemeSectionPreview() {
    AppTheme {
        ThemeSection(
            availableThemes =
                listOf(
                    ThemeId(null, "Biker"),
                    ThemeId(null, "Default"),
                    ThemeId(null, "Osmarender"),
                    ThemeId("Elevate", "Hiking"),
                ),
            activeTheme = ThemeId(null, "Default"),
            onSetActiveTheme = {},
            onDeleteThemePackage = {},
        )
    }
}
