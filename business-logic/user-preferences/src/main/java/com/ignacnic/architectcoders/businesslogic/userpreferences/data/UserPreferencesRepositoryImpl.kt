package com.ignacnic.architectcoders.businesslogic.userpreferences.data

import com.ignacnic.architectcoders.businesslogic.userpreferences.domain.UserPreferencesRepository

class UserPreferencesRepositoryImpl(
    private val sharedPreferencesDataSource: SharedPreferencesDataSource
) : UserPreferencesRepository {

    override fun getString(key: String, default: String) = sharedPreferencesDataSource
        .getString(key, default)

    override fun putString(key: String, value: String) = sharedPreferencesDataSource
        .putString(key, value)
}
