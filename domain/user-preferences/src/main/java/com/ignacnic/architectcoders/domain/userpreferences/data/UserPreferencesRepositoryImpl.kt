package com.ignacnic.architectcoders.domain.userpreferences.data

import com.ignacnic.architectcoders.domain.userpreferences.domain.UserPreferencesRepository

class UserPreferencesRepositoryImpl(
    private val sharedPreferencesDataSource: SharedPreferencesDataSource
) : UserPreferencesRepository {

    override fun getString(key: String, default: String) = sharedPreferencesDataSource
        .getString(key, default)

    override fun putString(key: String, value: String) = sharedPreferencesDataSource
        .putString(key, value)
}
