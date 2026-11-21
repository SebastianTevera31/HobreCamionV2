package com.rfz.appflotal.presentation.ui.monitor.viewmodel

import androidx.annotation.StringRes
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.tpms.SensorAlerts
import com.rfz.appflotal.domain.tpms.LecturaLlanta

@get:StringRes
val SensorAlerts.message: Int
    get() = when (this) {
        SensorAlerts.HIGH_PRESSURE -> R.string.presion_alta
        SensorAlerts.LOW_PRESSURE -> R.string.presion_baja
        SensorAlerts.HIGH_TEMPERATURE -> R.string.temperatura_alta
        SensorAlerts.LOW_BATTERY -> R.string.bateria_baja
        SensorAlerts.NO_DATA -> R.string.sin_datos
        SensorAlerts.FAST_LEAKAGE -> R.string.fuga_rapida
        SensorAlerts.SLOW_LEAKAGE -> R.string.fuga_lenta
        SensorAlerts.REMOVAL -> R.string.en_extraccion
    }

/** Los valores van en unidades base; MonitorViewModel.tireUiState los convierte al exponerlos. */
fun LecturaLlanta.aTireUiState(): TireUiState = TireUiState(
    currentTire = posicion,
    isAssembled = montada,
    pressure = Pair(presionPsi, alertaPresion),
    rawPressure = presionPsi,
    temperature = Pair(temperaturaC, alertaTemperatura),
    rawTemperature = temperaturaC,
    timestamp = fecha,
    batteryStatus = bateria,
    flatTireStatus = pinchazo,
    tireRemovingStatus = extraccion,
)
