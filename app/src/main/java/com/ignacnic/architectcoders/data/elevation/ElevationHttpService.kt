package com.ignacnic.architectcoders.data.elevation

import retrofit2.http.GET
import retrofit2.http.Query

interface ElevationHttpService {

    @GET("elevation")
    suspend fun getCoordinatesElevation(
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
    ): ElevationResult
}
