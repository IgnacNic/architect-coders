package com.ignacnic.architectcoders.businesslogic.elevation.data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ElevationResult(
    @SerialName("elevation")
    val elevation: List<Double>
)
