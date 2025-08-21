package com.ignacnic.convention

import com.android.build.api.dsl.LibraryExtension
import com.ignacnic.convention.utils.findSdkVersion
import com.ignacnic.convention.utils.libs
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            apply(plugin = "android-library")
            apply(plugin = "kotlin-android")

            configureAndroidLibrary()
            configureDependencies()
        }
    }

    private fun Project.configureAndroidLibrary() {
        extensions.getByType(LibraryExtension::class).apply {
            compileSdk = libs.findSdkVersion("compileSdk")

            defaultConfig {
                minSdk = libs.findSdkVersion("minSdk")
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }

            tasks.withType<KotlinCompile>().configureEach {
                compilerOptions.jvmTarget.set(JvmTarget.JVM_11)
            }

            buildTypes {
                release {
                    isMinifyEnabled = false
                }
            }
        }
    }

    private fun Project.configureDependencies() {
        dependencies {
            add("implementation", libs.findLibrary("androidx-core-ktx").get())
            add("implementation", libs.findLibrary("androidx-appcompat").get())
        }
    }
}
