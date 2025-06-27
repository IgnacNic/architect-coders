package com.ignacnic.architectcoders.domain.userpreferences.data

import android.app.Application
import android.content.Context
import androidx.core.content.edit

class SharedPreferencesDataSource(
    app: Application
) {
    private val sharedPreferences = app.getSharedPreferences(
        SHARED_PREFERENCE_FILE_KEY,
        Context.MODE_PRIVATE,
    )

    fun getString(key: String, defaultValue: String) =
        sharedPreferences.getString(
            key,
            defaultValue,
        ) ?: defaultValue

    fun putString(key: String, value: String) {
        sharedPreferences.edit {
            putString(key, value)
        }
    }


    companion object {
        private const val SHARED_PREFERENCE_FILE_KEY = "com.ignacnic.architectcoders.PREFERENCE_FILE_KEY"
    }
}
