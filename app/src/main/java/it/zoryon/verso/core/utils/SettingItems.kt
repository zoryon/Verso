package it.zoryon.verso.core.utils

import androidx.compose.material.icons.Icons
import it.zoryon.verso.domain.model.SettingsItemModel
import androidx.compose.material.icons.filled.Build
import it.zoryon.verso.core.ui.theme.IconBackground

object SettingItems {
    val list = listOf(
        SettingsItemModel(
            id = "folder",
            title = "Folder",
            subtitle = "Choose your download folder",
            icon = Icons.Default.Build,
            iconContainerColor = IconBackground
        )
    )
}

