plugins {
    alias(libs.plugins.ignacnic.android.library)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ignacnic.test.unit)
}

android {
    namespace = "com.ignacnic.architectcoders.businesslogic.elevation"
}

dependencies {
    implementation(project(":common:entities"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)
}
