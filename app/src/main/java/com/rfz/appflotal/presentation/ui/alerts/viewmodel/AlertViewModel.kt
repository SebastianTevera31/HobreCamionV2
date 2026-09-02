package com.rfz.appflotal.presentation.ui.alerts.viewmodel

import androidx.lifecycle.ViewModel
import com.rfz.appflotal.presentation.ui.alerts.screens.AlertType
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.AlertUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class AlertUiState(
    val alerts: List<AlertUi> = emptyList(),
    val selectedAlert: AlertType = AlertType.ALL,
    val wheels: List<String> = listOf("P1", "P2", "P3", "P4"),
    val selectedWheel: String = "",
    val date: String = "",
)

@HiltViewModel
class AlertViewModel @Inject constructor() : ViewModel() {
    private var _uiState = MutableStateFlow(AlertUiState())
    val uiState = _uiState.asStateFlow()

    fun getAlerts() {

    }

    fun getWheels() {

    }

    fun applyFilter(date: String, wheel: String, alert: AlertType) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                selectedWheel = wheel,
                selectedAlert = alert,
                date = date
            )
        }
    }
}