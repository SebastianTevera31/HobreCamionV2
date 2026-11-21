package com.rfz.appflotal.presentation.ui.monitor.viewmodel

import com.rfz.appflotal.data.model.tpms.SensorAlerts
import com.rfz.appflotal.domain.monitor.LlantaDiagrama
import com.rfz.appflotal.domain.sincronizacion.BloqueoInspeccion
import com.rfz.appflotal.data.model.tpms.PositionCoordinatesResponse
import com.rfz.appflotal.data.repository.UnidadOdometro
import com.rfz.appflotal.data.repository.UnidadPresion
import com.rfz.appflotal.data.repository.UnidadTemperatura
import com.rfz.appflotal.data.repository.bluetooth.BluetoothSignalQuality

const val VOID_DATE = "01/01/1900 00:00:00"

data class MonitorUiState(
    val monitorId: Int = 0,
    val currentData: String = "",
    val baseConfig: BaseConfig? = null,
    val signalIntensity: Pair<BluetoothSignalQuality, String> = Pair(
        BluetoothSignalQuality.Desconocida,
        ""),
    val imageDimen: Pair<Int, Int> = Pair(0, 0),
    /** Recurso de la imagen del diagrama; la UI lo decodifica. */
    val imagenDiagrama: Int? = null,
    val chassisImageUrl: String = "",
    val listOfTires: List<MonitorTire> = emptyList(),
    val coordinateList: List<PositionCoordinatesResponse>? = emptyList(),
    val showDialog: Boolean = false,
    val showView: Boolean = false,
    val monitorMac: String = "",
    val showSetupWizard: Boolean = false,
    val isBluetoothOn: Boolean = false,
    val temperatureUnit: UnidadTemperatura = UnidadTemperatura.CELCIUS,
    val pressureUnit: UnidadPresion = UnidadPresion.PSI,
    val odometerUnit: UnidadOdometro = UnidadOdometro.KILOMETROS,
    /** Alertas que llegaron con otra llanta fijada; null si no hay o el panel está en automático. */
    val avisoAlerta: AvisoAlertaUi? = null,
)

data class AvisoAlertaUi(
    /** La alerta sin ver más reciente: la que abre "Ver". */
    val posicion: String,
    val alerta: SensorAlerts,
    /** Cuántas alertas más hay sin ver además de [posicion]. */
    val adicionales: Int,
    val posiciones: Set<String>,
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
    /** Por qué hoy no se puede inspeccionar; null si se puede. Lo deriva MonitorViewModel.tireUiState. */
    val bloqueoInspeccion: BloqueoInspeccion? = null,
    val isLoading:Boolean = false
)

data class MonitorTire(
    val sensorPosition: String,
    val inAlert: Boolean,
    val isAssembled: Boolean,
    val isActive: Boolean,
    val xPosition: Int,
    val yPosition: Int,
)

fun LlantaDiagrama.aMonitorTire(): MonitorTire = MonitorTire(
    sensorPosition = posicion,
    isAssembled = montada,
    inAlert = enAlerta,
    isActive = activa,
    xPosition = x,
    yPosition = y
)

data class ListOfTireData(
    val tirePosition: String,
    val tireNumber: String,
    val sensorDate: String,
    val psi: Float,
    val temperature: Float,
)
