package com.rfz.appflotal.presentation.ui.monitor.viewmodel

import com.rfz.appflotal.data.model.tpms.SensorAlerts
import com.rfz.appflotal.domain.tpms.LecturaLlanta
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SelectorLlantaTest {

    private val scope = CoroutineScope(Job())
    private val selector = SelectorLlanta(scope) { _, _ -> }

    @After
    fun tearDown() = scope.cancel()

    private fun lectura(posicion: String, presion: SensorAlerts = SensorAlerts.NO_DATA) = LecturaLlanta(
        posicion = posicion, montada = true,
        presionPsi = 100f, alertaPresion = presion,
        temperaturaC = 30f, alertaTemperatura = SensorAlerts.NO_DATA,
        fecha = "", bateria = SensorAlerts.NO_DATA,
        pinchazo = SensorAlerts.NO_DATA, extraccion = SensorAlerts.NO_DATA
    )

    @Test
    fun aviso_enAutomatico_noHayAviso() {
        selector.recibir("P14", lectura("P14", SensorAlerts.LOW_PRESSURE))

        assertNull(selector.aviso())
    }

    @Test
    fun aviso_fijadaConAlertasDeOtras_nombraLaMasRecienteYCuentaLasDemas() {
        selector.tocar("P3")
        selector.recibir("P14", lectura("P14", SensorAlerts.LOW_PRESSURE))
        selector.recibir("P17", lectura("P17", SensorAlerts.HIGH_PRESSURE))

        val aviso = selector.aviso()!!
        assertEquals("P17", aviso.posicion)
        assertEquals(SensorAlerts.HIGH_PRESSURE, aviso.alerta)
        assertEquals(1, aviso.adicionales)
        assertEquals(setOf("P14", "P17"), aviso.posiciones)
    }

    @Test
    fun recibir_fijadaTramaDeOtraLlanta_elPanelNoLaMuestra() {
        selector.tocar("P3")

        assertFalse(selector.recibir("P14", lectura("P14", SensorAlerts.LOW_PRESSURE)))
        assertTrue(selector.recibir("P3", lectura("P3")))
    }

    @Test
    fun recibir_tramaSinPosicion_noSeMuestra() {
        assertFalse(selector.recibir("", lectura("")))
    }
}
