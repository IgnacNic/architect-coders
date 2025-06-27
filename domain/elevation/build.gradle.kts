plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.ignacnic.architectcoders.domain.elevation"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ignacnic.architectcoders.domain.elevation"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":common:entities"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.logging.interceptor)
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    testImplementation(libs.mockk)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
