package it.zoryon.verso.domain.model

data class HomeStateModel(
    val query: String = "",
    val results: List<YouTubeVideoModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val currentVideo: YouTubeVideoModel? = null,
    val isPlaying: Boolean = false,

    val isPlayerExpanded: Boolean = false
)