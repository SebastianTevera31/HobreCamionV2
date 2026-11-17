package com.rfz.appflotal.domain.tpms

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/** Horas mínimas entre dos inspecciones de la misma llanta. */
private const val INSPECTION_COOLDOWN_HOURS = 24L

/**
 * Regla del monitor para mostrar "Inspeccionar" en una llanta montada: se permite si no hay
 * inspección registrada o si la última tiene más de [INSPECTION_COOLDOWN_HOURS] horas.
 *
 * [lastInspection] es la `ultimainspeccion` del diagrama guardada en sensor_data, en ISO-8601
 * con o sin zona (sin zona se toma como hora local). Un valor ilegible no bloquea.
 */
fun isInspectionAvailable(lastInspection: String?, now: Instant = Instant.now()): Boolean {
    if (lastInspection.isNullOrBlank()) return true
    val inspectedAt = runCatching { Instant.parse(lastInspection) }.getOrNull()
        ?: runCatching {
            LocalDateTime.parse(lastInspection).atZone(ZoneId.systemDefault()).toInstant()
        }.getOrNull()
        ?: return true
    return inspectedAt.isBefore(now.minus(INSPECTION_COOLDOWN_HOURS, ChronoUnit.HOURS))
}
