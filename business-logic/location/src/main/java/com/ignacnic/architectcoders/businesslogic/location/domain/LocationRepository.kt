package com.ignacnic.architectcoders.businesslogic.location.domain

import com.ignacnic.architectcoders.entities.location.MyLocation
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    fun startLocationUpdates(): Flow<List<MyLocation>>

    fun stopLocationUpdates()

    suspend fun requestSingleLocation(onResult: (MyLocation?) -> Unit)
}
