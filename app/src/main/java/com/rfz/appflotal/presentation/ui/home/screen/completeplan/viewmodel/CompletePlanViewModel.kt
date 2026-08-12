package com.rfz.appflotal.presentation.ui.home.screen.completeplan.viewmodel

import androidx.lifecycle.ViewModel
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.CompletePlanUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CompletePlanViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(CompletePlanUiState())
    val uiState: StateFlow<CompletePlanUiState> = _uiState.asStateFlow()

    // Aquí el usuario agregará la lógica posteriormente
}
