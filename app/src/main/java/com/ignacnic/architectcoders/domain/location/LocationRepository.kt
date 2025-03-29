package com.ignacnic.architectcoders.domain.location

interface LocationRepository {

    fun requestLocationUpdates(onResult: (List<MyLocation>) -> Unit)

    fun removeLocationUpdates()

    suspend fun requestSingleLocation(onResult: (MyLocation?) -> Unit)
}