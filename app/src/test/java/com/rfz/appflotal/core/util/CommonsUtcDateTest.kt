package com.rfz.appflotal.core.util

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.util.TimeZone

/** Fechas de lecturas del monitor y alertas: se emiten en UTC y se muestran en hora local. */
class CommonsUtcDateTest {

    private lateinit var originalZone: TimeZone

    @Before
    fun setUp() {
        originalZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("America/Mexico_City")) // UTC-6
    }

    @After
    fun tearDown() = TimeZone.setDefault(originalZone)

    @Test
    fun getCurrentUtcDate_localZoneNotUtc_returnsUtcIsoDate() {
        val instant = Instant.parse("2026-10-20T18:30:15.123Z")

        assertEquals("2026-10-20T18:30:15.123Z", Commons.getCurrentUtcDate(instant))
    }

    @Test
    fun parseUtcDate_withOrWithoutZone_returnsSameInstant() {
        val expected = Instant.parse("2026-10-20T18:30:00Z")

        assertEquals(expected, Commons.parseUtcDate("2026-10-20T18:30:00"))
        assertEquals(expected, Commons.parseUtcDate("2026-10-20T18:30:00.000Z"))
        assertEquals(expected, Commons.parseUtcDate("2026-10-20 18:30:00"))
        assertEquals(expected, Commons.parseUtcDate("2026-10-20T12:30:00-06:00"))
    }

    @Test
    fun toUtcIsoDate_serverDateWithoutZone_returnsUtcIsoDate() {
        assertEquals("2026-10-20T18:30:00.000Z", Commons.toUtcIsoDate("2026-10-20T18:30:00"))
    }

    @Test
    fun convertUtcDate_utcDate_returnsLocalTime() {
        assertEquals(
            "2026-10-20 12:30",
            Commons.convertUtcDate("2026-10-20T18:30:00.000Z", "yyyy-MM-dd HH:mm")
        )
    }

    @Test
    fun convertUtcDate_voidDate_isNotShiftedAndStaysVoid() {
        val formatted = Commons.convertUtcDate("1900-01-01T00:00:00", "dd/MM/yyyy HH:mm:ss")

        assertEquals("01/01/1900 00:00:00", formatted)
        assertTrue(Commons.isVoidDate(formatted))
    }
}
