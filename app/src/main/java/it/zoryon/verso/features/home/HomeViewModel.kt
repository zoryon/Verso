package it.zoryon.verso.features.home

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import it.zoryon.verso.core.utils.ytExtractor.YtExtractorEngine
import it.zoryon.verso.domain.cache.AudioCacheManager
import it.zoryon.verso.domain.model.HomeStateModel
import it.zoryon.verso.domain.model.YouTubeVideoModel
import it.zoryon.verso.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.net.URL
import javax.inject.Inject
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.withContext
import androidx.core.net.toUri

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeStateModel())
    val state: StateFlow<HomeStateModel> = _state.asStateFlow()

    private val MAX_RESULTS = 30

    private val _searchQuery = MutableStateFlow("")
    private val player = ExoPlayer.Builder(context).build()
    private val audioCache = AudioCacheManager()

    init {
        setupSearchDebounce()
        setupPlayerListener()
        startProgressUpdater()
    }

    private fun setupPlayerListener() {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.update { it.copy(isPlaying = isPlaying) }
            }
        })
    }

    @OptIn(FlowPreview::class)
    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _searchQuery
                .debounce(800) // managing "Debounce": wait 800ms after the user stop typing to start searching
                .filter { it.isNotEmpty() } // search if there's at least 1 character
                .distinctUntilChanged()
                .collect { query ->
                    performSearch(query, isNextPage = false)
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _state.update { it.copy(query = newQuery) }
        _searchQuery.value = newQuery
    }

    fun performSearch(query: String, isNextPage: Boolean) {
        if (_state.value.isLoading || (isNextPage && _state.value.results.size >= MAX_RESULTS)) {
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val newVideos = YtExtractorEngine.search(query)

                _state.update { s ->
                    val updatedList = if (isNextPage) s.results + newVideos else newVideos
                    s.copy(
                        results = updatedList.take(MAX_RESULTS),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { it.copy(isLoading = false, errorMessage = "Errore durante la ricerca") }
            }
        }
    }

    fun fetchAudioAndPlay(video: YouTubeVideoModel) {
        viewModelScope.launch {
            try {
                val audioUrl = audioCache.getOrPut(video.id) {
                    YtExtractorEngine.getAudioUrl(video.id)
                }
                val index = _state.value.results.indexOf(video)
                if (index == -1) return@launch

                if (audioUrl != null) {
                    val mediaItem = MediaItem.fromUri(audioUrl)
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.play()
                    _state.update {it.copy(
                        currentVideo = video,
                        currentIndex = index
                    )}
                } else {
                    _state.update { it.copy(errorMessage = "Nessun flusso audio disponibile") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = "Impossibile recuperare l'audio") }
            }
        }
    }

    fun fetchAudioForDownload(video: YouTubeVideoModel) {
        viewModelScope.launch(Dispatchers.IO) {
            // Get download folder
            val treeUriString = settingsRepository.downloadPath.first()

            if (treeUriString == "Seleziona cartella...") return@launch

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Download iniziato: ${video.title}", Toast.LENGTH_SHORT).show()
            }

            try {
                val audioUrl = audioCache.getOrPut(video.id) {
                    YtExtractorEngine.getAudioUrl(video.id)
                } ?: return@launch

                val treeUri = treeUriString.toUri()

                val pickedDir = DocumentFile.fromTreeUri(context, treeUri)
                    ?: return@launch

                // sanitize filename (avoid illegal characters)
                val safeTitle = video.title
                    .replace("[^a-zA-Z0-9._-]".toRegex(), "_")

                val newFile = pickedDir.createFile(
                    "audio/mpeg",
                    "$safeTitle.mp3"
                ) ?: return@launch

                URL(audioUrl).openStream().use { input ->
                    context.contentResolver.openOutputStream(newFile.uri)?.use { output ->
                        input.copyTo(output)
                    }
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Download completato: ${video.title}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Download fallito: ${video.title}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun togglePlayPause() {
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    // Expand the MiniPlayer into a Modal Bottom Sheet
    fun setPlayerExpanded(expanded: Boolean) {
        _state.update { it.copy(isPlayerExpanded = expanded) }
    }

    // Progress bar for songs' duration
    private fun startProgressUpdater() {
        viewModelScope.launch {
            while (true) {
                if (player.isPlaying) {
                    _state.update {
                        it.copy(
                            playbackPosition = player.currentPosition,
                            duration = if (player.duration > 0) player.duration else 0L
                        )
                    }
                }
                kotlinx.coroutines.delay(500)
            }
        }
    }

    fun playNext() {
        val state = _state.value
        val nextIndex = state.currentIndex + 1
        if (nextIndex >= state.results.size) return

        fetchAudioAndPlay(state.results[nextIndex])
    }

    fun playPrevious() {
        val state = _state.value
        val prevIndex = state.currentIndex - 1
        if (prevIndex < 0) return

        fetchAudioAndPlay(state.results[prevIndex])
    }

    fun seekTo(position: Float) {
        val duration = player.duration
        if (duration <= 0) return
        val newPosition = (duration * position).toLong()
        player.seekTo(newPosition)
    }

    // Release resources when app/screen is left
    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}