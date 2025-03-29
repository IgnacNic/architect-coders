package com.ignacnic.architectcoders

import com.ignacnic.architectcoders.domain.location.MyLocation
import kotlinx.serialization.Serializable

object Screens {

    @Serializable
    object LocationRequester

    @Serializable
    data class LocationDetail(val location: MyLocation)
}