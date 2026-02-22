package it.zoryon.verso.features.library

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import it.zoryon.verso.domain.model.YouTubeVideoModel
import it.zoryon.verso.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.net.toUri

@HiltViewModel
class LibraryViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _songs = MutableStateFlow<List<YouTubeVideoModel>>(emptyList())
    val songs: StateFlow<List<YouTubeVideoModel>> = _songs.asStateFlow()

    init {
        loadSongs()
    }

    fun loadSongs() {
        viewModelScope.launch {
            val treeUriString = settingsRepository.downloadPath.first()
            if (treeUriString == "Seleziona cartella...") return@launch

            val treeUri = treeUriString.toUri()
            val directory = DocumentFile.fromTreeUri(context, treeUri) ?: return@launch
            val files = directory.listFiles().filter { it.type == "audio/mpeg" || it.name?.endsWith(".mp3") == true }

            val mapped = files.map { file ->
                val retriever = android.media.MediaMetadataRetriever()
                var title = file.name ?: "Unknown"
                var author = "Unknown Artist"

                try {
                    retriever.setDataSource(context, file.uri)
                    title = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_TITLE) ?: title
                    author = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Local file"
                } catch (e: Exception) {
                    // Fallback to filename if file is corrupted or has no tags
                } finally {
                    retriever.release()
                }

                YouTubeVideoModel(
                    id = file.uri.toString(), // the content:// URI for the player
                    title = title,
                    author = author,
                    thumbnailUrl = file.uri.toString(), // use the URI itself to load the embedded art
                    duration = ""
                )
            }

            _songs.value = mapped
        }
    }
}