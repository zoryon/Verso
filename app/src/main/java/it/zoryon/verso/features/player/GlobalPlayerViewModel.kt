package it.zoryon.verso.features.player

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import it.zoryon.verso.core.utils.ytExtractor.YtExtractorEngine
import it.zoryon.verso.domain.cache.AudioCacheManager
import it.zoryon.verso.domain.model.GlobalPlayerState
import it.zoryon.verso.domain.model.YouTubeVideoModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalPlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(GlobalPlayerState())
    val state: StateFlow<GlobalPlayerState> = _state.asStateFlow()

    private val player = ExoPlayer.Builder(context).build()
    private val audioCache = AudioCacheManager()

    // Keep track of the current playlist and index to handle context-aware Next/Previous
    private var currentPlaylist: List<YouTubeVideoModel> = emptyList()
    private var currentIndex: Int = -1

    init {
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

    // Can be called from ANY screen to play a video and set the contextual playlist
    fun playVideo(video: YouTubeVideoModel, contextPlaylist: List<YouTubeVideoModel>) {
        currentPlaylist = contextPlaylist
        currentIndex = currentPlaylist.indexOf(video)

        viewModelScope.launch {
            try {
                // Check if it's a local file (from Library) or remote (from Home)
                // Local files from DocumentFile usually start with content://
                val isLocalFile = video.id.startsWith("content://") || video.id.startsWith("file://")

                val mediaUri = if (isLocalFile) {
                    Uri.parse(video.id)
                } else {
                    val audioUrl = audioCache.getOrPut(video.id) {
                        YtExtractorEngine.getAudioUrl(video.id)
                    }
                    if (audioUrl != null) Uri.parse(audioUrl) else null
                }

                if (mediaUri != null) {
                    val mediaItem = MediaItem.fromUri(mediaUri)
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.play()
                    _state.update { it.copy(currentVideo = video, errorMessage = null) }
                } else {
                    _state.update { it.copy(errorMessage = "Audio stream not available") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = "Impossible to fetch audio") }
            }
        }
    }

    fun togglePlayPause() {
        if (player.isPlaying) player.pause() else player.play()
    }

    fun playNext() {
        if (currentPlaylist.isEmpty() || currentIndex == -1) return
        val nextIndex = currentIndex + 1
        if (nextIndex < currentPlaylist.size) {
            playVideo(currentPlaylist[nextIndex], currentPlaylist)
        }
    }

    fun playPrevious() {
        if (currentPlaylist.isEmpty() || currentIndex == -1) return
        val prevIndex = currentIndex - 1
        if (prevIndex >= 0) {
            playVideo(currentPlaylist[prevIndex], currentPlaylist)
        }
    }

    fun seekTo(position: Float) {
        val duration = player.duration
        if (duration <= 0) return
        val newPosition = (duration * position).toLong()
        player.seekTo(newPosition)
    }

    fun setPlayerExpanded(expanded: Boolean) {
        _state.update { it.copy(isPlayerExpanded = expanded) }
    }

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

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}