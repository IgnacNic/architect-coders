dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml")) // Create this file if not present.
        }
    }
}

rootProject.name = "build-logic"
include(":convention")
