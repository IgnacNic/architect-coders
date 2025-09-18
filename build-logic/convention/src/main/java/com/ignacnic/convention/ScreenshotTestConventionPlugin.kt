package com.ignacnic.convention

import com.ignacnic.convention.utils.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class ScreenshotTestConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            dependencies {
                add("testImplementation", libs.findLibrary("paparazzi").get())
            }
        }
    }

}
