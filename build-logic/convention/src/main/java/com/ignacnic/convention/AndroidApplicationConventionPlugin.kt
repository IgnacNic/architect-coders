package com.ignacnic.convention

import com.android.build.api.dsl.ApplicationExtension
import com.ignacnic.convention.utils.findSdkVersion
import com.ignacnic.convention.utils.libs
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("*** AndroidApplicationComposeConventionPlugin invoked ***")
        // Plugins
        with(project) {
            pluginManager.run {
                apply(plugin = "com.android.application")
                apply(plugin = "kotlin-android")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid()
                configureBuildTypes()
            }
        }
    }

    private fun Project.configureKotlinAndroid() {
        extensions.getByType(ApplicationExtension::class).apply {
            compileSdk = libs.findSdkVersion("compileSdk")

            defaultConfig {
                minSdk = libs.findSdkVersion("minSdk")
                targetSdk = libs.findSdkVersion("targetSdk")
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }

            buildFeatures {
                viewBinding = true
                buildConfig = true
            }
        }

        tasks.withType<KotlinCompile>().configureEach {
            compilerOptions.jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    private fun Project.configureBuildTypes() {
        extensions.getByType(ApplicationExtension::class).apply {
            buildTypes {
                release {
                    isMinifyEnabled = false
                }
            }
        }
    }
}
