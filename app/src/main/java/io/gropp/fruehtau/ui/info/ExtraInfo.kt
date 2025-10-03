package io.gropp.fruehtau.ui.info

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.gropp.fruehtau.service.LocationService

@Composable
fun ExtraInfo(locationService: LocationService, modifier: Modifier = Modifier) {
    Row {
        SunExtraInfo(locationService, modifier)
        Spacer(Modifier.width(20.dp))
        LocationExtraInfo(locationService, modifier)
    }
}
