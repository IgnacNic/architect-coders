plugins {
    alias(libs.plugins.ignacnic.android.library)
    alias(libs.plugins.ignacnic.compose)
    alias(libs.plugins.ignacnic.test.unit)
    alias(libs.plugins.ignacnic.test.screenshot)
    alias(libs.plugins.paparazzi)
}

android {
    namespace = "com.ignacnic.architectcoders.feature.location"
}

dependencies {

    implementation(project(":common:ui-resources"))
    implementation(project(":common:entities"))
    implementation(project(":common:file-manager"))
    implementation(project(":business-logic:elevation"))
    implementation(project(":business-logic:location"))
    implementation(project(":business-logic:gpx-file"))
    implementation(project(":business-logic:user-preferences"))
    implementation(libs.accompanist.permissions)
}
