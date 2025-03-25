package com.ignacnic.architectcoders.domain

interface LocationRepository {

    fun requestLocationUpdates(onResult: (List<MyLocation>) -> Unit)

    fun removeLocationUpdates()

    suspend fun requestSingleLocation(onResult: (MyLocation?) -> Unit)
}