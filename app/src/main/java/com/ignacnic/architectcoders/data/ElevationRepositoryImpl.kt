package com.ignacnic.architectcoders.data

import com.ignacnic.architectcoders.domain.ElevationRepository
import com.ignacnic.architectcoders.domain.MyLocation

class ElevationRepositoryImpl : ElevationRepository {
    override suspend fun getElevationForLocations(locations: List<MyLocation>): List<Pair<MyLocation, Double>> {
        val latitudes = locations.fold(initial = locations.first().latitude) { acc, nextLocation ->
            "$acc,${nextLocation.latitude}"
        }
        val longitudes = locations.fold(initial = locations.first().longitude) { acc, nextLocation ->
            "$acc,${nextLocation.longitude}"
        }
        return ElevationClient
            .instance
            .getCoordinatesElevation(latitude = latitudes, longitude = longitudes)
            .elevation
            .mapIndexed { i, elevation ->
                Pair(locations[i], elevation)
            }
    }
}