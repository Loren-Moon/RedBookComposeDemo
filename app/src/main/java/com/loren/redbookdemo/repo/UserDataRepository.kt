package com.loren.redbookdemo.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.loren.redbook.theme.AppThemeType
import com.loren.redbookdemo.ui.viewmodel.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserDataRepository @Inject constructor(
    private val userPreferences: DataStore<Preferences>
) {
    val userData: Flow<UserData> = userPreferences.data.map {
        UserData(
            AppThemeType.formatTheme(it[intPreferencesKey("theme")])
        )
    }

    suspend fun updateThemeConfig(appThemeType: AppThemeType) {
        userPreferences.edit {
            it[intPreferencesKey("theme")] = appThemeType.ordinal
        }
    }
}