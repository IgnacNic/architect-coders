package com.ignacnic.architectcoders.domain.userpreferences

interface UserPreferencesRepository {
    fun getString(key: String, default: String): String
    fun putString(key: String, value: String)
}
