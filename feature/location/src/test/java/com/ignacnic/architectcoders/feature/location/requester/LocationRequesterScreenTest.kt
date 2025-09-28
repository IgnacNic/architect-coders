package com.ignacnic.architectcoders.feature.location.requester

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.Paparazzi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.ignacnic.architectcoders.common.uiresources.theme.ArchitectCodersTheme
import com.ignacnic.architectcoders.entities.location.MyLocation
import com.ignacnic.architectcoders.feature.location.requester.LocationRequesterScreenViewModel.UiState
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalPermissionsApi::class)
class LocationRequesterScreenTest {
    @get:Rule
    val paparazziTestRule by lazy { Paparazzi() }

    @Test
    fun testEmptyRequesterScreen() {
        paparazziTestRule.snapshot {
            ArchitectCodersTheme {
                Box(
                    modifier = Modifier
                        .height(1000.dp),
                ) {
                    RequesterContent(
                        UiState(
                            locationUpdates = emptyList(),
                            updatesRunning = false,
                            locationRationaleNeeded = false,
                            updatesTrashRequested = false,
                        ),
                        MultiplePermissionsStatePreview(),
                    )
                }
            }
        }
    }

    @Test
    fun testFilledRequesterScreen() {
        paparazziTestRule.snapshot {
            ArchitectCodersTheme {
                Box(
                    modifier = Modifier
                        .height(1000.dp),
                ) {
                    RequesterContent(
                        UiState(
                            locationUpdates = listOf(
                                MyLocation(
                                    latitude = 40.42189,
                                    longitude = -3.682189,
                                    timeStamp = 0,
                                    elevation = 666.0
                                )
                            ),
                            updatesRunning = true,
                            locationRationaleNeeded = false,
                            updatesTrashRequested = false,
                        ),
                        MultiplePermissionsStatePreview(),
                    )
                }
            }
        }
    }

    @ExperimentalPermissionsApi
    class MultiplePermissionsStatePreview : MultiplePermissionsState {

        override val allPermissionsGranted: Boolean
            get() = false

        override val permissions: List<PermissionState>
            get() = emptyList()

        override val revokedPermissions: List<PermissionState>
            get() = emptyList()

        override val shouldShowRationale: Boolean
            get() = true

        override fun launchMultiplePermissionRequest() {
            // do nothing
        }
    }
}
