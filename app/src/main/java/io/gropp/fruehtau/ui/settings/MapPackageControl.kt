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
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.Map
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
import io.gropp.fruehtau.io.map.MapPackage
import io.gropp.fruehtau.io.map.MapPackageId
import io.gropp.fruehtau.ui.AppTheme
import io.gropp.fruehtau.ui.common.MenuListItem

@Composable
fun MapPackageControl(mapPackage: MapPackage, active: Boolean, onSetActive: () -> Unit, onDeleteMap: (String) -> Unit) {
    var confirmDeletionOfMap by remember { mutableStateOf<String?>(null) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column {
            Row(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)) {
                RadioButton(selected = active, onClick = onSetActive, modifier = Modifier.padding(top = 4.dp))
                MenuListItem(icon = Icons.Filled.Map, title = mapPackage.id.value, onClick = onSetActive)
            }

            mapPackage.maps.forEach { mapId ->
                Row(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceBright)) {
                    MenuListItem(
                        icon = Icons.Outlined.Map,
                        title = mapId,
                        trailingContent = {
                            IconButton(onClick = { confirmDeletionOfMap = mapId }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete map")
                            }
                        },
                    )
                }
            }
        }
    }

    confirmDeletionOfMap?.let { mapId ->
        AlertDialog(
            onDismissRequest = { confirmDeletionOfMap = null },
            title = { Text("Delete Map") },
            text = { Text("Are you sure you want to delete this map? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteMap(mapId)
                        confirmDeletionOfMap = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = { TextButton(onClick = { confirmDeletionOfMap = null }) { Text("Cancel") } },
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun MapPackageControlPreview() {
    AppTheme {
        MapPackageControl(
            active = true,
            onSetActive = {},
            mapPackage = MapPackage(id = MapPackageId("OpenAndroMaps"), maps = listOf("Bayern", "Berlin")),
            onDeleteMap = {},
        )
    }
}
