package io.gropp.fruehtau.ui.location

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.gropp.fruehtau.service.Location
import io.gropp.fruehtau.service.LocationService

@Composable
fun LocationService.WithLocation(content: @Composable (location: Location?) -> Unit) {
    WithLocationPermission {
        val location by location.collectAsState(null)
        content(location)
    }
}
