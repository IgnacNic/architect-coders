package com.ignacnic.architectcoders

import android.app.Application
import com.ignacnic.architectcoders.data.Initializer

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Initializer.app = this
    }
}
