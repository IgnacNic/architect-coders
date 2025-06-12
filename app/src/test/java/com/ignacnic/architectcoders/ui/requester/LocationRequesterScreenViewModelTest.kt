package com.ignacnic.architectcoders.ui.requester

import android.Manifest
import com.ignacnic.architectcoders.domain.location.LocationRepository
import com.ignacnic.architectcoders.domain.location.MyLocation
import com.ignacnic.architectcoders.ui.location.requester.LocationRequesterScreenViewModel
import com.ignacnic.architectcoders.ui.location.requester.LocationRequesterScreenViewModel.Action
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import kotlin.test.assertFailsWith

class LocationRequesterScreenViewModelTest {


    private val locationRepository = mockk<LocationRepository>(relaxed = true)
    private val sut = LocationRequesterScreenViewModel(locationRepository)
    private val locationFlow = MutableStateFlow(emptyList<MyLocation>())

    @Test
    fun `SHOULD have initial state WHEN viewModel is initialized`() {
        sut.state.value.let { state ->
            Assert.assertEquals(state.locationUpdates, emptyList<MyLocation>())
            Assert.assertFalse(state.updatesRunning)
            Assert.assertFalse(state.locationRationaleNeeded)
        }
    }

    @Test
    fun `SHOULD enable rationale WHEN PermissionResult is reduced GIVEN no permissions given`() {
        sut.reduceAction(Action.PermissionResult(emptyMap()))
        sut.state.value.let { state ->
            Assert.assertEquals(state.locationUpdates, emptyList<MyLocation>())
            Assert.assertFalse(state.updatesRunning)
            Assert.assertTrue(state.locationRationaleNeeded)
        }
    }

    @Test
    fun `SHOULD enable rationale WHEN PermissionResult is reduced GIVEN permissions were denied`() {
        sut.reduceAction(Action.PermissionResult(
            mapOf(Manifest.permission.ACCESS_FINE_LOCATION to false)
        ))
        sut.state.value.let { state ->
            Assert.assertEquals(state.locationUpdates, emptyList<MyLocation>())
            Assert.assertFalse(state.updatesRunning)
            Assert.assertTrue(state.locationRationaleNeeded)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `SHOULD have correct states WHEN PermissionResult is reduced GIVEN permissions were accepted`() = runTest {
        givenLocations(listOf(MOCK_LOCATION))
        val stateValues = mutableListOf<LocationRequesterScreenViewModel.UiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            sut.state.toList(stateValues)
        }
        sut.reduceAction(Action.PermissionResult(
            mapOf(Manifest.permission.ACCESS_FINE_LOCATION to true)
        ))
        verify {
            locationRepository.startLocationUpdates()
        }
        stateValues[1].let { state ->
            Assert.assertTrue(state.updatesRunning)
            Assert.assertFalse(state.locationRationaleNeeded)
            Assert.assertEquals(state.locationUpdates, emptyList<MyLocation>())
        }
        stateValues[2].let { state ->
            Assert.assertEquals(state.locationUpdates, listOf(MOCK_LOCATION))
        }
    }

    @Test
    fun `SHOULD remove updates WHEN UpdatesStopped is reduced`() {
        sut.reduceAction(Action.UpdatesStopped)
        Assert.assertFalse(sut.state.value.updatesRunning)
        verify {
            locationRepository.stopLocationUpdates()
        }
    }

    @Test
    fun `SHOULD update the state WHEN rationale dialog is dismissed`() {
        sut.reduceAction(Action.PermissionResult(emptyMap()))
        Assert.assertTrue(sut.state.value.locationRationaleNeeded)
        sut.reduceAction(Action.RationaleDialogDismissed)
        Assert.assertFalse(sut.state.value.locationRationaleNeeded)
    }

    @Test
    fun `SHOULD update the state WHEN rationale dialog is accepted`() {
        sut.reduceAction(Action.PermissionResult(emptyMap()))
        Assert.assertTrue(sut.state.value.locationRationaleNeeded)
        sut.reduceAction(Action.RationaleDialogConfirmed)
        Assert.assertFalse(sut.state.value.locationRationaleNeeded)
    }

    @Test
    fun `SHOULD fail with Illegal State WHEN start location updates GIVEN they already started`() {
        sut.reduceAction(Action.PermissionResult(
            mapOf(Manifest.permission.ACCESS_FINE_LOCATION to true))
        )
        assertFailsWith<IllegalStateException> {
            sut.reduceAction(Action.PermissionResult(
                mapOf(Manifest.permission.ACCESS_FINE_LOCATION to true))
            )
        }
    }

    @Test
    fun `SHOULD show trash dialog WHEN TrashUpdatesRequested`() {
        sut.reduceAction(Action.TrashUpdatesRequested)
        Assert.assertTrue(sut.state.value.updatesTrashRequested)
    }

    @Test
    fun `SHOULD hide trash dialog WHEN TrashDialogDismissed`() {
        sut.reduceAction(Action.TrashUpdatesRequested)
        Assert.assertTrue(sut.state.value.updatesTrashRequested)
        sut.reduceAction(Action.TrashDialogDismissed)
        Assert.assertFalse(sut.state.value.updatesTrashRequested)
    }

    @Test
    fun `SHOULD hide trash dialog and delete updates WHEN TrashUpdatesConfirmed`() = runTest {
        givenLocations(listOf(MOCK_LOCATION))
        sut.reduceAction(Action.PermissionResult(
            mapOf(Manifest.permission.ACCESS_FINE_LOCATION to true)
        ))
        sut.reduceAction(Action.TrashUpdatesRequested)
        sut.reduceAction(Action.TrashUpdatesConfirmed)
        sut.state.value.run {
            Assert.assertFalse(updatesRunning)
            Assert.assertFalse(updatesTrashRequested)
            Assert.assertEquals(locationUpdates, emptyList<MyLocation>())
        }
        verify {
            locationRepository.stopLocationUpdates()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun TestScope.givenLocations(locations: List<MyLocation>) {
        every {
            locationRepository.startLocationUpdates()
        } returns locationFlow.also { flow ->
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                flow.emit(locations)
            }
        }
    }

    companion object {
        val MOCK_LOCATION = MyLocation("test", "test", "test")
    }
}
