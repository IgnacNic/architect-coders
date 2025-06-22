package com.ignacnic.architectcoders.data.userpreferences

import com.ignacnic.architectcoders.domain.userpreferences.UserPreferencesRepository

class UserPreferencesRepositoryImpl(
    private val sharedPreferencesDataSource: SharedPreferencesDataSource
) : UserPreferencesRepository {

    override fun getString(key: String, default: String) = sharedPreferencesDataSource
        .getString(key, default)

    override fun putString(key: String, value: String) = sharedPreferencesDataSource
        .putString(key, value)
}
