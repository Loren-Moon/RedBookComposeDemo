plugins {
    alias(libs.plugins.redBook.android.feature)
}

android {
    namespace = "com.loren.redbook.shopping"

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(projects.core.data)
}