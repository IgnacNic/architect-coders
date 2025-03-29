package com.ignacnic.architectcoders.ui.screens.lab.location.singlelocation

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ignacnic.architectcoders.domain.location.MyLocation
import com.ignacnic.architectcoders.ui.screens.Screen
import com.ignacnic.architectcoders.ui.screens.lab.location.singlelocation.SingleLocationDisplayViewModel.Intent
import com.ignacnic.architectcoders.ui.screens.lab.location.singlelocation.SingleLocationDisplayViewModel.UIState
import com.ignacnic.architectcoders.ui.theme.ArchitectCodersTheme


@Composable
fun SingleLocationDisplay(vm: SingleLocationDisplayViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    val handler = vm::handleIntent
    Screen {
        Content(state, handler)
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun Content(state: UIState, handler: (Intent) -> Unit = {}) {
    Scaffold (
        topBar = { TopAppBar(title = { Text("Single Location request")}) }
    ) { paddingValues ->
        val locationPermissionState = rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            onPermissionsResult = {
                handler(Intent.PermissionResult(it))
            },
        )
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if(!state.locationLoading) {
                Button(
                    onClick = {
                        locationPermissionState.launchMultiplePermissionRequest()
                    },
                ) {
                    Text(text = "Get my location")
                }
            } else {
                CircularProgressIndicator()
            }
            Text(
                text = if(state.locationFetched) {
                    if (state.location != null) {
                        "Your location is: ${state.location.toSimpleString()}"
                    } else {
                        "Error accessing to your location"
                    }
                } else {
                    ""
                }
            )
            if (state.locationFetched && state.location != null) {
                Button(
                    onClick = { handler(Intent.RequestElevation) }
                ) {
                    if (state.elevationLoading){
                        CircularProgressIndicator()
                    } else {
                        Text(text = "Get your elevation")
                    }
                }
                if (state.elevation != null) {
                    Text(text = "Your elevation is ${state.elevation} meters")
                }
            }
        }
    }
}

@Preview
@Composable
private fun LocationDisplayPreview() {
    ArchitectCodersTheme {
        Surface {
            Content(
                UIState(
                    location = MyLocation(
                        latitude = "40,41105",
                        longitude = "-3.682535",
                        timeStamp = "0"
                    ),
                    locationFetched = true,
                    locationLoading = false,
                    elevationLoading = false,
                    elevation = null,
                )
            )
        }
    }
}