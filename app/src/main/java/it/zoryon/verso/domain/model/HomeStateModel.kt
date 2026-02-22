package it.zoryon.verso.domain.model

data class HomeStateModel(
    val query: String = "",
    val results: List<YouTubeVideoModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val currentIndex: Int = -1,
    val currentVideo: YouTubeVideoModel? = null,
    val isPlaying: Boolean = false,

    val isPlayerExpanded: Boolean = false,

    val playbackPosition: Long = 0L,
    val duration: Long = 0L
)