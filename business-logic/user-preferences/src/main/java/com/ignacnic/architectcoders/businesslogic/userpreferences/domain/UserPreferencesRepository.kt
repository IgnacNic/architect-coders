package com.ignacnic.architectcoders.businesslogic.userpreferences.domain

interface UserPreferencesRepository {
    fun getString(key: String, default: String): String
    fun putString(key: String, value: String)
}
