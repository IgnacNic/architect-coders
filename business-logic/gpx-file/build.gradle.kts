plugins {
    alias(libs.plugins.ignacnic.kotlin.library)
    alias(libs.plugins.ignacnic.test.unit)
}

dependencies {
    implementation(project(":common:entities"))
    implementation(libs.gpxandroidsdk)
}
