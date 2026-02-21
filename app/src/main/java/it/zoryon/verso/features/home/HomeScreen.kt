package it.zoryon.verso.features.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import it.zoryon.verso.core.ui.components.FullPlayer
import it.zoryon.verso.core.ui.components.MiniPlayer
import it.zoryon.verso.core.ui.components.SearchBar
import it.zoryon.verso.core.ui.components.VideoResultRow
import it.zoryon.verso.core.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            SearchBar(
                query = state.query,
                onQueryChange = { viewModel.onQueryChange(it) },
                placeholder = "Cerca..."
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = if (state.currentVideo != null) 140.dp else 100.dp)
            ) {
                itemsIndexed(state.results) { index, video ->
                    VideoResultRow(
                        video = video,
                        onPlay = { viewModel.fetchAudioAndPlay(video) },
                        onDownload = { /* Logica download */ }
                    )

                    // Trigger to load new videos once arrived at the end of the page
                    if (index == state.results.size - 1 &&
                        state.results.size < 30 &&
                        !state.isLoading) {
                        LaunchedEffect(Unit) {
                            viewModel.performSearch(state.query, isNextPage = true)
                        }
                    }
                }

                item {
                    if (state.isLoading) {
                        Box(Modifier
                            .fillMaxWidth()
                            .padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        }
                    } else if (state.results.size >= 30) {
                        Text(
                            text = "Fine dei risultati",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Mini Player
        AnimatedVisibility(
            visible = state.currentVideo != null,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            state.currentVideo?.let { video ->
                MiniPlayer(
                    video = video,
                    isPlaying = state.isPlaying,
                    onPlayPauseClick = { viewModel.togglePlayPause() },
                    onExpand = { viewModel.setPlayerExpanded(true) },
                    modifier = Modifier.padding(bottom = 0.dp)
                )
            }
        }

        // Full Player
        if (state.isPlayerExpanded) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.setPlayerExpanded(false) },
                sheetState = sheetState,
                dragHandle = null, // We have a custom handle in the Full Player
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxSize()
            ) {
                state.currentVideo?.let { video ->
                    FullPlayer(
                        video = video,
                        isPlaying = state.isPlaying,
                        onPlayPauseClick = { viewModel.togglePlayPause() },
                        onCollapse = { viewModel.setPlayerExpanded(false) }
                    )
                }
            }
        }
    }
}
