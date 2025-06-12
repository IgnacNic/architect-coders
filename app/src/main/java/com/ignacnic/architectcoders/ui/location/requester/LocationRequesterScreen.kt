package com.ignacnic.architectcoders.ui.location.requester

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ignacnic.architectcoders.BuildConfig
import com.ignacnic.architectcoders.R
import com.ignacnic.architectcoders.domain.location.MyLocation
import com.ignacnic.architectcoders.ui.Screen
import com.ignacnic.architectcoders.ui.location.LocationRationaleDialog
import com.ignacnic.architectcoders.ui.location.requester.LocationRequesterScreenViewModel.Action
import com.ignacnic.architectcoders.ui.location.requester.LocationRequesterScreenViewModel.UiState
import com.ignacnic.architectcoders.ui.theme.ArchitectCodersTheme


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationRequesterScreen(
    vm: LocationRequesterScreenViewModel,
    onCardClick: (MyLocation) -> Unit,
) {
    val state by vm.state.collectAsState()
    val reduce = vm::reduceAction
    Screen{
        val locationPermissionState = rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            onPermissionsResult = { reduce(Action.PermissionResult(it)) },
        )
        RequesterContent(state, locationPermissionState, reduce, onCardClick)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
private fun RequesterContent(
    state: UiState,
    locationPermissionState: MultiplePermissionsState,
    reduce: (Action) -> Unit = {},
    onCardClick: (MyLocation) -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar( title = { Text(stringResource(R.string.record_screen_title)) })
        }
    ) { innerPadding ->
        if (state.locationRationaleNeeded) {
            val context = LocalContext.current
            LocationRationaleDialog(
                onAccept = {
                    context.startActivity(
                        Intent(
                            ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts(
                                "package",
                                BuildConfig.APPLICATION_ID,
                                null,
                            )
                        )
                    )
                },
                onDismiss = { reduce(Action.RationaleDialogDismissed) },
            )
        } else if (state.updatesTrashRequested) {
            TrashUpdatesDialog(reduce)
        }
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
        ) {
            item {
                LocationUpdatesControlButtons(
                    state = state,
                    reduce = reduce,
                    locationPermissionState = locationPermissionState,
                    modifier = Modifier.padding(12.dp),
                )
            }
            items(state.locationUpdates) { item ->
                LocationListItem(
                    item = item,
                    onCardClick = onCardClick,
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun LocationUpdatesControlButtons(
    state: UiState,
    reduce: (Action) -> Unit,
    locationPermissionState: MultiplePermissionsState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(Modifier.weight(1f)) {
            IconButton(
                onClick = {
                    locationPermissionState.launchMultiplePermissionRequest()
                },
                enabled = !state.updatesRunning,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_play),
                    contentDescription = stringResource(R.string.play),
                )
            }
            IconButton(
                onClick = {
                    reduce(Action.UpdatesStopped)
                },
                enabled = state.updatesRunning,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_pause),
                    contentDescription = stringResource(R.string.pause),
                )
            }
            IconButton(
                onClick = {},
                enabled = state.locationUpdates.isNotEmpty(),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_save),
                    contentDescription = stringResource(R.string.save),
                )
            }
        }
        IconButton(
            onClick = {
                reduce(Action.TrashUpdatesRequested)
            },
            enabled = state.locationUpdates.isNotEmpty(),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_trash),
                contentDescription = stringResource(R.string.delete),
            )
        }
    }
}

@Composable
private fun LocationListItem(
    item: MyLocation,
    modifier: Modifier = Modifier,
    onCardClick: (MyLocation) -> Unit = {},
) {
    Card(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
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
            Column(modifier = Modifier
                .padding(8.dp)
                .weight(1f)) {
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

@Composable
private fun TrashUpdatesDialog(
    reduce: (Action) -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        text = {
            Text(text = stringResource(R.string.trash_dialog_text))
        },
        onDismissRequest = {
            reduce(Action.TrashDialogDismissed)
        },
        confirmButton = {
            TextButton(
                onClick = { reduce(Action.TrashUpdatesConfirmed) },
            ) { Text(stringResource(R.string.generic_delete)) }
        },
        dismissButton = {
            Button(
                onClick = { reduce(Action.TrashDialogDismissed) },
            ) { Text(stringResource(R.string.generic_cancel)) }
        },
    )
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

@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
private fun EmptyRequesterPreview() {
    ArchitectCodersTheme {
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

@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
private fun DialogRequesterPreview() {
    ArchitectCodersTheme {
        RequesterContent(
            UiState(
                locationUpdates = emptyList(),
                updatesRunning = false,
                locationRationaleNeeded = true,
                updatesTrashRequested = false,
            ),
            MultiplePermissionsStatePreview(),
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
private fun FilledRequesterPreview() {
    ArchitectCodersTheme {
        RequesterContent(
            UiState(
                locationUpdates = listOf(
                    MyLocation(
                        latitude = "40.42189",
                        longitude = "-3.682189",
                        timeStamp = "0",
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

@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
private fun TrashRequestedPreview() {
    ArchitectCodersTheme {
        RequesterContent(
            UiState(
                locationUpdates = listOf(
                    MyLocation(
                        latitude = "40.42189",
                        longitude = "-3.682189",
                        timeStamp = "0",
                    )
                ),
                updatesRunning = true,
                locationRationaleNeeded = false,
                updatesTrashRequested = true,
            ),
            MultiplePermissionsStatePreview(),
        )
    }
}
