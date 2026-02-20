package it.zoryon.verso.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.zoryon.verso.core.ui.components.SettingsRow
import it.zoryon.verso.core.ui.theme.Secondary
import it.zoryon.verso.core.utils.SettingItems

@Composable
fun SettingsScreen(navController: NavController) {
    val settingsOptions = remember { SettingItems.list }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Menu
        Column(
            modifier = Modifier
                .widthIn(max = 500.dp) // 500dp max on large screens
                .fillMaxWidth() // 100% on small screens
                .clip(RoundedCornerShape(16.dp))
                .background(Secondary)
        ) {
            settingsOptions.forEach { option ->
                SettingsRow(
                    item = option,
                    onClick = { id ->
                        println("Cliccato su: $id")
                    }
                )
            }
        }
    }
}