plugins {
    alias(libs.plugins.redBook.android.feature)
}

android {
    namespace = "com.loren.redbook.me"

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(projects.feature.base)
    implementation(projects.core.data)
    implementation(libs.coil.compose)
    implementation(projects.theme)
    implementation(libs.androidx.paging.compose)
}