package com.ignacnic.architectcoders.domain

import com.ignacnic.architectcoders.domain.location.MyLocation

interface ElevationRepository {
    suspend fun getElevationForLocations(
        locations: List<MyLocation>
    ): List<Double>
}