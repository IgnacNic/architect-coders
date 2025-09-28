package com.ignacnic.architectcoders.feature.location.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.Paparazzi
import com.ignacnic.architectcoders.common.uiresources.theme.ArchitectCodersTheme
import com.ignacnic.architectcoders.feature.location.detail.LocationDetailScreenViewModel.UiState
import org.junit.Rule
import org.junit.Test

class LocationDetailScreenTest {
    @get:Rule
    val paparazziRule by lazy { Paparazzi() }

    @Test
    fun testLoadedDetailScreen() {
        paparazziRule.snapshot {
            ArchitectCodersTheme {
                Box(
                    modifier = Modifier
                        .height(1000.dp)
                ) {
                    DetailContent(
                        state = UiState(
                            latitude = "40.42189",
                            longitude = "-3.682189",
                            time = "0",
                            loading = false,
                            elevation = 666.0,
                        ),
                    )
                }
            }
        }
    }

    @Test
    fun testLoadingDetailScreen() {
        paparazziRule.snapshot {
            ArchitectCodersTheme {
                Box(
                    modifier = Modifier
                        .height(1000.dp)
                ) {
                    DetailContent(
                        state = UiState(
                            latitude = "40.42189",
                            longitude = "-3.682189",
                            time = "0",
                            loading = true,
                            elevation = null,
                        ),
                    )
                }
            }
        }
    }

    @Test
    fun testErrorDetailScreen() {
        paparazziRule.snapshot {
            ArchitectCodersTheme {
                Box(
                    modifier = Modifier
                        .height(1000.dp)
                ) {
                    DetailContent(
                        state = UiState(
                            latitude = "40.42189",
                            longitude = "-3.682189",
                            time = "0",
                            loading = false,
                            elevation = null,
                        ),
                    )
                }
            }
        }
    }
}
