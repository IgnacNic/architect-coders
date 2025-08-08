package com.ignacnic.architectcoders.businesslogic.elevation.data

import com.ignacnic.architectcoders.businesslogic.elevation.domain.ElevationRepository
import com.ignacnic.architectcoders.entities.location.MyLocation


class ElevationRepositoryImpl(private val elevationHttpService: ElevationHttpService) :
    ElevationRepository {
    override suspend fun getElevationForLocations(locations: List<MyLocation>): List<Double> {
        val latitudes = locations.joinToString(separator = ",") {
            it.latitude.toString()
        }
        val longitudes = locations.joinToString(separator = ",") {
            it.longitude.toString()
        }
        return elevationHttpService
            .getCoordinatesElevation(latitude = latitudes, longitude = longitudes)
            .elevation
    }
}
