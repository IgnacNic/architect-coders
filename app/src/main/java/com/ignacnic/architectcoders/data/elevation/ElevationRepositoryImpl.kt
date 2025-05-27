package com.ignacnic.architectcoders.data.elevation

import com.ignacnic.architectcoders.domain.elevation.ElevationRepository
import com.ignacnic.architectcoders.domain.location.MyLocation

class ElevationRepositoryImpl(private val elevationHttpService: ElevationHttpService) :
    ElevationRepository {
    override suspend fun getElevationForLocations(locations: List<MyLocation>): List<Double> {
        val latitudes = locations.joinToString(separator = ",") {
            it.latitude
        }
        val longitudes = locations.joinToString(separator = ",") {
            it.longitude
        }
        return elevationHttpService
            .getCoordinatesElevation(latitude = latitudes, longitude = longitudes)
            .elevation
    }
}
