package com.ignacnic.architectcoders.businesslogic.elevation.domain

import com.ignacnic.architectcoders.entities.location.MyLocation

interface ElevationRepository {
    suspend fun getElevationForLocations(
        locations: List<MyLocation>
    ): List<Double>
}
