import com.loren.buildlogic.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidTestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            /**
             * testImplementation(libs.junit4)
             * androidTestImplementation(libs.android.text.ext)
             * androidTestImplementation(libs.androidx.test.espresso)
             */
            dependencies {
                add("testImplementation", libs.findLibrary("junit4").get())
                add("androidTestImplementation", libs.findLibrary("android-test-ext").get())
                add("androidTestImplementation", libs.findLibrary("androidx-test-espresso").get())
            }
        }
    }
}