package com.rfz.appflotal.presentation.ui.monitor.viewmodel

import android.graphics.Bitmap
import com.rfz.appflotal.data.model.database.CoordinatesEntity
import com.rfz.appflotal.data.model.database.SensorDataEntity
import com.rfz.appflotal.data.model.tpms.MonitorTireByDateResponse
import com.rfz.appflotal.data.model.tpms.PositionCoordinatesResponse
import com.rfz.appflotal.data.repository.UnidadPresion
import com.rfz.appflotal.data.repository.UnidadTemperatura
import com.rfz.appflotal.data.repository.bluetooth.BluetoothSignalQuality
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType

data class MonitorUiState(
    val monitorId: Int = 0,
    val currentData: String = "",
    val baseConfig: BaseConfig? = null,
    val signalIntensity: Pair<BluetoothSignalQuality, String> = Pair(
        BluetoothSignalQuality.Desconocida,
        ""),
    val imageDimen: Pair<Int, Int> = Pair(0, 0),
    val imageBitmap: Bitmap? = null,
    val chassisImageUrl: String = "",
    val listOfTires: List<MonitorTire> = emptyList(),
    val coordinateList: List<PositionCoordinatesResponse>? = emptyList(),
    val showDialog: Boolean = false,
    val showView: Boolean = false,
    val isBluetoothOn: Boolean = false,
    val temperatureUnit: UnidadTemperatura = UnidadTemperatura.CELCIUS,
    val pressureUnit: UnidadPresion = UnidadPresion.PSI
)

data class TireUiState(
    val currentTire: String = "",
    val pressure: Pair<Float, SensorAlerts> = Pair(0f, SensorAlerts.NO_DATA),
    val rawPressure: Float = 0f,
    val temperature: Pair<Float, SensorAlerts> = Pair(0f, SensorAlerts.NO_DATA),
    val rawTemperature: Float = 0f,
    val depth: Float = 0f,
    val timestamp: String = "",
    val batteryStatus: SensorAlerts = SensorAlerts.NO_DATA,
    val flatTireStatus: SensorAlerts = SensorAlerts.NO_DATA,
    val tireRemovingStatus: SensorAlerts = SensorAlerts.NO_DATA,
    val isAssembled: Boolean = false,
    val isInspectionAvailable: Boolean = false,
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

fun SensorDataEntity.toTireData(): ListOfTireData {
    return ListOfTireData(
        tirePosition = tire,
        tireNumber = tireNumber,
        sensorDate = timestamp,
        psi = pressure.toFloat(),
        temperature = temperature.toFloat()
    )
}

fun MonitorTireByDateResponse.toTireData(): ListOfTireData =
    ListOfTireData(
        tirePosition = tirePosition,
        tireNumber = tireNumber,
        sensorDate = sensorDate,
        psi = psi.toFloat(),
        temperature = temperature.toFloat()
    )

data class ListOfTireData(
    val tirePosition: String,
    val tireNumber: String,
    val sensorDate: String,
    val psi: Float,
    val temperature: Float,
)
