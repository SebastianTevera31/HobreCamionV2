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
import com.rfz.appflotal.domain.tpms.ApiTpmsUseCase
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

data class ConfigurationItem(
    val id: Int,
    val rawDescription: String,
    val tireCount: String
)

data class MonitorConfigurationUiState(
    val mac: String = "",
    val configurationSelected: ConfigurationItem? = null,
    val isScanning: Boolean = false
)

@HiltViewModel
class RegisterMonitorViewModel @Inject constructor(
    private val apiTpmsUseCase: ApiTpmsUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val bluetoothUseCase: BluetoothUseCase
) : ViewModel() {

    private var _configurationsList =
        MutableStateFlow<List<ConfigurationItem>>(emptyList())
    val configurationList = _configurationsList.asStateFlow()

    private var _registeredMonitorState = MutableStateFlow<ApiResult<Int>>(ApiResult.Loading)
    val registeredMonitorState = _registeredMonitorState.asStateFlow()

    private var _monitorConfigUiState = MutableStateFlow(MonitorConfigurationUiState())
    val monitorConfigUiState = _monitorConfigUiState.asStateFlow()

    fun loadConfigurationsIfEmpty() {
        if (_configurationsList.value.isEmpty()) loadConfigurations()
    }

    private fun loadConfigurations() {
        viewModelScope.launch {
            val response = apiTpmsUseCase.doGetConfigurations()
            responseHelper(response = response) { result ->
                if (result != null) {
                    val products = result
                        .filterNot { it.idConfiguration == 2 }
                        .map {
                            val count = it.fldDescription
                                .replace("TALON", "", ignoreCase = true)
                                .replace("BASE", "", ignoreCase = true)
                                .trim()
                            ConfigurationItem(
                                id = it.idConfiguration,
                                rawDescription = it.fldDescription,
                                tireCount = count
                            )
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
        configurationSelected: ConfigurationItem?,
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

        viewModelScope.launch {
            submitMonitorConfiguration(
                idMonitor = idMonitor,
                mac = mac,
                configurationSelected = configurationSelected,
                context = context,
                notifySuccess = true,
                restartBleOnSuccess = mac.isNotEmpty(),
                onResult = { _registeredMonitorState.value = it }
            )
        }
    }

    private suspend fun submitMonitorConfiguration(
        idMonitor: Int,
        mac: String,
        configurationSelected: ConfigurationItem,
        context: Context,
        notifySuccess: Boolean,
        restartBleOnSuccess: Boolean,
        onResult: (ApiResult<Int>) -> Unit
    ) {
        val userData = getTasksUseCase().first { it.isNotEmpty() }[0]

        val response = apiTpmsUseCase.doPostCrudMonitor(
            idMonitor = idMonitor,
            fldMac = mac,
            fldDate = getCurrentDate(),
            idVehicle = userData.idVehicle,
            idConfiguration = configurationSelected.id
        )

        responseHelper(
            response = response,
            onError = {
                onResult(ApiResult.Error(message = context.getString(RegisterMonitorMessage.UNKNOWN_ERROR.message)))
            }
        ) { result ->
            if (!result.isNullOrEmpty()) {
                val fields = result[0].message.split(":")
                if (fields.size == 2 && !fields.contains("error")) {
                    val newIdMonitor = fields[1].trim().toIntOrNull()
                    if (newIdMonitor != null) {
                        val baseConfig = if (configurationSelected.tireCount.isNotEmpty()) {
                            "BASE ${configurationSelected.tireCount}"
                        } else {
                            configurationSelected.rawDescription
                        }

                        updateMonitorDataDB(
                            newIdMonitor,
                            mac,
                            baseConfig,
                            userData.idUser
                        )
                        if (notifySuccess) {
                            showAlert(context, message = RegisterMonitorMessage.REGISTERED.message)
                        }
                        if (restartBleOnSuccess) {
                            onRestartBleConnection(context)
                        }

                        onResult(ApiResult.Success(data = newIdMonitor))
                    } else {
                        onResult(
                            ApiResult.Error(
                                message = context.getString(R.string.no_se_ha_asignado_ningun_monitor)
                            )
                        )
                    }
                } else {
                    onResult(ApiResult.Error(message = result[0].message))
                }
            } else {
                onResult(ApiResult.Error(message = context.getString(RegisterMonitorMessage.UNKNOWN_ERROR.message)))
            }
        }
    }

    fun updateMonitorConfiguration(config: ConfigurationItem?) {
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
            val result = getTasksUseCase().first { it.isNotEmpty() }
            // El catálogo se descarga en paralelo al abrir el diálogo por primera vez: hay
            // que esperarlo, o la búsqueda se hace contra una lista vacía y no preselecciona.
            val configurations = configurationList.first { it.isNotEmpty() }
            if (result.isNotEmpty()) {
                val values = result[0]
                val baseNum = values.baseConfiguration.replace("BASE", "").trim()
                val configSelected = if (baseNum.isEmpty()) {
                    null
                } else {
                    configurations.find { item ->
                        item.tireCount == baseNum || item.rawDescription.contains(baseNum)
                    }
                }

                _monitorConfigUiState.update { currentUiState ->
                    currentUiState.copy(
                        mac = values.monitorMac,
                        configurationSelected = configSelected
                    )
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

    fun onRestartBleConnection(context: Context) {
        ContextCompat.startForegroundService(
            context,
            Intent(context, HombreCamionService::class.java).setAction("ACTION_RESTART")
        )
    }
}
