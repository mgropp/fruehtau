package io.gropp.fruehtau.service

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LocationService @Inject constructor(@ApplicationContext appContext: Context) {
    private val fused: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(appContext)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val request =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5_000L).setMinUpdateIntervalMillis(2_000L).build()

    @SuppressLint("MissingPermission")
    private val updates: Flow<Location> = callbackFlow {
        val callback =
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val loc = result.lastLocation ?: return
                    trySend(Location(latitude = loc.latitude, longitude = loc.longitude, accuracy = loc.accuracy))
                }
            }

        fused.requestLocationUpdates(request, callback, null)

        scope.launch {
            runCatching { fused.lastLocation }
                .onSuccess { task ->
                    runCatching {
                        task.addOnSuccessListener { l ->
                            if (l != null) {
                                trySend(Location(l.latitude, l.longitude, l.accuracy))
                            }
                        }
                    }
                }
        }

        awaitClose { fused.removeLocationUpdates(callback) }
    }

    val location: StateFlow<Location?> =
        updates
            .distinctUntilChanged()
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = null,
            )
}
