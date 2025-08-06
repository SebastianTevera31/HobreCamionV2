package com.rfz.appflotal.presentation.ui.monitor.viewmodel

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startForegroundService
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.core.util.Commons.validateBluetoothConnectivity
import com.rfz.appflotal.data.network.service.HombreCamionService
import com.rfz.appflotal.data.network.service.ResultApi
import com.rfz.appflotal.data.repository.bluetooth.BluetoothData
import com.rfz.appflotal.data.repository.bluetooth.MonitorDataFrame
import com.rfz.appflotal.data.repository.bluetooth.SensorAlertDataFrame
import com.rfz.appflotal.data.repository.bluetooth.decodeAlertDataFrame
import com.rfz.appflotal.data.repository.bluetooth.decodeDataFrame
import com.rfz.appflotal.domain.bluetooth.BluetoothUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.database.SensorTableUseCase
import com.rfz.appflotal.domain.tpmsUseCase.ApiTpmsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject


enum class SensorAlerts(val message: String = "") {
    HighPressure("Persion alta"), LowPressure("Presion baja"), HighTemperature("Temperatura alta"), NoData
}

@HiltViewModel
class MonitorViewModel @Inject constructor(
    private val apiTpmsUseCase: ApiTpmsUseCase,
    private val bluetoothUseCase: BluetoothUseCase,
    private val sensorTableUseCase: SensorTableUseCase,
    private val getTasksUseCase: GetTasksUseCase
) : ViewModel() {
    private var _bluetoothData: MutableStateFlow<BluetoothData> = MutableStateFlow(BluetoothData())
    val bluetoothData = _bluetoothData.asStateFlow()
    private var _monitorUiState: MutableStateFlow<MonitorUiState> =
        MutableStateFlow(MonitorUiState())
    val monitorUiState = _monitorUiState.asStateFlow()

    init {
        viewModelScope.launch {
            val monitorId = getTasksUseCase().first()[0].id_monitor
            val configInfo = apiTpmsUseCase.doGetConfigurationMonitorById(3)
            when (configInfo) {
                is ResultApi.Success -> {
                    val data = configInfo.data
                    if (!data.isNullOrEmpty()) {
                        val config = data[0].fldDescription.replace("BASE", "").trim()
                        if (config.isDigitsOnly()) {
                            _monitorUiState.update { currentUiState ->
                                currentUiState.copy(
                                    monitorId = monitorId,
                                    numWheels = config.toInt(),
                                    chassisImageUrl = data[0].fldUrlImage
                                )
                            }
                        }
                    }
                }

                is ResultApi.Error -> {}
            }


        }
    }

    fun initService(ctx: Context) {
        collectSensorData()
        readBluetoothData()

        if (!isServiceRunning(ctx, HombreCamionService::class.java)) {
            val intent = Intent(ctx, HombreCamionService::class.java)
            startForegroundService(ctx, intent)
        }
    }

    private fun readBluetoothData() {
        viewModelScope.launch {
            bluetoothUseCase().collect { data ->
                _bluetoothData.update { data }
            }
        }
    }

    private fun collectSensorData() {
        viewModelScope.launch {

            bluetoothData.collect { data ->
                Log.d("MonitorViewModel", "$data")
                val rssi = data.rssi
                val bluetoothSignalQuality = data.bluetoothSignalQuality

                var dataFrame = data.dataFrame

                if (!validateBluetoothConnectivity(bluetoothSignalQuality) || dataFrame == null) {
                    val monitorId = 3

                    // Se agrega la funcion Let como seguridad, sin embargo el Id debe existir en esta parte
                    dataFrame = monitorId.let { sensorTableUseCase.doGetLastRecord(it)?.dataFrame }
                }

                if (dataFrame != null) updateSensorData(dataFrame)

                _monitorUiState.update { currentUiState ->
                    currentUiState.copy(
                        signalIntensity = Pair(
                            bluetoothSignalQuality, if (rssi != null) "$rssi dBm" else "N/A"
                        ),
                    )
                }
            }
        }
    }

    private fun updateSensorData(dataFrame: String) {
        _monitorUiState.update { currentUiState ->
            val pressionValue = decodeDataFrame(dataFrame, MonitorDataFrame.PRESSION)
            val pressionStatus =
                decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.PRESSURE)
            val temperatureValue =
                decodeDataFrame(dataFrame, MonitorDataFrame.TEMPERATURE)
            val temperatureStatus =
                decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.HIGH_TEMPERATURE)

            val pression = if (pressionValue.isDigitsOnly()) pressionValue.toFloat() else 0f
            val temperature = if (temperatureValue.isDigitsOnly()) pressionValue.toFloat() else 0f

            currentUiState.copy(
                wheel = decodeDataFrame(dataFrame, MonitorDataFrame.POSITION_WHEEL),
                battery = decodeAlertDataFrame(
                    dataFrame,
                    SensorAlertDataFrame.LOW_BATTERY
                ),
                pression = Pair(pression, SensorAlerts.valueOf(pressionStatus)),
                temperature = Pair(temperature, SensorAlerts.valueOf(temperatureStatus)),
                timestamp = getCurrentDate()
            )
        }
    }

    fun getCurrentRecordDate(): String? {
        return try {
            val fecha = monitorUiState.value.timestamp
            if (fecha.isNotEmpty()) {
                val inputFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getDefault()

                val date = inputFormat.parse(monitorUiState.value.timestamp)

                val outputFormat = SimpleDateFormat(
                    "dd/MMMM/yyyy HH:mm:ss",
                    Locale.getDefault()
                )
                outputFormat.format(date!!)
            } else null
        } catch (e: Exception) {
            Log.e("MonitorViewModel", "$e")
            "Fecha inválida"
        }
    }

    fun getSensorDataByWheel(wheelPosition: String) {
        viewModelScope.launch {
            val sensorData = apiTpmsUseCase.doGetDiagramMonitor(3)
            apiTpmsUseCase.doPostSensorData("aaa1410e6302479752001c500ca7", 3, getCurrentDate())
            when (sensorData) {
                is ResultApi.Success -> {
                    if (!sensorData.data.isNullOrEmpty()) {
                        sensorData.data.filter { data -> data.sensorPosition == wheelPosition }[0]
                            .let {
                                if (it.sensorPosition.contains("P")) {
                                    val tempAlert = when (it.highTemperature) {
                                        true -> SensorAlerts.HighTemperature
                                        false -> SensorAlerts.NoData
                                    }

                                    val pressureAlert =
                                        if (it.lowPressure) SensorAlerts.LowPressure
                                        else if (it.highPressure) SensorAlerts.HighPressure
                                        else SensorAlerts.NoData

                                    _monitorUiState.update { currentUiState ->
                                        currentUiState.copy(
                                            wheel = it.sensorPosition,
                                            temperature = Pair(it.temperature, tempAlert),
                                            pression = Pair(it.psi, pressureAlert),
                                            timestamp = it.ultimalectura
                                        )
                                    }
                                }
                            }
                    }
                }

                is ResultApi.Error -> {

                }
            }
        }
    }

    private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Int.MAX_VALUE).any {
            it.service.className == serviceClass.name
        }
    }
}