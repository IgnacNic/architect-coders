package com.ignacnic.architectcoders.domain.gpxfile.data

import com.ignacnic.architectcoders.domain.gpxfile.domain.GPXFileRepository
import com.ignacnic.architectcoders.entities.buildconfig.BuildConfigFieldsProvider
import com.ignacnic.architectcoders.entities.location.MyLocation
import me.bvn13.sdk.android.gpx.GpxType
import me.bvn13.sdk.android.gpx.MetadataType
import me.bvn13.sdk.android.gpx.WptType
import me.bvn13.sdk.android.gpx.read
import me.bvn13.sdk.android.gpx.toXmlString
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

class GPXFileRepositoryImpl(
    private val buildConfigFields: BuildConfigFieldsProvider
) : GPXFileRepository {
    override fun getGPXFromLocations(locations: List<MyLocation>, fileName: String) = GpxType(
            metadata = MetadataType(
                name = fileName,
                authorName = buildConfigFields.getBuildConfig().appId,
            ),
            creator = buildConfigFields.getBuildConfig().appId,
            wpt = locations.map { location ->
                WptType(
                    lat = location.latitude,
                    lon = location.longitude,
                    ele = location.elevation,
                    time = OffsetDateTime.ofInstant(
                        Instant.ofEpochMilli(location.timeStamp),
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
                        latitude = wayPoint.lat,
                        longitude = wayPoint.lon,
                        elevation = wayPoint.ele,
                        timeStamp = if (wayPoint.time != null) {
                                wayPoint.time?.toInstant()?.toEpochMilli()!!
                            } else {
                                Instant.now().toEpochMilli()
                            },
                    )
                )
            }
        }
    }
}
