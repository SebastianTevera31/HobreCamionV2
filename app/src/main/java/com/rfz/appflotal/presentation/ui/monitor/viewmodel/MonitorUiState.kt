package com.rfz.appflotal.presentation.ui.monitor.viewmodel

import android.graphics.Bitmap
import com.rfz.appflotal.data.model.database.CoordinatesEntity
import com.rfz.appflotal.data.model.database.SensorDataEntity
import com.rfz.appflotal.data.model.tpms.MonitorTireByDateResponse
import com.rfz.appflotal.data.model.tpms.PositionCoordinatesResponse
import com.rfz.appflotal.data.repository.bluetooth.BluetoothSignalQuality

data class MonitorUiState(
    val monitorId: Int = 0,
    val currentData: String = "",
    val baseConfig: BaseConfig? = null,
    val signalIntensity: Pair<BluetoothSignalQuality, String> = Pair(
        BluetoothSignalQuality.Desconocida,
        ""
    ),
    val imageDimen: Pair<Int, Int> = Pair(0, 0),
    val imageBitmap: Bitmap? = null,
    val chassisImageUrl: String = "",
    val listOfTires: List<MonitorTire> = emptyList(),
    val coordinateList: List<PositionCoordinatesResponse>? = emptyList(),
    val showDialog: Boolean = false,
    val showView: Boolean = false,
    val isBluetoothOn: Boolean = false,
    val lastOdometer: Int = 0,
    val dateOdometer: String = "",
)

data class TireUiState(
    val currentTire: String = "",
    val pressure: Pair<Float, SensorAlerts> = Pair(0f, SensorAlerts.NO_DATA),
    val temperature: Pair<Float, SensorAlerts> = Pair(0f, SensorAlerts.NO_DATA),
    val depth: Float = 0f,
    val timestamp: String = "",
    val batteryStatus: SensorAlerts = SensorAlerts.NO_DATA,
    val flatTireStatus: SensorAlerts = SensorAlerts.NO_DATA,
    val tireRemovingStatus: SensorAlerts = SensorAlerts.NO_DATA,
    val isAssembled: Boolean = false
)

data class MonitorTire(
    val sensorPosition: String,
    val inAlert: Boolean,
    val isAssembled: Boolean,
    val isActive: Boolean,
    val xPosition: Int,
    val yPosition: Int,
)

fun CoordinatesEntity.toTire(): MonitorTire {
    return MonitorTire(
        sensorPosition = idPosition,
        isAssembled = isAssembled,
        inAlert = inAlert,
        isActive = isActive,
        xPosition = xPosition,
        yPosition = yPosition
    )
}

fun SensorDataEntity.toTireData(): MonitorTireByDateResponse {
    return MonitorTireByDateResponse(
        tirePosition = tire,
        tireNumber = tireNumber,
        sensorDate = timestamp,
        psi = pressure,
        temperature = temperature
    )
}