package com.rfz.appflotal.presentation.ui.alerts.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Thermostat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.data.NetworkStatus
import com.rfz.appflotal.data.model.alerts.Alert
import com.rfz.appflotal.data.repository.database.SensorDataTableRepository
import com.rfz.appflotal.domain.alerts.GetAlertsUseCase
import com.rfz.appflotal.domain.database.CoordinatesTableUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.tpms.ApiTpmsUseCase
import com.rfz.appflotal.domain.wifi.WifiUseCase
import com.rfz.appflotal.presentation.ui.alerts.screens.AlertType
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.AlertStatus
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.AlertUi
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.asIcon
import com.rfz.appflotal.presentation.ui.utils.responseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.rfz.appflotal.data.model.alerts.AlertType as DomainAlertType

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
    private val alertsUseCase: GetAlertsUseCase,
    private val sensorDataTableRepository: SensorDataTableRepository,
    private val getCurrentTask: GetTasksUseCase,
    private val coordinatesTableUseCase: CoordinatesTableUseCase,
    private val wifiUseCase: WifiUseCase,
    private val apiTpmsUseCase: ApiTpmsUseCase,
) : ViewModel() {
    private var _uiState = MutableStateFlow(AlertUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getData()
        goToPage(1)
    }

    fun getData() {
        viewModelScope.launch {
            try {
                val tasks = getCurrentTask().first().firstOrNull()
                if (tasks != null) {
                    val idMonitor = tasks.id_monitor
                    if (idMonitor != 0) {
                        getConfigData(idMonitor)
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private suspend fun getConfigData(monitorId: Int) {
        if (wifiUseCase().value == NetworkStatus.Connected) {
            val baseCoordinates = apiTpmsUseCase.doGetPositionCoordinates(monitorId)
            responseHelper(baseCoordinates) { coords ->
                val wheels = coords.orEmpty().map { it.position }.sortedBy {
                    it.removePrefix("P").trim().toIntOrNull() ?: Int.MAX_VALUE
                }
                _uiState.update { it.copy(wheels = wheels) }
            }
        } else {
            val localCoordinates = coordinatesTableUseCase.getCoordinates(monitorId)
            val wheels = localCoordinates.map { it.idPosition }.sortedBy {
                it.removePrefix("P").trim().toIntOrNull() ?: Int.MAX_VALUE
            }
            _uiState.update { it.copy(wheels = wheels) }
        }
    }

    fun goToPage(page: Int) {
        val state = _uiState.value
        if (state.isLoading || page < 1 || (page == state.currentPage && state.alerts.isNotEmpty())) return
        if (page > state.currentPage && !state.hasNextPage) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = alertsUseCase(
                startDate = state.startDate,
                endDate = state.startDate,
                position = state.selectedWheel,
                alertType = if (state.selectedAlert == AlertType.ALL) "" else state.selectedAlert.name,
                startPaging = ((page - 1) * PAGE_SIZE)
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
            it.copy(
                selectedWheel = wheel,
                selectedAlert = alert,
                startDate = startDate,
                endDate = endDate,
                currentPage = 0,
                hasNextPage = true
            )
        }
        goToPage(1)
    }
}

private fun Alert.toAlertUi(): AlertUi {
    val isPressureAlert = alert == DomainAlertType.PRESSURE || alert == DomainAlertType.INFLATE
    return AlertUi(
        icon = (if (isPressureAlert) Icons.Outlined.Speed else Icons.Outlined.Thermostat).asIcon(),
        title = when (alert) {
            DomainAlertType.PRESSURE -> "Alerta de presión · $position"
            DomainAlertType.TEMPERATURE -> "Alerta de temperatura · $position"
            DomainAlertType.INFLATE -> "Alerta de inflado · $position"
            DomainAlertType.NONE -> "Alerta · $position"
        },
        detailLabel = if (isPressureAlert) "Presión:" else "Temp:",
        detailValue = if (isPressureAlert) "%.2f psi".format(psi) else "$temperature °C",
        status = AlertStatus.CRITICA
    )
}
