package com.ignacnic.architectcoders.di

import com.google.android.gms.location.LocationServices
import com.ignacnic.architectcoders.AppBuildConfigFieldsProvider
import com.ignacnic.architectcoders.common.filemanager.usecases.WriteToFileUseCase
import com.ignacnic.architectcoders.businesslogic.elevation.data.ElevationClient
import com.ignacnic.architectcoders.businesslogic.elevation.data.ElevationRepositoryImpl
import com.ignacnic.architectcoders.businesslogic.elevation.domain.ElevationRepository
import com.ignacnic.architectcoders.businesslogic.gpxfile.data.GPXFileRepositoryImpl
import com.ignacnic.architectcoders.businesslogic.gpxfile.domain.GPXFileRepository
import com.ignacnic.architectcoders.businesslogic.location.data.LocationRepositoryImpl
import com.ignacnic.architectcoders.businesslogic.location.domain.LocationRepository
import com.ignacnic.architectcoders.businesslogic.userpreferences.data.SharedPreferencesDataSource
import com.ignacnic.architectcoders.businesslogic.userpreferences.data.UserPreferencesRepositoryImpl
import com.ignacnic.architectcoders.businesslogic.userpreferences.domain.UserPreferencesRepository
import com.ignacnic.architectcoders.entities.buildconfig.BuildConfigFieldsProvider

val locationRepository: LocationRepository = LocationRepositoryImpl(
    LocationServices.getFusedLocationProviderClient(Initializer.app)
)
val elevationRepository: ElevationRepository = ElevationRepositoryImpl(
    elevationHttpService = ElevationClient.instance
)

val sharedPreferencesDataSource = SharedPreferencesDataSource(Initializer.app)

val userPreferencesRepository: UserPreferencesRepository = UserPreferencesRepositoryImpl(
    sharedPreferencesDataSource = sharedPreferencesDataSource
)

val buildConfigFieldsProvider: BuildConfigFieldsProvider = AppBuildConfigFieldsProvider()

val writeToFileUseCase: WriteToFileUseCase = WriteToFileUseCase(Initializer.app.contentResolver)

val gpxFileRepository: GPXFileRepository = GPXFileRepositoryImpl(buildConfigFieldsProvider)
