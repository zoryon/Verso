package it.zoryon.verso.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import it.zoryon.verso.domain.model.YouTubeVideoModel

@Composable
fun FullPlayer(
    video: YouTubeVideoModel,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onCollapse: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 65.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Handle
        Box(
            modifier = Modifier
                .size(width = 40.dp, height = 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.Gray.copy(alpha = 0.5f))
                .clickable(onClick = onCollapse)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Big Thumbnail
        AsyncImage(
            model = video.thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Title and Author (Aligned to left)
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = video.title,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = video.author,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Slider
        Slider(
            value = 0f,
            onValueChange = {},
            colors = SliderDefaults.colors(thumbColor = Color.Red, activeTrackColor = Color.Red)
        )

        // Commands row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Skip previous icon
            IconButton(onClick = {}) {
                Icon(Icons.Default.SkipPrevious, contentDescription = null, modifier = Modifier.size(48.dp))
            }

            // Action buttons
            Surface(
                onClick = onPlayPauseClick,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(84.dp).padding(16.dp)
                )
            }

            // Skip next icon
            IconButton(onClick = {}) {
                Icon(Icons.Default.SkipNext, contentDescription = null, modifier = Modifier.size(48.dp))
            }
        }
    }
}