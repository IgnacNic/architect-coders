package com.ignacnic.architectcoders.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ignacnic.architectcoders.ui.screens.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCurrentLocationClick: () -> Unit,
    onUpdatedLocationClick: () -> Unit,
) {
    Screen {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Home Screen")})
            }
        ) { innerPadding ->
            Column(Modifier.padding(innerPadding)) {
                Button(onClick = onCurrentLocationClick) {
                    Text("Current location")
                }
                Button(onClick = onUpdatedLocationClick) {
                    Text("Updated location")
                }
            }
        }
    }
}