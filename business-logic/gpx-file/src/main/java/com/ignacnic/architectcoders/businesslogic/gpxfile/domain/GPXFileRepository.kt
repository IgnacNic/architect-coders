package com.ignacnic.architectcoders.businesslogic.gpxfile.domain

import com.ignacnic.architectcoders.entities.location.MyLocation

interface GPXFileRepository {
    fun getGPXFromLocations(locations: List<MyLocation>, fileName: String): String
    fun getLocationsFromGPX(gpxContent: String): List<MyLocation>
}
