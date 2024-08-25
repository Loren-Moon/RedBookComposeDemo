import com.loren.buildlogic.convention.RedBookBuildType

plugins {
    alias(libs.plugins.redBook.android.application)
    alias(libs.plugins.redBook.android.application.compose)
    alias(libs.plugins.redBook.android.hilt)
}

android {
    namespace = "com.loren.redbookdemo"

    defaultConfig {
        applicationId = "com.loren.redbookdemo"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("./redbook.jks")
            storePassword = "redbook123"
            keyAlias = "release"
            keyPassword = "redbook123"
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = RedBookBuildType.DEBUG.applicationIdSuffix
        }
        release {
            isMinifyEnabled = true
            applicationIdSuffix = RedBookBuildType.RELEASE.applicationIdSuffix
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }
    packaging {
        resources {
//            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.datastore.preferences)

//    implementation(libs.accompanist.systemuicontroller) // 使用EdgeToEdge

    implementation(libs.androidx.core.splashscreen)

    implementation(projects.theme)
    implementation(projects.core.data)
    implementation(projects.feature.home)
    implementation(projects.feature.shopping)
    implementation(projects.feature.message)
    implementation(projects.feature.me)

    implementation(libs.coil.compose)

    implementation(libs.gson)
    implementation(libs.androidx.animation)
}