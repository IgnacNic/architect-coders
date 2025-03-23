package com.ignacnic.architectcoders.domain

import android.location.Location

data class MyLocation (
    val latitude: String,
    val longitude: String,
    val timeStamp: String,
) {
    fun toSimpleString() = "[$latitude, $longitude]"
}

fun Location.toMyLocation() = MyLocation(
    latitude = latitude.toString(),
    longitude = longitude.toString(),
    timeStamp = time.toString(),
)