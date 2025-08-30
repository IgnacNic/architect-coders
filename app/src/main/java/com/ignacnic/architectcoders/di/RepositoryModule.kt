package com.ignacnic.architectcoders.di

import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ignacnic.architectcoders.businesslogic.elevation.data.ElevationHttpService
import com.ignacnic.architectcoders.businesslogic.elevation.data.ElevationRepositoryImpl
import com.ignacnic.architectcoders.businesslogic.elevation.domain.ElevationRepository
import com.ignacnic.architectcoders.businesslogic.gpxfile.data.GPXFileRepositoryImpl
import com.ignacnic.architectcoders.businesslogic.gpxfile.domain.GPXFileRepository
import com.ignacnic.architectcoders.businesslogic.location.data.LocationRepositoryImpl
import com.ignacnic.architectcoders.businesslogic.location.domain.LocationRepository
import com.ignacnic.architectcoders.businesslogic.userpreferences.data.SharedPreferencesDataSource
import com.ignacnic.architectcoders.businesslogic.userpreferences.data.UserPreferencesRepositoryImpl
import com.ignacnic.architectcoders.businesslogic.userpreferences.domain.UserPreferencesRepository
import com.ignacnic.architectcoders.common.filemanager.usecases.ReadFromFileUseCase
import com.ignacnic.architectcoders.common.filemanager.usecases.WriteToFileUseCase
import com.ignacnic.architectcoders.entities.buildconfig.BuildConfigFieldsProvider
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.create

val repositoryModule = module {

    single<ElevationRepository> {
        ElevationRepositoryImpl(get<ElevationHttpService>())
    }

    single<ElevationHttpService> {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/v1/")
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .client(get(named(BASIC_CLIENT)))
            .build()
            .create<ElevationHttpService>()
    }

    factory<Json> {
        Json {
            ignoreUnknownKeys = true
        }
    }

    single<OkHttpClient>(named(BASIC_CLIENT)) {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor { msg ->
                    Log.d("HTTP", msg)
                }.setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .build()
    }

    single<GPXFileRepository> {
        GPXFileRepositoryImpl(get<BuildConfigFieldsProvider>())
    }

    single<FusedLocationProviderClient> {
        LocationServices.getFusedLocationProviderClient(androidContext())
    }

    single<LocationRepository> {
        LocationRepositoryImpl(get<FusedLocationProviderClient>())
    }

    single<SharedPreferencesDataSource> {
        SharedPreferencesDataSource(androidApplication())
    }

    single<UserPreferencesRepository> {
        UserPreferencesRepositoryImpl(get<SharedPreferencesDataSource>())
    }

    factory<ReadFromFileUseCase> {
        ReadFromFileUseCase(contentResolver = androidContext().contentResolver)
    }

    factory<WriteToFileUseCase> {
        WriteToFileUseCase(contentResolver = androidContext().contentResolver)
    }
}

const val BASIC_CLIENT = "BASIC_CLIENT"
