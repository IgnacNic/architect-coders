package com.ignacnic.architectcoders.data.elevation

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create

object ElevationClient {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val basicClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor { msg ->
                Log.d("HTTP", msg)
            }.setLevel(HttpLoggingInterceptor.Level.BODY)
        )
        .build()

    val instance = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/v1/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .client(basicClient)
        .build()
        .create<ElevationHttpService>()
}
