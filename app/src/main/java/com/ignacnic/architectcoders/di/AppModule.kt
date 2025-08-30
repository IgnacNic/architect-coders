package com.ignacnic.architectcoders.di

import com.ignacnic.architectcoders.AppBuildConfigFieldsProvider
import com.ignacnic.architectcoders.entities.buildconfig.BuildConfigFieldsProvider
import org.koin.dsl.module

val appModule = module {
    single<BuildConfigFieldsProvider> {
        AppBuildConfigFieldsProvider()
    }

}
