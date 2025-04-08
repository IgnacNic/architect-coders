package com.ignacnic.architectcoders.data

import com.ignacnic.architectcoders.data.elevation.ElevationClient
import com.ignacnic.architectcoders.data.elevation.ElevationRepositoryImpl
import com.ignacnic.architectcoders.data.location.LocationRepositoryImpl
import com.ignacnic.architectcoders.domain.elevation.ElevationRepository
import com.ignacnic.architectcoders.domain.location.LocationRepository

val locationRepository: LocationRepository = LocationRepositoryImpl(Initializer.app)
val elevationRepository: ElevationRepository = ElevationRepositoryImpl(
    elevationHttpService = ElevationClient.instance
)
