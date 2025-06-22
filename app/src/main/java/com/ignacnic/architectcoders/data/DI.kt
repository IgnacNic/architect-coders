package com.ignacnic.architectcoders.data

import com.google.android.gms.location.LocationServices
import com.ignacnic.architectcoders.data.elevation.ElevationClient
import com.ignacnic.architectcoders.data.elevation.ElevationRepositoryImpl
import com.ignacnic.architectcoders.data.gpx.GPXFileRepositoryImpl
import com.ignacnic.architectcoders.data.location.LocationRepositoryImpl
import com.ignacnic.architectcoders.data.userpreferences.SharedPreferencesDataSource
import com.ignacnic.architectcoders.data.userpreferences.UserPreferencesRepositoryImpl
import com.ignacnic.architectcoders.domain.elevation.ElevationRepository
import com.ignacnic.architectcoders.domain.gpx.GPXFileRepository
import com.ignacnic.architectcoders.domain.location.LocationRepository
import com.ignacnic.architectcoders.domain.userpreferences.UserPreferencesRepository

val locationRepository: LocationRepository = LocationRepositoryImpl(
    LocationServices.getFusedLocationProviderClient(Initializer.app)
)
val elevationRepository: ElevationRepository = ElevationRepositoryImpl(
    elevationHttpService = ElevationClient.instance
)

val sharedPreferencesDataSource = SharedPreferencesDataSource(Initializer.app)

val userPreferencesRepository: UserPreferencesRepository = UserPreferencesRepositoryImpl(
    sharedPreferencesDataSource = sharedPreferencesDataSource
)

val gpxFileRepository: GPXFileRepository = GPXFileRepositoryImpl()
