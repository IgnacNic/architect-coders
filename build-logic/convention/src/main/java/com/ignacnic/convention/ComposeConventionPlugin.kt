package com.ignacnic.convention

import com.android.build.api.dsl.CommonExtension
import com.ignacnic.convention.utils.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class ComposeConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.run {
                apply(plugin = libs.findPlugin("kotlin.compose").get().get().pluginId)
            }
            configureCompose()
            setupDependencies()
        }
    }

    private fun Project.configureCompose() {
        extensions.getByType(CommonExtension::class).apply {
            buildFeatures {
                compose = true
            }
        }
    }

    private fun Project.setupDependencies() {
        dependencies {
            val bom = libs.findLibrary("androidx-compose-bom").get()
            add("implementation", platform(bom))
            add("androidTestImplementation", platform(bom))
            add("implementation", libs.findLibrary("androidx-ui").get())
            add("implementation", libs.findLibrary("androidx-ui-graphics").get())
            add("implementation", libs.findLibrary("androidx-ui-tooling-preview").get())
            add("implementation", libs.findLibrary("androidx-navigation-compose").get())
            add("implementation", libs.findLibrary("androidx-material3").get())
            add("implementation", libs.findLibrary("androidx-ui-test-manifest").get())
            add("implementation", libs.findLibrary("androidx-ui-test-junit4").get())
            add("debugImplementation", libs.findLibrary("androidx-ui-tooling").get())
        }
    }
}
