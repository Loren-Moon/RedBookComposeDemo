package com.loren.buildlogic.convention

enum class RedBookBuildType(val applicationIdSuffix: String? = null) {
    DEBUG(".debug"),
    RELEASE,
}
