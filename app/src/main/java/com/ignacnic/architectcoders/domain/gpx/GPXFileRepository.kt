package com.ignacnic.architectcoders.domain.gpx

import com.ignacnic.architectcoders.domain.location.MyLocation

interface GPXFileRepository {
    fun getGPXFromLocations(locations: List<MyLocation>, fileName: String): String
    fun getLocationsFromGPX(gpxContent: String): List<MyLocation>
}
