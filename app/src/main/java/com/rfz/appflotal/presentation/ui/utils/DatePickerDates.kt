package com.rfz.appflotal.presentation.ui.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/*
 * El DatePicker de Material3 trabaja con la medianoche UTC del día elegido. Convertir esos
 * millis con la zona del dispositivo (p. ej. UTC-6) da el día anterior, así que ambas
 * conversiones se hacen en UTC.
 */

/** Millis del DatePicker → "yyyy-MM-ddT00:00:00" del mismo día calendario. */
fun datePickerMillisToIsoDate(millis: Long): String =
    Instant.ofEpochMilli(millis)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
        .atStartOfDay()
        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

/** Fecha "yyyy-MM-dd", con o sin hora, → millis para preseleccionarla en el DatePicker; null si no se reconoce. */
fun isoDateToDatePickerMillis(date: String): Long? = try {
    LocalDate.parse(date.trim().substringBefore('T'), DateTimeFormatter.ISO_LOCAL_DATE)
        .atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()
} catch (_: Exception) {
    null
}
