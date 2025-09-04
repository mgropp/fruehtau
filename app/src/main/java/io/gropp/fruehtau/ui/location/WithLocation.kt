package io.gropp.fruehtau.ui.location

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.gropp.fruehtau.service.Location
import io.gropp.fruehtau.service.LocationService
import io.gropp.fruehtau.ui.map.WithLocationPermission

@Composable
fun WithLocation(locationService: LocationService, content: @Composable (location: Location?) -> Unit) {
    WithLocationPermission {
        val location by locationService.location.collectAsState(null)
        content(location)
    }
}
