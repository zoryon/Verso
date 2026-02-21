package it.zoryon.verso.core.utils

import androidx.compose.material.icons.Icons
import it.zoryon.verso.domain.model.SettingsItemModel
import androidx.compose.material.icons.filled.Build
import it.zoryon.verso.core.ui.theme.IconBackground

object SettingItems {
    val list = listOf(
        SettingsItemModel(
            id = "cartelle",
            title = "Cartelle",
            subtitle = "Gestisci le tue cartelle",
            icon = Icons.Default.Build,
            iconContainerColor = IconBackground
        )
    )
}

