package com.ignacnic.architectcoders.common.uiresources.components

import androidx.compose.ui.res.stringResource
import app.cash.paparazzi.Paparazzi
import com.ignacnic.architectcoders.common.uiresources.R
import com.ignacnic.architectcoders.common.uiresources.theme.ArchitectCodersTheme
import org.junit.Rule
import org.junit.Test

class RationaleDialogTest {
    @get:Rule
    val paparazziTestRule by lazy { Paparazzi() }

    @Test
    fun testRationaleDialog() {
        paparazziTestRule.snapshot {
            ArchitectCodersTheme {
                    RationaleDialog(
                        title = stringResource(R.string.location_rationale_title),
                        description = stringResource(R.string.location_rationale_description),)
            }
        }
    }
}
