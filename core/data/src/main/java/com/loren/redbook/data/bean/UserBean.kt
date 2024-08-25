package com.loren.redbook.data.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserBean(
    val headImg: String,
    val userName: String
) : Parcelable
