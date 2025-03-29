package com.ignacnic.architectcoders.ui.screens.location.requester

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ignacnic.architectcoders.R
import com.ignacnic.architectcoders.domain.location.MyLocation
import com.ignacnic.architectcoders.ui.screens.Screen
import com.ignacnic.architectcoders.ui.screens.location.requester.LocationRequesterScreenViewModel.Action
import com.ignacnic.architectcoders.ui.screens.location.requester.LocationRequesterScreenViewModel.UiState


@Composable
fun LocationRequesterScreen(
    vm: LocationRequesterScreenViewModel,
    onCardClick: (MyLocation) -> Unit,
) {
    val state by vm.state.collectAsState()
    Screen{
        RequesterContent(state, vm::reduceAction, onCardClick)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
private fun RequesterContent(
    state: UiState,
    reduce: (Action) -> Unit,
    onCardClick: (MyLocation) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar( title = { Text("Find my location") })
        }
    ) { innerPadding ->
        val locationPermissionState = rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            onPermissionsResult = { reduce(Action.PermissionResult(it)) },
        )
        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            item {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Receive Location Updates",
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Switch(
                        checked = state.updatesRunning,
                        onCheckedChange = { checked ->
                            if (checked) {
                                locationPermissionState.launchMultiplePermissionRequest()
                            } else {
                                reduce(Action.UpdatesStopped)
                            }
                        }
                    )
                }
            }
            items(state.locationUpdates) { item ->
                Card(
                    modifier = Modifier.
                    padding(horizontal = 12.dp, vertical = 6.dp)
                        .fillMaxWidth(),
                    onClick = {
                        onCardClick(item)
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val latitude = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Latitude: ")
                            }
                            append(item.latitude)
                        }
                        val longitude = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Longitude: ")
                            }
                            append(item.longitude)
                        }
                        val timeStamp = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Timestamp: ")
                            }
                            append(item.timeStamp)
                        }
                        Column(modifier = Modifier.padding(8.dp).weight(1f)) {
                            Text(latitude)
                            Text(longitude)
                            Text(timeStamp)
                        }
                        Icon(
                            painter = painterResource(R.drawable.ic_location),
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = "Location",
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .size(40.dp),
                        )
                    }
                }
            }
        }
    }
}