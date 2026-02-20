package it.zoryon.verso.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.zoryon.verso.core.ui.theme.Primary
import it.zoryon.verso.core.ui.theme.TextPrimary
import it.zoryon.verso.core.ui.theme.TextSecondary
import it.zoryon.verso.domain.model.SettingsItemModel

@Composable
fun SettingsRow(
    item: SettingsItemModel,
    onClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(item.id) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(item.iconContainerColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = item.title,
                color = TextPrimary,
                fontSize = 16.sp
            )
            Text(
                text = item.subtitle,
                color = TextSecondary,
                fontSize = 13.sp
            )
        }
    }
}