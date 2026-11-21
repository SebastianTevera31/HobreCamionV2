package com.rfz.appflotal.domain.monitor

import com.rfz.appflotal.data.model.tpms.SensorAlerts
import com.rfz.appflotal.domain.tpms.LecturaLlanta
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SeleccionLlantaTest {

    private val auto = SeleccionLlanta()

    @Test
    fun alRecibir_automaticoSinAlertas_sigueLaUltimaTrama() {
        val s = auto.alRecibir("P3", false).alRecibir("P7", false)

        assertEquals("P7", s.posicion)
    }

    @Test
    fun alRecibir_automaticoConAlerta_laAlertaTomaElPanel() {
        val s = auto.alRecibir("P3", false).alRecibir("P14", true)

        assertEquals("P14", s.posicion)
    }

    @Test
    fun alRecibir_automaticoMostrandoAlerta_unaTramaNormalNoLaDesplaza() {
        val s = auto.alRecibir("P14", true).alRecibir("P3", false)

        assertEquals("P14", s.posicion)
    }

    @Test
    fun alRecibir_automaticoMostrandoAlerta_otraAlertaMasRecienteLaDesplaza() {
        val s = auto.alRecibir("P14", true).alRecibir("P17", true)

        assertEquals("P17", s.posicion)
    }

    @Test
    fun alRecibir_alertaMostradaSeNormaliza_vuelveASeguirCualquierTrama() {
        val s = auto.alRecibir("P14", true).alRecibir("P14", false).alRecibir("P3", false)

        assertEquals("P3", s.posicion)
        assertFalse("P14" in s.enAlerta)
    }

    @Test
    fun alRecibir_fijadaConAlertaDeOtraLlanta_noCambiaElPanelYQuedaSinVer() {
        val s = auto.alTocar("P3", ahora = 0).alRecibir("P14", true).alRecibir("P17", true)

        assertEquals("P3", s.posicion)
        assertEquals(listOf("P14", "P17"), s.sinVer)
    }

    @Test
    fun alRecibir_fijadaYLaAlertaSinVerSeNormaliza_saleDelAviso() {
        val s = auto.alTocar("P3", ahora = 0).alRecibir("P14", true).alRecibir("P14", false)

        assertTrue(s.sinVer.isEmpty())
    }

    @Test
    fun alTocar_llantaSinVer_laQuitaDelAvisoYReiniciaElTiempo() {
        val s = auto.alTocar("P3", ahora = 0).alRecibir("P14", true).alTocar("P14", ahora = 30_000)

        assertEquals("P14", s.posicion)
        assertTrue(s.sinVer.isEmpty())
        assertEquals(ModoSeleccion.Fijada(hasta = 90_000), s.modo)
    }

    @Test
    fun alVencer_antesDeTiempo_sigueFijada() {
        val s = auto.alTocar("P3", ahora = 0).alVencer(ahora = 59_999)

        assertEquals(ModoSeleccion.Fijada(hasta = DURACION_FIJADA_MS), s.modo)
    }

    @Test
    fun alVencer_conAlertasSinVer_vuelveAAutomaticoEnLaMasReciente() {
        val s = auto.alTocar("P3", ahora = 0)
            .alRecibir("P14", true).alRecibir("P17", true)
            .alVencer(ahora = DURACION_FIJADA_MS)

        assertEquals(ModoSeleccion.Automatica, s.modo)
        assertEquals("P17", s.posicion)
        assertTrue(s.sinVer.isEmpty())
    }

    @Test
    fun alRecibir_tramaSinPosicion_noCambiaNada() {
        val s = auto.alRecibir("P3", false)

        assertEquals(s, s.alRecibir("", true))
    }

    @Test
    fun tieneAlertaPrioritaria_soloBateriaBaja_noEsPrioritaria() {
        val lectura = LecturaLlanta(
            posicion = "P3", montada = true,
            presionPsi = 110f, alertaPresion = SensorAlerts.NO_DATA,
            temperaturaC = 30f, alertaTemperatura = SensorAlerts.NO_DATA,
            fecha = "", bateria = SensorAlerts.LOW_BATTERY,
            pinchazo = SensorAlerts.NO_DATA, extraccion = SensorAlerts.NO_DATA
        )

        assertFalse(lectura.tieneAlertaPrioritaria)
        assertTrue(lectura.copy(alertaPresion = SensorAlerts.LOW_PRESSURE).tieneAlertaPrioritaria)
    }

    @Test
    fun tieneAlertaPrioritaria_enExtraccion_esPrioritaria() {
        val lectura = LecturaLlanta(
            posicion = "P5", montada = true,
            presionPsi = 0f, alertaPresion = SensorAlerts.NO_DATA,
            temperaturaC = 25f, alertaTemperatura = SensorAlerts.NO_DATA,
            fecha = "", bateria = SensorAlerts.NO_DATA,
            pinchazo = SensorAlerts.NO_DATA, extraccion = SensorAlerts.REMOVAL
        )

        assertTrue(lectura.tieneAlertaPrioritaria)
    }
}
