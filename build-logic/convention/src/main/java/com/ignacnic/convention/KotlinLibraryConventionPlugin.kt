package com.ignacnic.convention

import com.android.build.api.dsl.LibraryExtension
import com.ignacnic.convention.utils.libs
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.apply

class KotlinLibraryConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            apply(plugin = "java-library")
            apply(plugin = libs.findPlugin("jetbrains-kotlin-jvm").get().get().pluginId)

            configureKotlinLibrary()
        }
    }

    private fun Project.configureKotlinLibrary() {
        extensions.getByType(JavaPluginExtension::class).apply {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11

            tasks.withType<KotlinCompile>().configureEach {
                compilerOptions.jvmTarget.set(JvmTarget.JVM_11)
            }
        }
    }
}
