package com.ignacnic.architectcoders

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.ignacnic.architectcoders.data.elevationRepository
import com.ignacnic.architectcoders.data.locationRepository
import com.ignacnic.architectcoders.domain.location.MyLocation
import com.ignacnic.architectcoders.domain.location.MyLocationType
import com.ignacnic.architectcoders.ui.screens.location.detail.LocationDetailScreen
import com.ignacnic.architectcoders.ui.screens.location.detail.LocationDetailScreenViewModel
import com.ignacnic.architectcoders.ui.screens.location.requester.LocationRequesterScreen
import com.ignacnic.architectcoders.ui.screens.location.requester.LocationRequesterScreenViewModel
import kotlin.reflect.typeOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = Screens.LocationRequester) {
                composable<Screens.LocationRequester> {
                    LocationRequesterScreen(
                        vm = viewModel { LocationRequesterScreenViewModel(locationRepository) },
                        onCardClick = { location ->
                            navController.navigate(
                                Screens.LocationDetail(location = location)
                            )
                        },
                    )
                }
                composable<Screens.LocationDetail>(
                    typeMap = mapOf(typeOf<MyLocation>() to MyLocationType)
                ) {
                    val args = it.toRoute<Screens.LocationDetail>()
                    LocationDetailScreen(
                        vm = viewModel {
                            LocationDetailScreenViewModel(args.location, elevationRepository)
                        },
                        onBack = navController::popBackStack,
                    )
                }
            }
        }
    }
}