package io.gropp.fruehtau.ui.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiNature
import androidx.compose.material.icons.filled.EmojiPeople
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.gropp.fruehtau.ui.AppTheme

@Composable
fun ExpandableMenuSection(
    icon: ImageVector? = null,
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        MenuListItem(
            icon = icon,
            title = title,
            trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                    )
                }
            },
            onClick = onToggle,
        )
        AnimatedVisibility(visible = expanded) {
            Column(
                modifier =
                    Modifier.fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(start = 16.dp, top = 4.dp, bottom = 4.dp)
            ) {
                content()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpandedMenuSectionPreview() {
    AppTheme {
        ExpandableMenuSection(
            icon = Icons.Default.SentimentVerySatisfied,
            title = "Expandable Section",
            expanded = true,
            onToggle = {},
        ) {
            Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
                Column {
                    MenuListItem(Icons.Default.EmojiNature, "More Content") {}
                    MenuListItem(Icons.Default.EmojiPeople, "More Content") {}
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CollapsedMenuSectionPreview() {
    AppTheme {
        ExpandableMenuSection(
            icon = Icons.Default.SentimentVerySatisfied,
            title = "Expandable Section",
            expanded = false,
            onToggle = {},
        ) {}
    }
}
