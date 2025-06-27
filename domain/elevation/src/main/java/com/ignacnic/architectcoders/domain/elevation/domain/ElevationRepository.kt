package com.ignacnic.architectcoders.domain.elevation.domain

import com.ignacnic.architectcoders.entities.location.MyLocation

interface ElevationRepository {
    suspend fun getElevationForLocations(
        locations: List<MyLocation>
    ): List<Double>
}
