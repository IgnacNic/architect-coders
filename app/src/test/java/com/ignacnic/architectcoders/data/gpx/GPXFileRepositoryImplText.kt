package com.ignacnic.architectcoders.data.gpx

import com.ignacnic.architectcoders.BuildConfig
import me.bvn13.sdk.android.gpx.GpxType
import me.bvn13.sdk.android.gpx.MetadataType
import me.bvn13.sdk.android.gpx.WptType
import me.bvn13.sdk.android.gpx.toXmlString
import org.junit.Test
import kotlin.test.assertEquals

class GPXFileRepositoryImplText {

    private val sut = GPXFileRepositoryImpl()

    @Test
    fun `SHOULD return empty list WHEN getLocationsFromGPX GIVEN gpx has no waypoints`() {
        val actualResult = sut.getLocationsFromGPX(EMPTY_GPX_FILE)
        assertEquals(actualResult, emptyList())
    }

    @Test
    fun `SHOULD return a one element list WHEN getLocationsFromGPX GIVEN gpx has one waypoint`() {
        val actualResult = sut.getLocationsFromGPX(GPX_WITH_WPT_NO_TIMESTAMPS)
        assertEquals(actualResult.size, 1)
        actualResult.last().run {
            assertEquals(latitude, MOCK_LAT)
            assertEquals(longitude, MOCK_LON)
            assertEquals(elevation, MOCK_ELE)
        }
    }

    companion object {
        private const val MOCK_FILE_NAME = "test"
        private const val MOCK_LON = "1.0"
        private const val MOCK_LAT = "1.0"
        private const val MOCK_ELE = "0.0"
        private val EMPTY_GPX_FILE = GpxType(
            metadata = MetadataType(name = MOCK_FILE_NAME, authorName = BuildConfig.APPLICATION_ID),
            creator = BuildConfig.APPLICATION_ID,
        ).toXmlString()
        private val GPX_WITH_WPT_NO_TIMESTAMPS = GpxType(
            metadata = MetadataType(name = MOCK_FILE_NAME, authorName = BuildConfig.APPLICATION_ID),
            wpt = listOf(
                WptType(
                    lat = MOCK_LAT.toDouble(),
                    lon = MOCK_LON.toDouble(),
                    ele = MOCK_ELE.toDouble(),
                )
            )
        ).toXmlString()
    }
}
