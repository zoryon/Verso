package it.zoryon.verso.core.utils.ytExtractor

import it.zoryon.verso.domain.model.YouTubeVideoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.StreamInfoItem

object YtExtractorEngine {

    suspend fun search(query: String): List<YouTubeVideoModel> =
        withContext(Dispatchers.IO) {
            val service = ServiceList.YouTube
            val extractor = service.getSearchExtractor(query)

            extractor.fetchPage()

            extractor.initialPage.items.mapNotNull { item ->
                if (item is StreamInfoItem) {
                    YouTubeVideoModel(
                        id = extractYtVideoId(item.url),
                        url = item.url,
                        title = item.name,
                        author = item.uploaderName ?: "Sconosciuto",
                        thumbnailUrl = item.thumbnails.firstOrNull()?.url ?: "",
                        duration = item.duration.toString()
                    )
                } else null
            }
        }

    suspend fun getAudioUrl(videoUrl: String): String? = withContext(Dispatchers.IO) {
        val service = ServiceList.YouTube
        val extractor = service.getStreamExtractor(videoUrl)

        extractor.fetchPage()

        extractor
            .audioStreams
            .maxByOrNull { it.bitrate }
            ?.content
    }

    private fun extractYtVideoId(url: String): String = android.net.Uri.parse(url).getQueryParameter("v") ?: url
}