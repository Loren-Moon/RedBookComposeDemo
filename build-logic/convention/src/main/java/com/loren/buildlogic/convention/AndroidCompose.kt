package com.loren.buildlogic.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *>,
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = libs.findVersion("kotlinCompilerExtensionVersion").get().toString()
        }

        /**
         * implementation(platform(libs.androidx.compose.bom))
         * implementation("androidx.compose.ui:ui")
         * implementation("androidx.compose.ui:ui-graphics")
         * implementation("androidx.compose.ui:ui-tooling-preview")
         * implementation("androidx.compose.material3:material3")
         * androidTestImplementation(platform(libs.androidx.compose.bom))
         * androidTestImplementation(libs.androidx.compose.ui.test)
         * debugImplementation(libs.androidx.compose.ui.tooling)
         * debugImplementation(libs.androidx.compose.ui.testManifest)
         */
        dependencies {
            val bom = libs.findLibrary("androidx-compose-bom").get()
            add("implementation", platform(bom))
            add("implementation", libs.findLibrary("androidx-compose-ui-ui").get())
            add("implementation", libs.findLibrary("androidx-compose-ui-graphics").get())
            add("implementation", libs.findLibrary("androidx-compose-ui-tooling-preview").get())
            add("implementation", libs.findLibrary("androidx-compose-material3").get())
            add("implementation", libs.findLibrary("androidx-hilt-navigation-compose").get())
            add("implementation", libs.findLibrary("androidx-navigation-compose").get())
            add("implementation", libs.findLibrary("androidx-lifecycle-runtimeCompose").get())
            add("androidTestImplementation", platform(bom))
            add("androidTestImplementation", libs.findLibrary("androidx-compose-ui-test").get())
            add("debugImplementation", libs.findLibrary("androidx-compose-ui-tooling").get())
            add("debugImplementation", libs.findLibrary("androidx-compose-ui-testManifest").get())
        }
    }

}
