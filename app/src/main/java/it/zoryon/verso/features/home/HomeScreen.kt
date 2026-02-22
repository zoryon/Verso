package it.zoryon.verso.features.home

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import it.zoryon.verso.core.ui.components.SearchBar
import it.zoryon.verso.core.ui.components.VideoResultRow
import it.zoryon.verso.core.ui.theme.TextSecondary
import it.zoryon.verso.features.player.GlobalPlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    globalPlayerViewModel: GlobalPlayerViewModel,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val playerState by globalPlayerViewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            SearchBar(
                query = state.query,
                onQueryChange = { viewModel.onQueryChange(it) },
                placeholder = "Cerca..."
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = if (playerState.currentVideo != null) 140.dp else 100.dp)
            ) {
                itemsIndexed(state.results) { index, video ->
                    VideoResultRow(
                        video = video,
                        onPlay = { globalPlayerViewModel.playVideo(video, contextPlaylist = state.results) },
                        onDownload = { viewModel.fetchAudioForDownload(video) }
                    )

                    // Trigger to load new videos once arrived at the end of the page
                    LaunchedEffect(state.results.size) {
                        if (state.results.isNotEmpty() &&
                            state.results.size < 30 &&
                            !state.isLoading) {
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
    }
}
