package it.zoryon.verso.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.zoryon.verso.core.utils.ytExtractor.YtExtractorEngine
import it.zoryon.verso.domain.model.HomeStateModel
import it.zoryon.verso.domain.model.YouTubeVideoModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(HomeStateModel())
    val state: StateFlow<HomeStateModel> = _state.asStateFlow()

    private var currentQuery = ""
    private val MAX_RESULTS = 30

    private val _searchQuery = MutableStateFlow("")

    init {
        println("DEBUG: HomeViewModel Inizializzato")
        setupSearchDebounce()
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
                println("DEBUG: Audio URL ottenuto: $audioUrl")
                _state.update { it.copy(currentlyPlayingUrl = audioUrl) }
                // HERE WE WILL CALL OUR PLAYER FOR THE AUDIO
            } catch (e: Exception) {
                println("DEBUG: ERRORE fetchAudio: ${e.message}")
                _state.update { it.copy(errorMessage = "Impossibile recuperare l'audio") }
            }
        }
    }
}