package com.ignacnic.architectcoders.data.location

import android.Manifest
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.ignacnic.architectcoders.domain.location.LocationRepository
import com.ignacnic.architectcoders.domain.location.MyLocation
import com.ignacnic.architectcoders.domain.location.toMyLocation
import java.util.concurrent.TimeUnit

class LocationRepositoryImpl(
    private val locationProvider: FusedLocationProviderClient,
) : LocationRepository {

    private var locationCallback: LocationCallback? = null

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun requestLocationUpdates(onResult: (List<MyLocation>) -> Unit) {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                onResult(
                    result.locations.fold(mutableListOf(result.locations[0])) { accList, location ->
                        if (accList.last().distanceTo(location) < accList.last().accuracy){
                            accList
                        } else {
                            accList.apply { add(location) }
                        }
                    }.map { it.toMyLocation() }
                )
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
    }

    override fun removeLocationUpdates() {
        locationCallback?.let {
            locationProvider.removeLocationUpdates(it)
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
        val startingVal = lastLocation ?: this[0]
        return fold(mutableListOf(startingVal)) { acc, location ->
            if (acc.last().hasAccuracy() && acc.last().distanceTo(location)/2 < acc.last().accuracy){
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

    companion object {
        const val REFRESH_INTERVAL_SECONDS = 3L
    }
}
