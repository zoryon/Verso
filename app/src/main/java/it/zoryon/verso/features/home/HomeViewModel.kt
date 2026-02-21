package it.zoryon.verso.features.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import it.zoryon.verso.core.utils.ytExtractor.YtExtractorEngine
import it.zoryon.verso.domain.model.HomeStateModel
import it.zoryon.verso.domain.model.YouTubeVideoModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(HomeStateModel())
    val state: StateFlow<HomeStateModel> = _state.asStateFlow()

    private val MAX_RESULTS = 30

    private val _searchQuery = MutableStateFlow("")
    private val player = ExoPlayer.Builder(context).build()

    init {
        println("DEBUG: HomeViewModel Inizializzato")
        setupPlayerListener()
        setupSearchDebounce()
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
                    println("DEBUG: Debounce scattato per: $query")
                    performSearch(query, isNextPage = false)
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        println("DEBUG: onQueryChange -> $newQuery")
        _state.update { it.copy(query = newQuery) }
        _searchQuery.value = newQuery
    }

    fun performSearch(query: String, isNextPage: Boolean) {
        if (_state.value.isLoading || (isNextPage && _state.value.results.size >= MAX_RESULTS)) {
            println("DEBUG: performSearch BLOCCATO (isLoading=${_state.value.isLoading}, size=${_state.value.results.size})")
            return
        }
        println("DEBUG: Avvio performSearch per: $query (isNextPage=$isNextPage)")

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val newVideos = YtExtractorEngine.search(query)
                println("DEBUG: Engine ha restituito ${newVideos.size} video")

                _state.update { s ->
                    val updatedList = if (isNextPage) s.results + newVideos else newVideos
                    s.copy(
                        results = updatedList.take(MAX_RESULTS),
                        isLoading = false
                    )
                }
                println("DEBUG: Stato aggiornato. Totale risultati in lista: ${_state.value.results.size}")
            } catch (e: Exception) {
                println("DEBUG: ERRORE in performSearch: ${e.message}")
                e.printStackTrace()
                _state.update { it.copy(isLoading = false, errorMessage = "Errore durante la ricerca") }
            }
        }
    }

    fun fetchAudioAndPlay(video: YouTubeVideoModel) {
        println("DEBUG: Richiesta Audio per: ${video.title}")
        viewModelScope.launch {
            try {
                val audioUrl = YtExtractorEngine.getAudioUrl(video.id)
                if (audioUrl != null) {
                    println("DEBUG: Audio URL ottenuto, avvio riproduzione...")

                    val mediaItem = MediaItem.fromUri(audioUrl)
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.play()
                    _state.update { it.copy(currentVideo = video)}
                } else {
                    println("DEBUG: ERRORE: Nessun URL audio restituito")
                    _state.update { it.copy(errorMessage = "Nessun flusso audio disponibile") }
                }
            } catch (e: Exception) {
                println("DEBUG: ERRORE fetchAudio: ${e.message}")
                _state.update { it.copy(errorMessage = "Impossibile recuperare l'audio") }
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

    // Release resources when app/screen is left
    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}