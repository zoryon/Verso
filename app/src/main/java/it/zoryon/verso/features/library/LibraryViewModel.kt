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

    private fun loadSongs() {
        viewModelScope.launch {
            val treeUriString = settingsRepository.downloadPath.first()

            if (treeUriString == "Seleziona cartella...") return@launch

            val treeUri = treeUriString.toUri()
            val directory = DocumentFile.fromTreeUri(context, treeUri) ?: return@launch

            val files = directory.listFiles()
                .filter { it.type == "audio/mpeg" }

            val mapped = files.map {
                YouTubeVideoModel(
                    id = it.uri.toString(),
                    title = it.name ?: "Unknown",
                    author = "Local file",
                    thumbnailUrl = "",
                    duration = ""
                )
            }

            _songs.value = mapped
        }
    }
}