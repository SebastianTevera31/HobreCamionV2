package com.rfz.appflotal.presentation.ui.monitor.viewmodel

import com.rfz.appflotal.data.model.tpms.DiagramMonitorResponse
import com.rfz.appflotal.data.model.tpms.MonitorTireByDateResponse
import com.rfz.appflotal.data.repository.bluetooth.BluetoothSignalQuality

data class MonitorUiState(
    val monitorId: Int = 0,
    val wheel: String = "N/A",
    val battery: String = "N/A",
    val pression: Pair<Float, SensorAlerts> = Pair(0f, SensorAlerts.NoData),
    val temperature: Pair<Float, SensorAlerts> = Pair(0f, SensorAlerts.NoData),
    val depth: Float = 0f,
    val timestamp: String = "",
    val signalIntensity: Pair<BluetoothSignalQuality, String> = Pair(
        BluetoothSignalQuality.Desconocida,
        "N/A"
    ),
    val numWheels: Int = 0,
    val chassisImageUrl: String = "",
    val wheelsWithAlert: Map<String, Boolean> = emptyMap()
)

fun DiagramMonitorResponse.toTireData(): MonitorTireByDateResponse {
    return MonitorTireByDateResponse(
        tirePosition = sensorPosition,
        tireNumber = tireNumber,
        sensorDate = ultimalectura,
        psi = psi.toInt(),
        temperature = temperature.toInt()
    )
}