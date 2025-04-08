package com.ignacnic.architectcoders.ui.location.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ignacnic.architectcoders.R
import com.ignacnic.architectcoders.ui.Screen
import com.ignacnic.architectcoders.ui.location.detail.LocationDetailScreenViewModel.UiState
import com.ignacnic.architectcoders.ui.location.detail.LocationDetailScreenViewModel.Action

@Composable
fun LocationDetailScreen(
    vm: LocationDetailScreenViewModel,
    onBack: () -> Unit,
) {
    val state by vm.state.collectAsState()
    val reduce = vm::reduceAction
    Screen {
        DetailContent(state, onBack, reduce)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailContent(
    state: UiState,
    onBack: () -> Unit,
    reduce: (action: Action) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Location Details") },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.clickable(onClick = onBack)
                    )
                }
            )
        }
    ) { paddingValues ->
        LaunchedEffect(null) {
            reduce(Action.onUiLoad)
        }
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_location),
                contentDescription = "Location",
                modifier = Modifier.size(100.dp)
            )
            Row {
                Column {
                    Text(text = "Latitude", fontWeight = FontWeight.Bold)
                    Text(text = "Longitude", fontWeight = FontWeight.Bold)
                    Text(text = "Time", fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(text = ": ${state.latitude}")
                    Text(text = ": ${state.longitude}")
                    Text(text = ": ${state.time}")
                }
            }
            Text(text = "Elevation (in meters):", fontWeight = FontWeight.Bold)
            if (state.loading) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = if (state.elevation != null) {
                        "${state.elevation}"
                    } else {
                        "There was an error fetching your elevation"
                    }
                )
            }
        }
    }
}
