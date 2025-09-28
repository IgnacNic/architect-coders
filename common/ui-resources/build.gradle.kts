plugins {
    alias(libs.plugins.ignacnic.android.library)
    alias(libs.plugins.ignacnic.compose)
}

android {
    namespace = "com.ignacnic.architectcoders.common.uiresources"
}

dependencies {
    implementation(libs.accompanist.permissions)
}
