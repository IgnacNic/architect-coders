package com.ignacnic.architectcoders.domain

interface ElevationRepository {
    suspend fun getElevationForLocations(
        locations: List<MyLocation>
    ): List<Pair<MyLocation, Double>>
}