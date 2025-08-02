package com.ignacnic.architectcoders.businesslogic.gpxfile.data

import com.ignacnic.architectcoders.entities.buildconfig.BuildConfigFields
import com.ignacnic.architectcoders.entities.buildconfig.BuildConfigFieldsProvider
import com.ignacnic.architectcoders.entities.location.MyLocation
import io.mockk.every
import io.mockk.mockk
import me.bvn13.sdk.android.gpx.GpxType
import me.bvn13.sdk.android.gpx.MetadataType
import me.bvn13.sdk.android.gpx.WptType
import me.bvn13.sdk.android.gpx.toXmlString
import org.junit.Assert
import org.junit.Test

class GPXFileRepositoryImplText {

    private val buildConfigFieldsProvider = mockk<BuildConfigFieldsProvider>(relaxed = true).also {
        every { it.getBuildConfig() } returns BuildConfigFields(appId = "test")
    }
    private val sut = GPXFileRepositoryImpl(buildConfigFieldsProvider)

    @Test
    fun `SHOULD return empty list WHEN getLocationsFromGPX GIVEN gpx has no waypoints`() {
        val actualResult = sut.getLocationsFromGPX(EMPTY_GPX_FILE)
        Assert.assertEquals(actualResult, emptyList<MyLocation>())
    }

    @Test
    fun `SHOULD return a one element list WHEN getLocationsFromGPX GIVEN gpx has one waypoint`() {
        val actualResult = sut.getLocationsFromGPX(GPX_WITH_WPT_NO_TIMESTAMPS)
        Assert.assertEquals(actualResult.size, 1)
        actualResult.last().run {
            Assert.assertEquals(latitude, MOCK_LAT, 0.0)
            Assert.assertEquals(longitude, MOCK_LON, 0.0)
            Assert.assertEquals(elevation, MOCK_ELE)
        }
    }

    companion object {
        private const val MOCK_FILE_NAME = "test"
        private const val MOCK_LON = 1.0
        private const val MOCK_LAT = 1.0
        private const val MOCK_ELE = 0.0
        private val EMPTY_GPX_FILE = GpxType(
            metadata = MetadataType(name = MOCK_FILE_NAME, authorName = "test"),
            creator = "test",
        ).toXmlString()
        private val GPX_WITH_WPT_NO_TIMESTAMPS = GpxType(
            metadata = MetadataType(name = MOCK_FILE_NAME, authorName = "test"),
            wpt = listOf(
                WptType(
                    lat = MOCK_LAT,
                    lon = MOCK_LON,
                    ele = MOCK_ELE,
                )
            )
        ).toXmlString()
    }
}
