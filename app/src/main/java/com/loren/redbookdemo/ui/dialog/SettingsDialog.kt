package com.loren.redbookdemo.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.loren.redbook.theme.RedBookTheme
import com.loren.redbook.theme.AppThemeType
import com.loren.redbookdemo.R
import com.loren.redbookdemo.ui.viewmodel.SettingsUiState
import com.loren.redbookdemo.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsDialog(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val settingsUiState by settingsViewModel.settingsUiState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    AlertDialog(
        containerColor = RedBookTheme.colors.background,
        modifier = Modifier
            .widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        title = {
            Text(stringResource(id = R.string.settings_title), color = RedBookTheme.colors.title)
        },
        text = {
            HorizontalDivider(color = RedBookTheme.colors.theme)
            when (settingsUiState) {
                SettingsUiState.Loading -> {
                    Text(stringResource(id = R.string.loading), color = RedBookTheme.colors.body, style = RedBookTheme.textStyle.bodyMedium)
                }

                is SettingsUiState.Success -> {
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        stringArrayResource(id = R.array.setting_theme_desc).forEachIndexed { index, s ->
                            SettingsDialogThemeChooserRow(
                                text = s,
                                selected = index == (settingsUiState as SettingsUiState.Success).userData.theme.ordinal
                            ) {
                                settingsViewModel.updateThemeConfig(AppThemeType.entries[index])
                            }
                        }
                    }
                }
            }
        },
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Text(
                text = stringResource(R.string.settings_confirm),
                color = RedBookTheme.colors.theme,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { onDismiss() },
            )
        }
    )
}

@Composable
fun SettingsDialogThemeChooserRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick,
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            colors = RadioButtonDefaults.colors(selectedColor = RedBookTheme.colors.theme),
            selected = selected,
            onClick = null,
        )
        Spacer(Modifier.width(8.dp))
        Text(text, color = RedBookTheme.colors.body)
    }
}