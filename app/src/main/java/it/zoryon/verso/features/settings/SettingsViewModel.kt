package it.zoryon.verso.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.zoryon.verso.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SettingsState {
    MAIN_MENU,
    DOWNLOAD_FOLDER
}

@HiltViewModel
class SettingsViewModel @Inject constructor(private val repo: SettingsRepository) : ViewModel() {
    private val _currentScreenState = MutableStateFlow(SettingsState.MAIN_MENU)
    val currentScreenState: StateFlow<SettingsState> = _currentScreenState.asStateFlow()

    // Current download path
     val downloadPath: StateFlow<String> = repo.downloadPath
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            "Caricamento..."
        )

    fun navigateToDownload() {
        _currentScreenState.value = SettingsState.DOWNLOAD_FOLDER
    }

    fun navigateBack() {
        _currentScreenState.value = SettingsState.MAIN_MENU
    }

    fun updateDownloadPath(newPath: String) {
        viewModelScope.launch {
            repo.saveDownloadPath(newPath)
        }
    }
}