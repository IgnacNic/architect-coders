pluginManagement {
    includeBuild("build-logic")

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
include(":common:entities")
include(":common:file-manager")
include(":common:ui-resources")
include(":business-logic:elevation")
include(":business-logic:gpx-file")
include(":business-logic:location")
include(":business-logic:user-preferences")
include(":feature:location")
