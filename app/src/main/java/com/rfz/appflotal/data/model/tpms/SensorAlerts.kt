package com.rfz.appflotal.data.model.tpms

/** Alertas de un sensor TPMS; el texto de cada una lo resuelve la UI. */
enum class SensorAlerts {
    HIGH_PRESSURE,
    LOW_PRESSURE,
    HIGH_TEMPERATURE,
    LOW_BATTERY,
    NO_DATA,
    FAST_LEAKAGE,
    SLOW_LEAKAGE,
    REMOVAL
}
