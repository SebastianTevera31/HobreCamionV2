package com.rfz.appflotal.presentation.ui.alertas.viewmodel

import androidx.lifecycle.ViewModel
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.AlertUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class AlertUiState(
    val alerts: List<AlertUi> = emptyList(),
    val selectedAlert: AlertUi? = null,
    val wheels: List<String> = emptyList(),
)

@HiltViewModel
class AlertViewModel @Inject constructor() : ViewModel() {
    private var _uiState = MutableStateFlow(AlertUiState())
    val uiState = _uiState.asStateFlow()

    fun getAlerts() {

    }

    fun getWheels() {

    }

    fun filterAlerts(wheel: String) {

    }
}