package it.zoryon.verso.core.navigation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BottomBarViewModel : ViewModel() {
    // Global state for the navigation
    private val _selectedIndex = MutableStateFlow(0)
    val selectedIndex: StateFlow<Int> = _selectedIndex

    fun selectTab(index: Int) {
        _selectedIndex.value = index
    }
}