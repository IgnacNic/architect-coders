package com.ignacnic.architectcoders.navigation

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ignacnic.architectcoders.di.elevationRepository
import com.ignacnic.architectcoders.entities.location.MyLocation
import com.ignacnic.architectcoders.feature.location.detail.LocationDetailScreen
import com.ignacnic.architectcoders.feature.location.detail.LocationDetailScreenViewModel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

fun NavGraphBuilder.locationDetailNode(appNavController: NavController) {
    composable<Screens.LocationDetail>(
        typeMap = mapOf(typeOf<MyLocationNavData>() to MyLocationNavType)
    ) {
        val args = it.toRoute<Screens.LocationDetail>()
        LocationDetailScreen(
            vm = viewModel {
                LocationDetailScreenViewModel(args.locationNavData.toEntity(), elevationRepository)
            },
            onBack = appNavController::popBackStack,
        )
    }
}

@Serializable
@Parcelize
class MyLocationNavData(
    val latitude: String,
    val longitude: String,
    val timeStamp: String,
    val elevation: String?,
) : Parcelable

fun MyLocationNavData.toEntity() = MyLocation(
    latitude.toDouble(),
    longitude.toDouble(),
    timeStamp.toLong(),
    elevation?.toDouble(),
)

fun MyLocation.toNavData() = MyLocationNavData(
    latitude.toString(),
    longitude.toString(),
    timeStamp.toString(),
    elevation.toString(),
)

val MyLocationNavType = object : NavType<MyLocationNavData>(
    isNullableAllowed = false
) {
    override fun get(bundle: Bundle, key: String): MyLocationNavData? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(
                key, MyLocationNavData::class.java
            )
        } else {
            bundle.getParcelable(key)
        }
    }

    override fun parseValue(value: String): MyLocationNavData {
        return Json.decodeFromString<MyLocationNavData>(value)
    }

    override fun serializeAsValue(value: MyLocationNavData): String {
        return Json.encodeToString(value)
    }

    override fun put(bundle: Bundle, key: String, value: MyLocationNavData) {
        bundle.putParcelable(key, value)
    }

}
