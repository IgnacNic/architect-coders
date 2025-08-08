package com.ignacnic.architectcoders.businesslogic.elevation.data

import com.ignacnic.architectcoders.entities.location.MyLocation
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ElevationRepositoryImplTest {

    private lateinit var sut: ElevationRepositoryImpl
    private val elevationHttpService = mockk<ElevationHttpService>()

    @Before
    fun setup() {
        sut = ElevationRepositoryImpl(elevationHttpService)
    }

    @Test
    fun `SHOULD return a list of elevations WHEN request returns successfully`() = runTest {
        val elevationList = listOf(1.0, 2.0)
        coEvery {
            elevationHttpService.getCoordinatesElevation(any(), any())
        } returns ElevationResult(elevationList)
        val elevation = MyLocation(latitude = 0.0, longitude = 0.0, timeStamp = 0L, elevation = null)
        val actualResult = sut.getElevationForLocations(listOf(elevation))
        Assert.assertEquals(actualResult, elevationList)
    }
}
