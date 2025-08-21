package com.ignacnic.architectcoders.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ignacnic.architectcoders.di.buildConfigFieldsProvider
import com.ignacnic.architectcoders.di.gpxFileRepository
import com.ignacnic.architectcoders.di.locationRepository
import com.ignacnic.architectcoders.di.userPreferencesRepository
import com.ignacnic.architectcoders.di.writeToFileUseCase
import com.ignacnic.architectcoders.feature.location.requester.LocationRequesterScreen
import com.ignacnic.architectcoders.feature.location.requester.LocationRequesterScreenViewModel
import com.ignacnic.architectcoders.feature.location.requester.LocationRequesterScreenViewModel.SideEffect.NavigateToLocationDetails

fun NavGraphBuilder.locationRequesterNode(appNavController: NavController) {
    composable<Screens.LocationRequester> {
        LocationRequesterScreen(
            vm = viewModel { LocationRequesterScreenViewModel(
                locationRepository,
                userPreferencesRepository,
                gpxFileRepository,
                buildConfigFieldsProvider,
                writeToFileUseCase,
            ) },
            onNavigationEffect = { effect ->
                if (effect is NavigateToLocationDetails) {
                    appNavController.navigate(
                        Screens.LocationDetail(locationNavData = effect.location.toNavData())
                    )
                }
            },
        )
    }
}

