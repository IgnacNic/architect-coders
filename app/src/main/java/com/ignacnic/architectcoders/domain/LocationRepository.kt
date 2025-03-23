package com.ignacnic.architectcoders.domain

interface LocationRepository {

    fun requestLocationUpdates(lambda: (List<MyLocation>) -> Unit)

    fun removeLocationUpdates()

    suspend fun requestSingleLocation(): MyLocation
}