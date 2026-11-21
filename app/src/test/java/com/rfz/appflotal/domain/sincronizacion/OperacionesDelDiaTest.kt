package com.rfz.appflotal.domain.sincronizacion

import com.rfz.appflotal.data.model.sincronizacion.EstadoOperacion
import com.rfz.appflotal.data.model.sincronizacion.OperacionPendiente
import com.rfz.appflotal.data.model.sincronizacion.TipoOperacion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class OperacionesDelDiaTest {

    private val zona = ZoneId.of("America/Mexico_City")
    private val hoy = LocalDate.of(2026, 11, 21)
    private val hoyMillis = hoy.atTime(10, 0).atZone(zona).toInstant().toEpochMilli()
    private val ayerMillis = hoy.minusDays(1).atTime(10, 0).atZone(zona).toInstant().toEpochMilli()

    private fun calcular(
        ultimaInspeccion: String? = null,
        fechaMontaje: String? = null,
        cola: List<OperacionPendiente> = emptyList(),
    ) = calcularOperacionesDelDia("P1", ultimaInspeccion, fechaMontaje, cola, hoy, zona)

    private fun inspeccionEnCola(
        posicion: String = "p1 ",
        estado: EstadoOperacion = EstadoOperacion.PENDIENTE,
        creadoEn: Long = hoyMillis,
    ) = OperacionPendiente(
        id = 1, tipo = TipoOperacion.INSPECCION, posicion = posicion, idGrupo = null,
        estado = estado, intentos = 0, ultimoError = null, creadoEn = creadoEn
    )

    @Test
    fun calcular_sinOperacionesHoy_permiteInspeccionYDesmontajeLaPide() {
        val delDia = calcular(
            ultimaInspeccion = "2026-11-20T18:30:00.000Z",
            fechaMontaje = "2026-11-01T09:00:00"
        )

        assertNull(delDia.bloqueoInspeccion)
        assertTrue(delDia.puedeInspeccionar)
        assertTrue(delDia.desmontajeRequiereInspeccion)
    }

    @Test
    fun calcular_inspeccionAceptadaHoy_bloqueaInspeccionYDesmontajeNoLaPide() {
        val delDia = calcular(ultimaInspeccion = "2026-11-21T08:15:00.000Z")

        assertEquals(BloqueoInspeccion.INSPECCIONADA_HOY, delDia.bloqueoInspeccion)
        assertFalse(delDia.desmontajeRequiereInspeccion)
    }

    @Test
    fun calcular_montadaHoy_bloqueaInspeccionYDesmontajeNoLaPide() {
        val delDia = calcular(fechaMontaje = "2026-11-21T07:00:00.000Z")

        assertEquals(BloqueoInspeccion.MONTADA_HOY, delDia.bloqueoInspeccion)
        assertFalse(delDia.desmontajeRequiereInspeccion)
    }

    @Test
    fun calcular_inspeccionEnColaDeHoy_cuentaComoHecha() {
        val delDia = calcular(cola = listOf(inspeccionEnCola()))

        assertEquals(BloqueoInspeccion.INSPECCIONADA_HOY, delDia.bloqueoInspeccion)
    }

    @Test
    fun calcular_inspeccionRechazadaEnCola_noCuenta() {
        val delDia = calcular(cola = listOf(inspeccionEnCola(estado = EstadoOperacion.CON_ERROR)))

        assertTrue(delDia.puedeInspeccionar)
    }

    @Test
    fun calcular_inspeccionEnColaDeAyerOOtraPosicion_noCuenta() {
        val delDia = calcular(
            cola = listOf(inspeccionEnCola(creadoEn = ayerMillis), inspeccionEnCola(posicion = "P2"))
        )

        assertTrue(delDia.puedeInspeccionar)
    }

    @Test
    fun calcular_fechaIlegibleOVacia_noBloquea() {
        val delDia = calcular(ultimaInspeccion = "01/01/1900 00:00:00", fechaMontaje = "")

        assertTrue(delDia.puedeInspeccionar)
    }
}
