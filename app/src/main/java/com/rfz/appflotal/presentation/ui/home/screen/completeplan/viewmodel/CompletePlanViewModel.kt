package com.rfz.appflotal.presentation.ui.home.screen.completeplan.viewmodel

import androidx.lifecycle.ViewModel
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.CompletePlanUiState
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.BottomNavItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CompletePlanViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(CompletePlanUiState())
    val uiState: StateFlow<CompletePlanUiState> = _uiState.asStateFlow()

    fun onNavItemClick(item: BottomNavItems) {
        _uiState.update { currentState ->
            currentState.copy(currentScreen = item)
        }
    }

    // Aquí el usuario agregará la lógica posteriormente
}
