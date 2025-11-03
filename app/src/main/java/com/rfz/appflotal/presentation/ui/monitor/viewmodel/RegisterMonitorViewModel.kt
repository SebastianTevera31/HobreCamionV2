package com.rfz.appflotal.presentation.ui.monitor.viewmodel

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.network.service.HombreCamionService
import com.rfz.appflotal.domain.bluetooth.BluetoothUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.tpmsUseCase.ApiTpmsUseCase
import com.rfz.appflotal.presentation.ui.utils.responseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class RegisterMonitorMessage(@StringRes val message: Int) {
    EMPTY_MONITOR(R.string.ingrese_la_mac_del_monitor),
    EMPTY_CONFIGURATION(R.string.seleccione_tipo_monitor),
    REGISTERED(R.string.monitor_registrado_correctamente),
    UNKNOWN_ERROR(R.string.error_desconocido),
}

data class MonitorConfigurationUiState(
    val mac: String = "",
    val configurationSelected: Pair<Int, String>? = null,
    val isScanning: Boolean = false
)

@HiltViewModel
class RegisterMonitorViewModel @Inject constructor(
    private val apiTpmsUseCase: ApiTpmsUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val bluetoothUseCase: BluetoothUseCase
) : ViewModel() {

    private var _configurationsList =
        MutableStateFlow<Map<Int, String>>(emptyMap())
    val configurationList = _configurationsList.asStateFlow()

    private var _registeredMonitorState = MutableStateFlow<ApiResult<Int>>(ApiResult.Loading)
    val registeredMonitorState = _registeredMonitorState.asStateFlow()

    private var _monitorConfigUiState = MutableStateFlow(MonitorConfigurationUiState())
    val monitorConfigUiState = _monitorConfigUiState.asStateFlow()

    init {
        viewModelScope.launch {
            val response = apiTpmsUseCase.doGetConfigurations()
            responseHelper(response = response) { result ->
                if (result != null) {
                    val products = result
                        .filterNot { it.idConfiguration == 2 }
                        .associate {
                            it.idConfiguration to it.fldDescription.replace("BASE", "TALON")
                        }

                    _configurationsList.value = products
                }
            }
        }
    }

    private fun readBleScanData() {
        viewModelScope.launch {
            bluetoothUseCase.scannedDevices().collect { data ->
                if (data != null) {
                    _monitorConfigUiState.update { currentUiState ->
                        currentUiState.copy(
                            mac = data.address,
                            isScanning = false
                        )
                    }
                }
            }
        }
    }

    fun registerMonitor(
        idMonitor: Int = 0,
        mac: String,
        configurationSelected: Pair<Int, String>?,
        context: Context
    ) {
        _registeredMonitorState.value = ApiResult.Loading

        _monitorConfigUiState.update { currentUiState ->
            currentUiState.copy(configurationSelected = configurationSelected)
        }

        if (configurationSelected == null) {
            _registeredMonitorState.value =
                ApiResult.Error(message = context.getString(RegisterMonitorMessage.EMPTY_CONFIGURATION.message))
            return
        }

        if (mac.isEmpty()) {
            ApiResult.Error(message = context.getString(RegisterMonitorMessage.EMPTY_MONITOR.message))
            return
        }

        viewModelScope.launch {
            val userData = getTasksUseCase().first()[0]

            val response = apiTpmsUseCase.doPostCrudMonitor(
                idMonitor = idMonitor,
                fldMac = mac,
                fldDate = getCurrentDate(),
                idVehicle = userData.idVehicle,
                idConfiguration = configurationSelected.first
            )

            responseHelper(response = response) { result ->
                if (!result.isNullOrEmpty()) {
                    val fields = result[0].message.split(":")
                    if (fields.size == 2 && !fields.contains("error")) {
                        val idMonitor = fields[1].trim().toIntOrNull()
                        if (idMonitor != null) {
                            updateMonitorDataDB(
                                idMonitor,
                                mac,
                                "BASE ${configurationSelected.second.split(" ")[1]}",
                                userData.idUser
                            )
                            showAlert(context, message = RegisterMonitorMessage.REGISTERED.message)

                            onRestartClicked(context)

                            _registeredMonitorState.value = ApiResult.Success(data = idMonitor)
                        } else {
                            _registeredMonitorState.value = ApiResult.Error(
                                message = context.getString(
                                    R.string.no_se_ha_asignado_ningun_monitor
                                )
                            )
                        }
                    } else {
                        _registeredMonitorState.value = ApiResult.Error(message = result[0].message)
                    }
                } else {
                    _registeredMonitorState.value =
                        ApiResult.Error(message = context.getString(RegisterMonitorMessage.UNKNOWN_ERROR.message))
                }
            }
        }
    }

    fun updateMonitorConfiguration(config: Pair<Int, String>?) {
        _monitorConfigUiState.update { currentUiState ->
            currentUiState.copy(
                configurationSelected = config
            )
        }
    }

    fun startScan() {
        _monitorConfigUiState.update { currentUiState ->
            currentUiState.copy(
                isScanning = true
            )
        }
        bluetoothUseCase.startScan()
        readBleScanData()
    }

    fun stopScan() {
        _monitorConfigUiState.update { currentUiState ->
            currentUiState.copy(
                isScanning = false
            )
        }
        bluetoothUseCase.stopScan()
    }

    fun getMonitorConfiguration() {
        viewModelScope.launch {
            val result = getTasksUseCase().first()
            if (result.isNotEmpty()) {
                val values = result[0]
                val monitorType = values.baseConfiguration.replace("BASE", "TALON")
                val configSelected = configurationList.value.filterValues { it == monitorType }
                    .map { Pair(it.key, it.value) }
                if (configSelected.isNotEmpty()) {
                    _monitorConfigUiState.update { currentUiState ->
                        currentUiState.copy(
                            mac = values.monitorMac,
                            configurationSelected = configSelected[0]
                        )
                    }
                }
            }
        }
    }

    fun clearMonitorRegistrationData() {
        _registeredMonitorState.value = ApiResult.Loading
    }

    fun clearMonitorConfiguration() {
        _monitorConfigUiState.value = MonitorConfigurationUiState()
    }

    private fun updateMonitorDataDB(
        idMonitor: Int,
        mac: String,
        baseConfiguration: String,
        idUser: Int
    ) {
        viewModelScope.launch {
            getTasksUseCase.updateMonitor(idMonitor, mac, baseConfiguration, idUser)
        }
    }

    private fun showAlert(ctx: Context, message: Int? = null, strMessage: String? = null) {
        if (message != null) {
            Toast.makeText(ctx, ctx.getString(message), Toast.LENGTH_LONG).show()
        }

        if (strMessage != null) {
            Toast.makeText(ctx, strMessage, Toast.LENGTH_LONG).show()
        }
    }

    fun onRestartClicked(context: Context) {
        ContextCompat.startForegroundService(
            context,
            Intent(context, HombreCamionService::class.java).setAction("ACTION_RESTART")
        )
    }
}