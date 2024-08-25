import com.android.build.gradle.ProguardFiles.getDefaultProguardFile

plugins {
    alias(libs.plugins.redBook.android.feature)
    id("kotlin-parcelize")
}

android {
    namespace = "com.loren.redbook.data"

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.animation)
}