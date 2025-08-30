package com.ignacnic.architectcoders.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ignacnic.architectcoders.feature.location.requester.LocationRequesterScreen
import com.ignacnic.architectcoders.feature.location.requester.LocationRequesterScreenViewModel
import com.ignacnic.architectcoders.feature.location.requester.LocationRequesterScreenViewModel.SideEffect.NavigateToLocationDetails
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.locationRequesterNode(appNavController: NavController) {
    composable<Screens.LocationRequester> {
        val viewModel: LocationRequesterScreenViewModel = koinViewModel()
        LocationRequesterScreen(
            vm = viewModel,
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

