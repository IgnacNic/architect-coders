package com.ignacnic.architectcoders.data.gpx

import com.ignacnic.architectcoders.BuildConfig
import com.ignacnic.architectcoders.domain.gpx.GPXFileRepository
import com.ignacnic.architectcoders.domain.location.MyLocation
import me.bvn13.sdk.android.gpx.GpxType
import me.bvn13.sdk.android.gpx.MetadataType
import me.bvn13.sdk.android.gpx.WptType
import me.bvn13.sdk.android.gpx.read
import me.bvn13.sdk.android.gpx.toXmlString
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

class GPXFileRepositoryImpl : GPXFileRepository {
    override fun getGPXFromLocations(locations: List<MyLocation>, fileName: String) = GpxType(
            metadata = MetadataType(name = fileName, authorName = BuildConfig.APPLICATION_ID),
            creator = BuildConfig.APPLICATION_ID,
            wpt = locations.map { location ->
                WptType(
                    lat = location.latitude.toDouble(),
                    lon = location.longitude.toDouble(),
                    ele = location.elevation.toDoubleOrNull(),
                    time = OffsetDateTime.ofInstant(
                        Instant.ofEpochMilli(location.timeStamp.toLong()),
                        ZoneId.systemDefault(),
                    ),
                )
            }
        ).toXmlString()

    override fun getLocationsFromGPX(gpxContent: String): List<MyLocation> {
        val gpx = GpxType.read(gpxContent.byteInputStream())
        return buildList {
            gpx.wpt?.forEach { wayPoint ->
                add(
                    MyLocation(
                        latitude = wayPoint.lat.toString(),
                        longitude = wayPoint.lon.toString(),
                        elevation = wayPoint.ele.toString(),
                        timeStamp = if (wayPoint.time != null) {
                                wayPoint.time?.toInstant()?.toEpochMilli().toString()
                            } else {
                                Instant.now().toEpochMilli().toString()
                            },
                    )
                )
            }
        }
    }
}
