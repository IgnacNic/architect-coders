package com.ignacnic.architectcoders.ui.requester

import android.Manifest
import android.net.Uri
import androidx.core.net.toUri
import com.ignacnic.architectcoders.domain.gpx.GPXFileRepository
import com.ignacnic.architectcoders.domain.location.LocationRepository
import com.ignacnic.architectcoders.domain.location.MyLocation
import com.ignacnic.architectcoders.domain.userpreferences.UserPreferencesRepository
import com.ignacnic.architectcoders.ui.location.requester.LocationRequesterScreenViewModel
import com.ignacnic.architectcoders.ui.location.requester.LocationRequesterScreenViewModel.Action
import com.ignacnic.architectcoders.ui.location.requester.LocationRequesterScreenViewModel.SideEffect
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LocationRequesterScreenViewModelTest {


    private val locationRepository = mockk<LocationRepository>(relaxed = true)
    private val userPreferencesRepository = mockk<UserPreferencesRepository>(relaxed = true)
    private val gpxFileRepository = mockk<GPXFileRepository>(relaxed = true)
    private val sut = LocationRequesterScreenViewModel(
        locationRepository,
        userPreferencesRepository,
        gpxFileRepository,
    )
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

    @Test
    fun `SHOULD have requested trash WHEN TrashUpdatesRequested`() {
        sut.reduceAction(Action.TrashUpdatesRequested)
        Assert.assertTrue(sut.state.value.updatesTrashRequested)
    }

    @Test
    fun `SHOULD not have requested trash WHEN TrashUpdatesDismissed`() {
        sut.reduceAction(Action.TrashDialogDismissed)
        Assert.assertFalse(sut.state.value.updatesTrashRequested)
    }

    @Test
    fun `SHOULD have empty Location list WHEN TrashUpdatesConfirmed`() = runTest {
        givenLocations(listOf(MOCK_LOCATION))
        sut.reduceAction(Action.PermissionResult(
            mapOf(Manifest.permission.ACCESS_FINE_LOCATION to true)
        ))
        sut.reduceAction(Action.TrashUpdatesConfirmed)
        with(sut.state.value) {
            assertEquals(locationUpdates, emptyList())
            assertFalse(updatesRunning)
            assertFalse(updatesTrashRequested)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `SHOULD stop updates and send LaunchDirectoryPicker WHEN SaveRoute GIVEN there is no user directory`() =
        runTest {
            every { userPreferencesRepository.getString(any(), any()) } returnsArgument(1)
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)

            val sideEffectValues = mutableListOf<SideEffect>()
            backgroundScope.launch(testDispatcher) {
                sut.sideEffects.toList(sideEffectValues)
            }
            Assert.assertEquals(sideEffectValues, emptyList<SideEffect>())
            sut.reduceAction(Action.SaveRoute)
            advanceUntilIdle()
            Assert.assertFalse(sut.state.value.updatesRunning)
            Assert.assertTrue(sideEffectValues.last() is SideEffect.LaunchDirectoryPicker)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `SHOULD stop updates and send LaunchFilePicker WHEN SaveRoute GIVEN there is a directory`() =
        runTest {
            val storedUri = "test"
            val expectedUri = mockk<Uri>()
            mockkStatic(android.net.Uri::class)
            every { storedUri.toUri() } returns expectedUri
            every { userPreferencesRepository.getString(any(), any()) } returns storedUri
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)

            val sideEffectValues = mutableListOf<SideEffect>()
            backgroundScope.launch(testDispatcher) {
                sut.sideEffects.toList(sideEffectValues)
            }
            Assert.assertEquals(sideEffectValues, emptyList<SideEffect>())
            sut.reduceAction(Action.SaveRoute)
            advanceUntilIdle()
            Assert.assertFalse(sut.state.value.updatesRunning)
            Assert.assertTrue(sideEffectValues.last() is SideEffect.LaunchFilePicker)
            Assert.assertEquals(
                (sideEffectValues.last() as SideEffect.LaunchFilePicker).uri,
                expectedUri,
            )
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `SHOULD send LaunchFilePicker WHEN FileDirectoryPicked`() = runTest {
        val storedUri = "test"
        val expectedUri = mockk<Uri>()
        mockkStatic(android.net.Uri::class)
        every { storedUri.toUri() } returns expectedUri
        every { userPreferencesRepository.getString(any(), any()) } returns storedUri
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        val sideEffectValues = mutableListOf<SideEffect>()
        backgroundScope.launch(testDispatcher) {
            sut.sideEffects.toList(sideEffectValues)
        }
        sut.reduceAction(Action.SaveRoute)
        Assert.assertFalse(sut.state.value.updatesRunning)
        Assert.assertTrue(sideEffectValues.last() is SideEffect.LaunchFilePicker)
        Assert.assertEquals(
            (sideEffectValues.last() as SideEffect.LaunchFilePicker).uri,
            expectedUri,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `SHOULD send WriteToFile WHEN FileCreated`() =  runTest {
        val expectedUri = mockk<Uri>(relaxed = true)
        every { gpxFileRepository.getGPXFromLocations(any(), any()) } returns "test"
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        val sideEffectValues = mutableListOf<SideEffect>()
        backgroundScope.launch(testDispatcher) {
            sut.sideEffects.toList(sideEffectValues)
        }

        sut.reduceAction(Action.FileCreated(expectedUri))
        sideEffectValues.last().run {
            assertTrue(this is SideEffect.WriteToFile)
            assertEquals(this.uri, expectedUri)
            assertEquals(this.content, "test")
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
        val MOCK_LOCATION = MyLocation("test", "test", "test", "")
    }
}
