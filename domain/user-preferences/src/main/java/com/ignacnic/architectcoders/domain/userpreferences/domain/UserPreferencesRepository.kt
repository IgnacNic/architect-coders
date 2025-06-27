package com.ignacnic.architectcoders.domain.userpreferences.domain

interface UserPreferencesRepository {
    fun getString(key: String, default: String): String
    fun putString(key: String, value: String)
}
