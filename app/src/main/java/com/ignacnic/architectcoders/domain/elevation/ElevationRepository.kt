package com.ignacnic.architectcoders.domain.elevation

import com.ignacnic.architectcoders.domain.location.MyLocation

interface ElevationRepository {
    suspend fun getElevationForLocations(
        locations: List<MyLocation>
    ): List<Double>
}
