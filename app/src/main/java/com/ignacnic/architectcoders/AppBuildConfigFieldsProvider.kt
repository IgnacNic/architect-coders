package com.ignacnic.architectcoders

import com.ignacnic.architectcoders.entities.buildconfig.BuildConfigFields
import com.ignacnic.architectcoders.entities.buildconfig.BuildConfigFieldsProvider

class AppBuildConfigFieldsProvider : BuildConfigFieldsProvider {
    override fun getBuildConfig() = BuildConfigFields(
        appId = BuildConfig.APPLICATION_ID
    )
}
