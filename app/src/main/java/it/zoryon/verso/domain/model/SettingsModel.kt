package it.zoryon.verso.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

data class SettingsItemModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconContainerColor: androidx.compose.ui.graphics.Color
)