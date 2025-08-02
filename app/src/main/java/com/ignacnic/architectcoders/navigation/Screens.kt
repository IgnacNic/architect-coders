package com.ignacnic.architectcoders.navigation

import kotlinx.serialization.Serializable

object Screens {

    @Serializable
    object LocationRequester

    @Serializable
    data class LocationDetail(val locationNavData: MyLocationNavData)
}
