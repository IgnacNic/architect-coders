plugins {
    alias(libs.plugins.ignacnic.android.library)
    alias(libs.plugins.ignacnic.test.unit)
}

android {
    namespace = "com.ignacnic.architectcoders.businesslogic.location"
}

dependencies {

    implementation(project(":common:entities"))
    implementation(project(":common:file-manager"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.play.services.location)
}
