package it.zoryon.verso.domain.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private val DOWNLOAD_PATH_KEY = stringPreferencesKey("download_path")

    val downloadPath: Flow<String> = context.dataStore.data.map {
        it[DOWNLOAD_PATH_KEY] ?: "Seleziona cartella..."
    }

    // Update path
    suspend fun saveDownloadPath(path: String) {
        context.dataStore.edit { it[DOWNLOAD_PATH_KEY] = path }
    }
}