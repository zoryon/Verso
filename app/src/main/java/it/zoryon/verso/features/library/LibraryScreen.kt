package it.zoryon.verso.features.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController

@Composable
fun LibraryScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text("Library Screen", textAlign = TextAlign.Center)
    }
}