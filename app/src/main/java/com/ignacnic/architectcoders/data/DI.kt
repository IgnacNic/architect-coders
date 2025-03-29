package com.ignacnic.architectcoders.data

import com.ignacnic.architectcoders.domain.ElevationRepository
import com.ignacnic.architectcoders.domain.location.LocationRepository

val locationRepository: LocationRepository = LocationRepositoryImpl(Initializer.app)
val elevationRepository: ElevationRepository = ElevationRepositoryImpl(
    elevationHttpService = ElevationClient.instance
)