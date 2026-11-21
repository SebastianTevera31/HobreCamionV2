package com.rfz.appflotal.domain.tpms

import com.rfz.appflotal.data.model.tpms.SensorAlerts
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.aTireUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LecturaLlantaTest {

    @Test
    fun estaEnAlerta_sinAlertas_devuelveFalso() {
        assertFalse(estaEnAlerta(SensorAlerts.NO_DATA, SensorAlerts.NO_DATA, SensorAlerts.NO_DATA, SensorAlerts.NO_DATA))
    }

    @Test
    fun estaEnAlerta_soloBateriaBaja_devuelveVerdadero() {
        assertTrue(estaEnAlerta(SensorAlerts.NO_DATA, SensorAlerts.NO_DATA, SensorAlerts.LOW_BATTERY, SensorAlerts.NO_DATA))
    }

    @Test
    fun aTireUiState_lectura_conservaValoresBaseParaConvertirAlMostrar() {
        val lectura = LecturaLlanta(
            posicion = "P3",
            montada = true,
            presionPsi = 110f,
            alertaPresion = SensorAlerts.LOW_PRESSURE,
            temperaturaC = 38f,
            alertaTemperatura = SensorAlerts.NO_DATA,
            fecha = "18/11/2026 10:00",
            bateria = SensorAlerts.NO_DATA,
            pinchazo = SensorAlerts.NO_DATA,
            extraccion = SensorAlerts.NO_DATA
        )

        val ui = lectura.aTireUiState()

        // El ViewModel convierte desde raw*; si aquí llegaran convertidos se convertirían dos veces.
        assertEquals(110f, ui.rawPressure)
        assertEquals(38f, ui.rawTemperature)
        assertEquals(SensorAlerts.LOW_PRESSURE, ui.pressure.second)
        assertEquals("P3", ui.currentTire)
        assertTrue(ui.isAssembled)
    }
}
