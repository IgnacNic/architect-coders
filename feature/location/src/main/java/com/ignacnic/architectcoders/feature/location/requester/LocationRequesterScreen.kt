package com.ignacnic.architectcoders.feature.location.requester

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
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.ignacnic.architectcoders.common.uiresources.R
import com.ignacnic.architectcoders.common.uiresources.components.RationaleDialog
import com.ignacnic.architectcoders.common.uiresources.components.Screen
import com.ignacnic.architectcoders.common.uiresources.utils.CollectSideEffect
import com.ignacnic.architectcoders.common.uiresources.utils.rememberDocumentPickerActivityLauncher
import com.ignacnic.architectcoders.common.uiresources.utils.rememberDocumentTreeActivityLauncher
import com.ignacnic.architectcoders.common.uiresources.utils.rememberFineLocationPermissionsState
import com.ignacnic.architectcoders.entities.location.MyLocation
import com.ignacnic.architectcoders.feature.location.requester.LocationRequesterScreenViewModel.Action
import com.ignacnic.architectcoders.feature.location.requester.LocationRequesterScreenViewModel.SideEffect
import com.ignacnic.architectcoders.feature.location.requester.LocationRequesterScreenViewModel.UiState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationRequesterScreen(
    vm: LocationRequesterScreenViewModel,
    onNavigationEffect: (SideEffect) -> Unit,
) {
    val state by vm.state.collectAsState()
    val reduce = vm::reduceAction
    val context = LocalContext.current
    val locationPermissionState = rememberFineLocationPermissionsState {
        reduce(Action.PermissionResult(it))
    }
    val documentTreeLauncher = rememberDocumentTreeActivityLauncher { result ->
        reduce(Action.FileDirectoryPicked(result))
    }
    val documentPickerLauncher = rememberDocumentPickerActivityLauncher { result ->
        reduce(Action.FileCreated(result))
    }
    CollectSideEffect(vm.sideEffects) { sideEffect ->
        when (sideEffect) {
            is SideEffect.LaunchAppDetailsSettings -> {
                context.startActivity(
                    Intent(
                        ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts(
                            "package",
                            sideEffect.appId,
                            null,
                        )
                    )
                )
            }

            is SideEffect.LaunchDirectoryPicker -> documentTreeLauncher.launch(null)
            is SideEffect.NavigateToLocationDetails -> onNavigationEffect(sideEffect)
            is SideEffect.LaunchFilePicker -> documentPickerLauncher.launch(sideEffect.uri)
        }
    }

    Screen {
        RequesterContent(state, locationPermissionState, reduce)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun RequesterContent(
    state: UiState,
    locationPermissionState: MultiplePermissionsState,
    reduce: (Action) -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar( title = { Text(stringResource(R.string.record_screen_title)) })
        }
    ) { innerPadding ->
        if (state.locationRationaleNeeded) {
            RationaleDialog(
                title = stringResource(R.string.location_rationale_title),
                description = stringResource(R.string.location_rationale_description),
                onAccept = { reduce(Action.RationaleDialogConfirmed) },
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
                    onCardClick = { location ->
                        reduce(Action.CardClicked(location))
                    },
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
                onClick = {
                    reduce(Action.SaveRoute)
                },
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
                append(item.latitude.toString())
            }
            val longitude = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Longitude: ")
                }
                append(item.longitude.toString())
            }
            val timeStamp = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Timestamp: ")
                }
                append(item.timeStamp.toString())
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
