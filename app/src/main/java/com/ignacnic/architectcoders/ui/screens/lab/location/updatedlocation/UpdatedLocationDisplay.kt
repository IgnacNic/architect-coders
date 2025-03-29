package com.ignacnic.architectcoders.ui.screens.lab.location.updatedlocation

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ignacnic.architectcoders.data.locationRepository
import com.ignacnic.architectcoders.ui.screens.Screen
import com.ignacnic.architectcoders.ui.theme.ArchitectCodersTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun UpdatedLocationDisplay(vm: UpdatedLocationDisplayViewModel) {
    Screen {
        val locationPermissionState = rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            onPermissionsResult = vm::onPermissionsRequestResult,
        )
        Scaffold(
            topBar = {
                TopAppBar( title = { Text("Updated Location")})
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.padding(innerPadding)
            ) {
                item {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Enable Location Updates",
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Switch(
                            checked = vm.state.updatesRunning,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    locationPermissionState.launchMultiplePermissionRequest()
                                } else {
                                    vm.onStopUpdates()
                                }
                            }
                        )
                    }
                }
                items(vm.state.locationUpdates) { item ->
                    Column(Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text("Latitude: ${item.latitude}")
                        Text("Longitude: ${item.longitude}")
                        Text("Timestamp: ${item.timeStamp}")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun preview() {
    ArchitectCodersTheme {
        Surface {
            UpdatedLocationDisplay(
                vm = UpdatedLocationDisplayViewModel(locationRepository)
            )
        }
    }
}