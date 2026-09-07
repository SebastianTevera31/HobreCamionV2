package com.rfz.appflotal.presentation.ui.alerts.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Thermostat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.data.model.alerts.Alert
import com.rfz.appflotal.data.repository.alerts.AlertsRepository
import com.rfz.appflotal.data.repository.database.SensorDataTableRepository
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.presentation.ui.alerts.screens.AlertType
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.AlertStatus
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.AlertUi
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.asIcon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PAGE_SIZE = 10

data class AlertUiState(
    val alerts: List<AlertUi> = emptyList(),
    val selectedAlert: AlertType = AlertType.ALL,
    val wheels: List<String> = emptyList(),
    val selectedWheel: String = "",
    val startDate: String = getCurrentDate(pattern = "dd/MM/yyyy"),
    val endDate: String = getCurrentDate(pattern = "dd/MM/yyyy"),
    val currentPage: Int = 0, // 0 = todavía no se cargó ninguna página
    val hasNextPage: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class AlertViewModel @Inject constructor(
    private val alertsRepository: AlertsRepository,
    private val sensorDataTableRepository: SensorDataTableRepository,
    private val getCurrentTask: GetTasksUseCase
) : ViewModel() {
    private var _uiState = MutableStateFlow(AlertUiState())
    val uiState = _uiState.asStateFlow()

    init {
        goToPage(1)
    }

    fun getData() {
        viewModelScope.launch {
            val idMonitor = getCurrentTask().first().first().id_monitor
            val wheels = sensorDataTableRepository.getLastData(idMonitor)
            _uiState.update { it.copy(wheels = wheels.map { wheel -> wheel.tire }) }
        }
    }

    fun goToPage(page: Int) {
        val state = _uiState.value
        if (state.isLoading || page < 1 || page == state.currentPage) return
        if (page > state.currentPage && !state.hasNextPage) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = alertsRepository.getAlerts(
                startDate = state.startDate,
                endDate = state.startDate,
                position = state.selectedWheel,
                alertType = state.selectedAlert.name,
                startPaging = ((page - 1) * PAGE_SIZE).toString()
            )

            result.onSuccess { alerts ->
                _uiState.update {
                    it.copy(
                        alerts = alerts.map(Alert::toAlertUi),
                        currentPage = page,
                        hasNextPage = alerts.size == PAGE_SIZE,
                        isLoading = false
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.message)
                }
            }
        }
    }

    fun applyFilter(startDate: String, endDate: String, wheel: String, alert: AlertType) {
        _uiState.update {
            AlertUiState(
                wheels = it.wheels,
                selectedWheel = wheel,
                selectedAlert = alert,
                startDate = startDate,
                endDate = endDate,
            )
        }
        goToPage(1)
    }
}

// Mapeo provisional: ajustar el criterio de "alert" y las etiquetas a la
// semántica real que el backend le da a ese código una vez esté documentada.
private fun Alert.toAlertUi(): AlertUi {
    val isPressureAlert = alert == 1
    return AlertUi(
        icon = (if (isPressureAlert) Icons.Outlined.Speed else Icons.Outlined.Thermostat).asIcon(),
        title = if (isPressureAlert) "Alerta de presión" else "Alerta de temperatura",
        detailLabel = if (isPressureAlert) "Presión:" else "Temp:",
        detailValue = if (isPressureAlert) "$psi psi" else "$temperature °C",
        status = AlertStatus.CRITICA
    )
}