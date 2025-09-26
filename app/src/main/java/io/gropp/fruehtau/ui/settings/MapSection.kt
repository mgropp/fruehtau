package io.gropp.fruehtau.ui.settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.gropp.fruehtau.io.map.MapPackage
import io.gropp.fruehtau.io.map.MapPackageId
import io.gropp.fruehtau.ui.AppTheme
import io.gropp.fruehtau.ui.common.SectionTitle

@Composable
fun MapSection(
    availableMapPackages: List<MapPackage>,
    activeMapPackage: MapPackageId?,
    onSetActiveMapPackage: (MapPackageId) -> Unit,
    onDeleteMap: (MapPackageId, String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionTitle("Map Packages")
        if (availableMapPackages.isEmpty()) {
            Text("No map packages installed.")
        } else {
            availableMapPackages.forEach { mapPackage ->
                MapPackageControl(
                    active = mapPackage.id == activeMapPackage,
                    onSetActive = { onSetActiveMapPackage(mapPackage.id) },
                    mapPackage = mapPackage,
                    onDeleteMap = { mapId -> onDeleteMap(mapPackage.id, mapId) },
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun MapPackageSectionPreviewLight() {
    MapPackageSectionPreview()
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MapPackageSectionPreviewDark() {
    MapPackageSectionPreview()
}

@Composable
private fun MapPackageSectionPreview() {
    AppTheme {
        MapSection(
            availableMapPackages =
                listOf(
                    MapPackage(id = MapPackageId("OpenAndroMaps"), maps = listOf("Bayern", "Berlin")),
                    MapPackage(id = MapPackageId("OpenHiking"), maps = listOf("Alps East", "Alps West")),
                ),
            activeMapPackage = MapPackageId("OpenAndroMaps"),
            onSetActiveMapPackage = {},
            onDeleteMap = { _, _ -> },
        )
    }
}
