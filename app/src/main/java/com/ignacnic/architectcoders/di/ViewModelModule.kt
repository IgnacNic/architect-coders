package com.ignacnic.architectcoders.di

import com.ignacnic.architectcoders.feature.location.detail.LocationDetailScreenViewModel
import com.ignacnic.architectcoders.feature.location.requester.LocationRequesterScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::LocationRequesterScreenViewModel)
    viewModel { location ->
        LocationDetailScreenViewModel(location.get(), get())
    }
}
