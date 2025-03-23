package com.ignacnic.architectcoders.data

import com.ignacnic.architectcoders.domain.LocationRepository

val locationRepository: LocationRepository = LocationRepositoryImpl(Initializer.app)