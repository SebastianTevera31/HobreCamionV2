package com.rfz.appflotal.presentation.ui.utils

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.TimeZone

/** Fecha de adquisición: el día elegido en el DatePicker no debe recorrerse por la zona horaria. */
class DatePickerDatesTest {

    private lateinit var originalZone: TimeZone

    @Before
    fun setUp() {
        originalZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("America/Mexico_City")) // UTC-6
    }

    @After
    fun tearDown() = TimeZone.setDefault(originalZone)

    private fun pickerMillis(date: String): Long =
        LocalDate.parse(date).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    @Test
    fun `el dia elegido se conserva en una zona con offset negativo`() {
        assertEquals("2026-10-21T00:00:00", datePickerMillisToIsoDate(pickerMillis("2026-10-21")))
    }

    @Test
    fun `el primer dia del anio no regresa al anio anterior`() {
        assertEquals("2026-01-01T00:00:00", datePickerMillisToIsoDate(pickerMillis("2026-01-01")))
    }

    @Test
    fun `la fecha guardada se preselecciona en el mismo dia`() {
        assertEquals(pickerMillis("2026-10-21"), isoDateToDatePickerMillis("2026-10-21T00:00:00"))
        assertEquals(pickerMillis("2026-10-21"), isoDateToDatePickerMillis("2026-10-21T15:42:10.123"))
        assertEquals(pickerMillis("2026-10-21"), isoDateToDatePickerMillis("2026-10-21"))
    }

    @Test
    fun `ida y vuelta conserva el dia`() {
        val millis = isoDateToDatePickerMillis("2025-12-31T00:00:00")!!
        assertEquals("2025-12-31T00:00:00", datePickerMillisToIsoDate(millis))
    }

    @Test
    fun `una fecha no reconocida no preselecciona nada`() {
        assertNull(isoDateToDatePickerMillis(""))
        assertNull(isoDateToDatePickerMillis("21/10/2026"))
    }
}
