package com.ignacnic.architectcoders.domain.location

import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.milliseconds

@Serializable
@Parcelize
data class MyLocation (
    val latitude: String,
    val longitude: String,
    val timeStamp: String,
    val elevation: String,
) : Parcelable

fun Location.toMyLocation() = MyLocation(
    latitude = latitude.toString(),
    longitude = longitude.toString(),
    timeStamp = time.milliseconds.inWholeMilliseconds.toString(),
    elevation = if (hasAltitude()) altitude.toString() else ""
)

val MyLocationType = object : NavType<MyLocation>(
    isNullableAllowed = false
) {
    override fun get(bundle: Bundle, key: String): MyLocation? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(
                key, MyLocation::class.java
            )
        } else {
            bundle.getParcelable(key)
        }
    }

    override fun parseValue(value: String): MyLocation {
        return Json.decodeFromString<MyLocation>(value)
    }

    override fun serializeAsValue(value: MyLocation): String {
        return Json.encodeToString(value)
    }

    override fun put(bundle: Bundle, key: String, value: MyLocation) {
        bundle.putParcelable(key, value)
    }

}
