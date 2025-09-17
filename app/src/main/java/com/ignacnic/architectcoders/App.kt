package com.ignacnic.architectcoders

import android.app.Application
import com.ignacnic.architectcoders.di.appModule
import com.ignacnic.architectcoders.di.repositoryModule
import com.ignacnic.architectcoders.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(androidContext = this@App)
            modules(
                appModule,
                repositoryModule,
                viewModelModule,
            )
        }
    }
}
