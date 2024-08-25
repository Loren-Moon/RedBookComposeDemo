plugins {
    alias(libs.plugins.redBook.android.library)
    alias(libs.plugins.redBook.android.library.compose)
}

android {
    namespace = "com.loren.redbook.theme"

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(libs.material)
    testImplementation(libs.junit4)
    androidTestImplementation(libs.android.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)
    implementation(libs.coil.compose)
    implementation(libs.androidx.paging.compose)
}