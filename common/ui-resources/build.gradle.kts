plugins {
    alias(libs.plugins.ignacnic.android.library)
    alias(libs.plugins.ignacnic.compose)
    alias(libs.plugins.ignacnic.test.screenshot)
    alias(libs.plugins.paparazzi)
}

android {
    namespace = "com.ignacnic.architectcoders.common.uiresources"
}

dependencies {
    implementation(libs.accompanist.permissions)
}
