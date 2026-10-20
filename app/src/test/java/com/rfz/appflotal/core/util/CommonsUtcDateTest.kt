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
    fun `emite la hora en UTC real`() {
        val instant = Instant.parse("2026-10-20T18:30:15.123Z")
        assertEquals("2026-10-20T18:30:15.123Z", Commons.getCurrentUtcDate(instant))
    }

    @Test
    fun `interpreta como UTC fechas con y sin zona`() {
        val expected = Instant.parse("2026-10-20T18:30:00Z")
        assertEquals(expected, Commons.parseUtcDate("2026-10-20T18:30:00"))
        assertEquals(expected, Commons.parseUtcDate("2026-10-20T18:30:00.000Z"))
        assertEquals(expected, Commons.parseUtcDate("2026-10-20 18:30:00"))
        assertEquals(expected, Commons.parseUtcDate("2026-10-20T12:30:00-06:00"))
    }

    @Test
    fun `normaliza la fecha del servidor al formato UTC`() {
        assertEquals("2026-10-20T18:30:00.000Z", Commons.toUtcIsoDate("2026-10-20T18:30:00"))
    }

    @Test
    fun `muestra la fecha UTC en hora local`() {
        assertEquals(
            "2026-10-20 12:30",
            Commons.convertUtcDate("2026-10-20T18:30:00.000Z", "yyyy-MM-dd HH:mm")
        )
    }

    @Test
    fun `la fecha vacia no se desplaza de zona`() {
        val formatted = Commons.convertUtcDate("1900-01-01T00:00:00", "dd/MM/yyyy HH:mm:ss")
        assertEquals("01/01/1900 00:00:00", formatted)
        assertTrue(Commons.isVoidDate(formatted))
    }
}
