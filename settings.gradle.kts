pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ArchitectCoders"
include(":app")
include(":feature:location")
include(":domain:elevation")
include(":domain:location")
include(":common:entities")
include(":domain:user-preferences")
include(":domain:gpx-file")
include(":common:ui-resources")
