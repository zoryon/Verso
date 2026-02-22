package it.zoryon.verso.domain.model

data class GlobalPlayerState(
    val currentVideo: YouTubeVideoModel? = null,
    val isPlaying: Boolean = false,
    val isPlayerExpanded: Boolean = false,
    val playbackPosition: Long = 0L,
    val duration: Long = 0L,
    val errorMessage: String? = null
)