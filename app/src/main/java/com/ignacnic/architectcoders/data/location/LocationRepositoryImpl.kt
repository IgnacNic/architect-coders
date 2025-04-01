package com.ignacnic.architectcoders.data.location

import android.Manifest
import android.content.Context
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.ignacnic.architectcoders.domain.location.LocationRepository
import com.ignacnic.architectcoders.domain.location.MyLocation
import com.ignacnic.architectcoders.domain.location.toMyLocation
import java.util.concurrent.TimeUnit

class LocationRepositoryImpl(context: Context) : LocationRepository {

    private val locationProvider: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var locationCallback: LocationCallback? = null

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun requestLocationUpdates(onResult: (List<MyLocation>) -> Unit) {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                onResult(result.locations.map { it.toMyLocation() })
            }
        }
        locationCallback?.let {
            locationProvider.requestLocationUpdates(
                LocationRequest.Builder(
                    /*priority*/ Priority.PRIORITY_HIGH_ACCURACY,
                    /*intervalMillis*/ TimeUnit.SECONDS.toMillis(3),
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
                    if (it.isSuccessful)
                        it.result?.toMyLocation()
                    else
                        null
                )
            }
    }
}