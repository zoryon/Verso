package it.zoryon.verso.domain.model

data class YouTubeVideoModel(
    val id: String,
    val title: String,
    val author: String,
    val thumbnailUrl: String,
    val duration: String
)