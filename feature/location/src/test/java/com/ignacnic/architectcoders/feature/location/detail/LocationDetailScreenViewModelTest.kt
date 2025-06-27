package com.ignacnic.architectcoders.feature.location.detail

import com.ignacnic.architectcoders.domain.elevation.domain.ElevationRepository
import com.ignacnic.architectcoders.entities.location.MyLocation
import com.ignacnic.architectcoders.feature.location.CoroutinesTestRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class LocationDetailScreenViewModelTest {

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    private val elevationRepository = mockk<ElevationRepository>()

    private fun testSetup(
        elevationResults: List<Double> = emptyList(),
        block: suspend TestScope.(LocationDetailScreenViewModel) -> Unit,
    ) = runTest {
        coEvery {
            elevationRepository.getElevationForLocations(any())
        } returns elevationResults
        val location = MyLocation(
            latitude = MOCK_LAT,
            longitude = MOCK_LON,
            timeStamp = MOCK_TS,
            elevation = null,
        )
        val sut = LocationDetailScreenViewModel(
            location,
            elevationRepository
        )
        block(sut)
    }

    @Test
    fun `SHOULD have initial state WHEN view model initialized`() = testSetup { sut ->
        assertEqualsInitialState(sut.state.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `SHOULD have result state WHEN OnUiLoad action is reduced AND elevation has a single val`() = testSetup(
        listOf(MOCK_ELEVATION)
    ) { sut ->
        sut.reduceAction(LocationDetailScreenViewModel.Action.OnUiLoad)
        advanceUntilIdle()
        sut.state.value.let { state ->
            Assert.assertFalse(state.loading)
            Assert.assertEquals(state.elevation, MOCK_ELEVATION)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `SHOULD have result state with first val WHEN OnUiLoad action is reduced AND repo returns multiple vals`() =
        testSetup(
            listOf(MOCK_ELEVATION, WRONG_MOCK_ELEVATION)
        ) { sut ->
            sut.reduceAction(LocationDetailScreenViewModel.Action.OnUiLoad)
            advanceUntilIdle()
            assertEqualsFinalSuccessState(sut.state.value, MOCK_ELEVATION)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `SHOULD have result state with null val WHEN OnUiLoad action is reduced AND repo returns no vals`() =
        testSetup { sut ->
            sut.reduceAction(LocationDetailScreenViewModel.Action.OnUiLoad)
            advanceUntilIdle()
            assertEqualsFinalSuccessState(sut.state.value, null)
        }

    private fun assertEqualsInitialState(state: LocationDetailScreenViewModel.UiState) {
        Assert.assertFalse(state.loading)
        Assert.assertNull(state.elevation)
        Assert.assertEquals(state.latitude, MOCK_LAT)
        Assert.assertEquals(state.longitude, MOCK_LON)
        Assert.assertEquals(state.time, MOCK_TS)
    }

    private fun assertEqualsFinalSuccessState(
        state: LocationDetailScreenViewModel.UiState,
        result: Double?,
    ) {
        Assert.assertFalse(state.loading)
        Assert.assertEquals(state.elevation, result)
    }

    companion object {
        const val MOCK_LAT = 40.42189
        const val MOCK_LON = -3.682189
        const val MOCK_TS = 0L
        const val MOCK_ELEVATION = 666.0
        const val WRONG_MOCK_ELEVATION = 0.0
    }
}
