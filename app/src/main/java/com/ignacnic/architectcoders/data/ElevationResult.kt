package com.ignacnic.architectcoders.data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ElevationResult(
    @SerialName("elevation")
    val elevation: List<Double>
)