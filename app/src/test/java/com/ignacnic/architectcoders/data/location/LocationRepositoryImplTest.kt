package com.ignacnic.architectcoders.data.location

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.ignacnic.architectcoders.domain.location.MyLocation
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class LocationRepositoryImplTest {
    private lateinit var sut: LocationRepositoryImpl

    private val locationClient = mockk<FusedLocationProviderClient>(relaxed = true)

    @Before
    fun setup() {
        sut = LocationRepositoryImpl(locationClient)
    }

    @Test
    fun `SHOULD request location updates on client WHEN requestLocationUpdate invoked`() = runTest {
        val flow = sut.startLocationUpdates()
        val collectJob = launch { flow.collect{} }
        advanceUntilIdle()
        verify {
            locationClient.requestLocationUpdates(any(), any<LocationCallback>(), any())
        }
        collectJob.cancel()
    }

    @Test
    fun `SHOULD do nothing WHEN remove updates is called GIVEN location callback is not initialized`() {
        sut.stopLocationUpdates()
        verify(exactly = 0) {
            locationClient.removeLocationUpdates(any<LocationCallback>())
        }
    }

    @Test
    fun `SHOULD call remove updates WHEN remove updates is called GIVEN updates were requested`() = runTest {
        val flow = sut.startLocationUpdates()
        val collectJob = launch { flow.collect{} }
        advanceUntilIdle()
        sut.stopLocationUpdates()
        verify { locationClient.removeLocationUpdates(any<LocationCallback>()) }
        collectJob.cancel()
    }

    @Test
    fun `SHOULD call remove updates WHEN flow lost observer GIVEN updates were requested`() = runTest {
        val flow = sut.startLocationUpdates()
        val collectJob = launch { flow.collect{} }
        advanceUntilIdle()
        collectJob.cancel()
        advanceUntilIdle()
        verify { locationClient.removeLocationUpdates(any<LocationCallback>()) }
    }

    @Test
    fun `SHOULD request current location WHEN requestSingleLocation is called`() = runTest {
        sut.requestSingleLocation {  }
        coVerify {
            locationClient.getCurrentLocation(any<Int>(), any())
        }
    }

    @Test
    fun `SHOULD duplicated filtered locations WHEN startLocationUpdates GIVEN missing accuracy`() = runTest {
        val flow = sut.startLocationUpdates()
        val collectJob = launch { flow.collect{ result ->
            Assert.assertEquals(MYLOCATION_EXPECTED_NO_ACC, result)
        }}
        advanceUntilIdle()
        sut.locationCallback?.onLocationResult(LocationResult.create(LOCATION_LIST_WITHOUT_ACC))
        advanceUntilIdle()
        collectJob.cancel()
    }

    @Test
    fun `SHOULD correctly filtered locations WHEN startLocationUpdates GIVEN missing accuracy`() = runTest {
        val flow = sut.startLocationUpdates()
        val collectJob = launch { flow.collect{ result ->
            Assert.assertEquals(MYLOCATION_EXPECTED, result)
        }}
        advanceUntilIdle()
        sut.locationCallback?.onLocationResult(LocationResult.create(LOCATION_LIST_WITH_ACC))
        advanceUntilIdle()
        collectJob.cancel()
    }

    companion object {
        private val LOCATION_LIST_WITHOUT_ACC = listOf(
            Location("").apply {
                latitude = 1.0
                longitude = 2.0
                time = 0L
            },
            Location("").apply {
                latitude = 1.0
                longitude = 2.0
                time = 1L
            },
        )
        private val MYLOCATION_EXPECTED_NO_ACC = listOf(
            MyLocation("1.0", "2.0", "0"),
            MyLocation("1.0", "2.0", "1"),
        )
        private val LOCATION_LIST_WITH_ACC = listOf(
            Location("").apply {
                latitude = 1.0
                longitude = 2.0
                time = 0L
                accuracy = 10f
            },
            Location("").apply {
                latitude = 1.0
                longitude = 2.0
                time = 1L
                accuracy = 10f
            },
        )
        private val MYLOCATION_EXPECTED = listOf(
            MyLocation("1.0", "2.0", "0"),
        )
    }
}
