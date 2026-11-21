package com.rfz.appflotal.domain.tpms

import com.rfz.appflotal.data.model.tpms.SensorAlerts

/** Última lectura de una llanta, en unidades base (PSI y °C); la UI convierte a la unidad elegida. */
data class LecturaLlanta(
    val posicion: String,
    val montada: Boolean,
    val presionPsi: Float,
    val alertaPresion: SensorAlerts,
    val temperaturaC: Float,
    val alertaTemperatura: SensorAlerts,
    val fecha: String,
    val bateria: SensorAlerts,
    val pinchazo: SensorAlerts,
    val extraccion: SensorAlerts,
)

fun estaEnAlerta(
    alertaTemperatura: SensorAlerts,
    alertaPresion: SensorAlerts,
    bateria: SensorAlerts,
    pinchazo: SensorAlerts
): Boolean = alertaTemperatura != SensorAlerts.NO_DATA ||
    alertaPresion != SensorAlerts.NO_DATA ||
    bateria != SensorAlerts.NO_DATA ||
    pinchazo != SensorAlerts.NO_DATA
