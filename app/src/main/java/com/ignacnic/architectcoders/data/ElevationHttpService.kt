package com.ignacnic.architectcoders.data

import retrofit2.http.GET
import retrofit2.http.Query

interface ElevationHttpService {

    @GET("elevation")
    suspend fun getCoordinatesElevation(
        @Query("latitude") latitude: String,
        @Query("latitude") longitude: String,
    ): ElevationResult
}