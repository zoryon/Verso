package it.zoryon.verso

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import it.zoryon.verso.core.ui.theme.VersoTheme
import it.zoryon.verso.core.utils.ytExtractor.ExtractorInitializer
import it.zoryon.verso.core.utils.ytExtractor.OkHttpDownloader

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ExtractorInitializer.init(OkHttpDownloader())
        enableEdgeToEdge()
        setContent {
            VersoTheme {
                AppLayout()
            }
        }
    }
}