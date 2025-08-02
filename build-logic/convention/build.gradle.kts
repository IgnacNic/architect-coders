plugins {
    `kotlin-dsl`
}

group = "com.ignacnic.convention" // Package name for the our plugins

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        create("androidApplication") {
            id = "com.ignacnic.convention.android.application" // This is the id we used to resolve our plugin.
            implementationClass = "com.ignacnic.convention.AndroidApplicationConventionPlugin"
        }
        create("androidLibrary") {
            id = "com.ignacnic.convention.android.library" // This is the id we used to resolve our plugin.
            implementationClass = "com.ignacnic.convention.AndroidLibraryConventionPlugin"
        }
        create("compose") {
            id = "com.ignacnic.convention.compose" // This is the id we used to resolve our plugin.
            implementationClass = "com.ignacnic.convention.ComposeConventionPlugin"
        }
        create("kotlinLibrary") {
            id = "com.ignacnic.convention.kotlin.library" // This is the id we used to resolve our plugin.
            implementationClass = "com.ignacnic.convention.KotlinLibraryConventionPlugin"
        }
        create("unitTest") {
            id = "com.ignacnic.convention.test" // This is the id we used to resolve our plugin.
            implementationClass = "com.ignacnic.convention.UnitTestConventionPlugin"
        }
    }
}
