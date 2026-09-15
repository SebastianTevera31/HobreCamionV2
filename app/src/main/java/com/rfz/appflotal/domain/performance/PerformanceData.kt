package com.rfz.appflotal.domain.performance

import kotlinx.serialization.Serializable

@Serializable
data class PerformanceData(
    val fuelConsumption: String,
    val co2Emissions: String,
    val calculatedAt: Long = 0L
)
