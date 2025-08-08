package com.ignacnic.architectcoders.businesslogic.location.data

import android.Manifest
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.ignacnic.architectcoders.businesslogic.location.domain.LocationRepository
import com.ignacnic.architectcoders.entities.location.MyLocation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

class LocationRepositoryImpl(
    private val locationProvider: FusedLocationProviderClient,
) : LocationRepository {

    internal var locationCallback: LocationCallback? = null

    private var lastLocation: Location? = null

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun startLocationUpdates(): Flow<List<MyLocation>> = callbackFlow {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                trySend(result.locations.filterLocations().map { it.toMyLocation() })
            }
        }
        locationCallback?.let {
            locationProvider.requestLocationUpdates(
                LocationRequest.Builder(
                    /*priority*/ Priority.PRIORITY_HIGH_ACCURACY,
                    /*intervalMillis*/ TimeUnit.SECONDS.toMillis(REFRESH_INTERVAL_SECONDS),
                ).build(),
                it,
                Looper.getMainLooper()
            )
        }

        awaitClose {
            stopLocationUpdates()
        }
    }

    override fun stopLocationUpdates() {
        locationCallback?.let {
            locationProvider.removeLocationUpdates(it)
            locationCallback = null
            lastLocation = null
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override suspend fun requestSingleLocation(onResult: (MyLocation?) -> Unit) {
        locationProvider
            .getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token,
            )
            .addOnCompleteListener {
                onResult(
                    if (it.isSuccessful) {
                        it.result?.toMyLocation()
                    }
                    else {
                        null
                    }
                )
            }
    }

    private fun List<Location>.filterLocations(): List<Location> {
        val startingVal = lastLocation?: get(0)
        val startingIdx = if (lastLocation != null) -1 else 0
        return foldIndexed(mutableListOf(startingVal)) { i, acc, location ->
            if (
                startingIdx == i
                || acc.last().hasAccuracy()
                && acc.last().distanceTo(location)/2 < acc.last().accuracy
            ){
                acc
            } else {
                acc.apply { add(location) }
            }
        }.apply {
            if (lastLocation != null) {
                removeAt(0)
            } else {
                lastLocation = last()
            }
        }
    }


    private fun Location.toMyLocation() = MyLocation(
        latitude = latitude,
        longitude = longitude,
        timeStamp = time.milliseconds.inWholeMilliseconds,
        elevation = if (hasAltitude()) altitude else null
    )

    companion object {
        const val REFRESH_INTERVAL_SECONDS = 5L
    }
}
