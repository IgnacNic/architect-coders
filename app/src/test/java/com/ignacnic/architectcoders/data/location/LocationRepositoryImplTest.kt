package com.ignacnic.architectcoders.data.location

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LocationRepositoryImplTest {
    private lateinit var sut: LocationRepositoryImpl

    private val locationClient = mockk<FusedLocationProviderClient>(relaxed = true)

    @Before
    fun setup() {
        sut = LocationRepositoryImpl(locationClient)
    }

    @Test
    fun `SHOULD request location updates on client WHEN requestLocationUpdate invoked`() {
        sut.requestLocationUpdates {}
        verify {
            locationClient.requestLocationUpdates(any(), any<LocationCallback>(), any())
        }
    }

    @Test
    fun `SHOULD do nothing WHEN remove updates is called GIVEN location callback is not initialized`() {
        sut.removeLocationUpdates()
        verify(exactly = 0) {
            locationClient.removeLocationUpdates(any<LocationCallback>())
        }
    }

    @Test
    fun `SHOULD call remove updates WHEN remove updates is called GIVEN updates were requested`() {
        sut.requestLocationUpdates {}
        sut.removeLocationUpdates()
        verify { locationClient.removeLocationUpdates(any<LocationCallback>()) }
    }

    @Test
    fun `SHOULD request current location WHEN requestSingleLocation is called`() = runTest {
        sut.requestSingleLocation {  }
        coVerify {
            locationClient.getCurrentLocation(any<Int>(), any())
        }
    }
}
