import com.android.build.api.dsl.LibraryExtension
import com.loren.buildlogic.convention.configureKotlinAndroid
import com.loren.buildlogic.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dsl.dependencies.ProjectFinder
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.android")
                apply("redBook.android.library.compose")
                apply("redBook.android.hilt")
                apply("redBook.android.test")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    consumerProguardFiles("consumer-rules.pro")
                }
            }
        }
    }
}