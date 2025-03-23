package com.ignacnic.architectcoders

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ignacnic.architectcoders.data.locationRepository
import com.ignacnic.architectcoders.ui.screens.home.HomeScreen
import com.ignacnic.architectcoders.ui.screens.location.singlelocation.SingleLocationDisplay
import com.ignacnic.architectcoders.ui.screens.location.singlelocation.SingleLocationDisplayViewModel
import com.ignacnic.architectcoders.ui.screens.location.updatedlocation.UpdatedLocationDisplay
import com.ignacnic.architectcoders.ui.screens.location.updatedlocation.UpdatedLocationDisplayViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = "home") {
                composable("home") {
                    HomeScreen(
                        onCurrentLocationClick = { navController.navigate("single_location") },
                        onUpdatedLocationClick = { navController.navigate("updated_location") }
                    )
                }
                composable("single_location") {
                    SingleLocationDisplay(
                        vm = viewModel { SingleLocationDisplayViewModel(locationRepository) }
                    )
                }
                composable("updated_location") {
                    UpdatedLocationDisplay(
                        vm = viewModel { UpdatedLocationDisplayViewModel(locationRepository) }
                    )
                }
            }
        }
    }
}