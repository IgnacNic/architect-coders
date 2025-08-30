plugins {
    alias(libs.plugins.ignacnic.android.application)
    alias(libs.plugins.ignacnic.compose)
    alias(libs.plugins.kotlinx.serialization)
    id("org.jetbrains.kotlin.plugin.parcelize")
}

android {
    namespace = "com.ignacnic.architectcoders"

    defaultConfig {
        applicationId = "com.ignacnic.architectcoders"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.compose)
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(project(":feature:location"))
    implementation(project(":common:entities"))
    implementation(project(":common:ui-resources"))
    implementation(project(":common:file-manager"))
    implementation(project(":business-logic:elevation"))
    implementation(project(":business-logic:location"))
    implementation(project(":business-logic:user-preferences"))
    implementation(project(":business-logic:gpx-file"))

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.play.services.location)
}
