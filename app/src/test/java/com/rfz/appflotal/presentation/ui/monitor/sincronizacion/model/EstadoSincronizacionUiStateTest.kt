package com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model

import com.rfz.appflotal.data.model.sincronizacion.EstadoEnvio
import com.rfz.appflotal.data.model.sincronizacion.EstadoOperacion
import com.rfz.appflotal.data.model.sincronizacion.EstadoSincronizacion
import com.rfz.appflotal.data.model.sincronizacion.OperacionPendiente
import com.rfz.appflotal.data.model.sincronizacion.TipoOperacion
import com.rfz.appflotal.data.model.sincronizacion.operacionesBloqueadas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EstadoSincronizacionUiStateTest {

    private fun operacion(
        id: Long,
        posicion: String,
        tipo: TipoOperacion = TipoOperacion.MONTAJE,
        estado: EstadoOperacion = EstadoOperacion.PENDIENTE,
        idGrupo: String? = null,
        error: String? = null
    ) = OperacionPendiente(id, tipo, posicion, idGrupo, estado, 0, error, creadoEn = id)

    private fun estado(vararg operaciones: OperacionPendiente, envio: EstadoEnvio = EstadoEnvio()) =
        operaciones.toList().let { EstadoSincronizacion(it, operacionesBloqueadas(it), envio) }

    @Test
    fun operacionesBloqueadas_rechazoEnSuPosicion_bloqueaSoloLasPosteriores() {
        val ops = listOf(
            operacion(1, "P3"),
            operacion(2, "P3", estado = EstadoOperacion.CON_ERROR),
            operacion(3, "P3"),
            operacion(4, "P5")
        )

        assertEquals(setOf(3L), operacionesBloqueadas(ops))
    }

    @Test
    fun conEstado_desmontajeConSuInspeccion_seMuestraEnUnaFila() {
        val ui = EstadoSincronizacionUiState().conEstado(
            estado(
                operacion(1, "P3", TipoOperacion.INSPECCION, idGrupo = "g"),
                operacion(2, "P3", TipoOperacion.DESMONTAJE, idGrupo = "g")
            ),
            conConexion = true
        )

        assertEquals(1, ui.enCola.size)
        assertEquals(TipoFilaSincronizacion.DESMONTAJE_CON_INSPECCION, ui.enCola.first().tipo)
    }

    @Test
    fun conEstado_inspeccionDelGrupoRechazada_laFilaUsaElIdYMotivoDeLaRechazada() {
        val ui = EstadoSincronizacionUiState().conEstado(
            estado(
                operacion(1, "P3", TipoOperacion.INSPECCION, EstadoOperacion.CON_ERROR, "g", "motivo"),
                operacion(2, "P3", TipoOperacion.DESMONTAJE, idGrupo = "g")
            ),
            conConexion = true
        )

        val fila = ui.requierenAtencion.single()
        assertEquals(1L, fila.id)
        assertEquals("motivo", fila.mensaje)
        assertTrue(ui.enCola.isEmpty())
        assertEquals(IndicadorSincronizacion.REQUIERE_ATENCION, ui.indicador)
        assertEquals(1, ui.cantidadIndicador)
    }

    @Test
    fun conEstado_rechazoYPendienteEnLaMismaPosicion_elAvisoMuestraElRechazo() {
        val ui = EstadoSincronizacionUiState().conEstado(
            estado(
                operacion(1, "P3", estado = EstadoOperacion.CON_ERROR, error = "motivo"),
                operacion(2, "P3")
            ),
            conConexion = true
        )

        assertTrue(ui.porPosicion.getValue("P3").conError)
        assertEquals(EstadoFilaSincronizacion.EN_ESPERA, ui.enCola.single().estado)
    }

    @Test
    fun conEstado_pendientesSinConexion_indicaSinConexion() {
        val ui = EstadoSincronizacionUiState().conEstado(estado(operacion(1, "P3")), conConexion = false)

        assertEquals(IndicadorSincronizacion.SIN_CONEXION, ui.indicador)
        assertEquals(1, ui.cantidadIndicador)
    }

    @Test
    fun conEstado_enviandoConConexion_marcaLaFilaComoEnviando() {
        val ui = EstadoSincronizacionUiState().conEstado(
            estado(
                operacion(1, "P3", estado = EstadoOperacion.ENVIANDO),
                envio = EstadoEnvio(procesando = true)
            ),
            conConexion = true
        )

        assertEquals(IndicadorSincronizacion.ENVIANDO, ui.indicador)
        assertEquals(EstadoFilaSincronizacion.ENVIANDO, ui.enCola.single().estado)
    }

    @Test
    fun conEstado_sesionVencidaConPendientes_requiereAtencion() {
        val ui = EstadoSincronizacionUiState().conEstado(
            estado(operacion(1, "P3"), envio = EstadoEnvio(sesionVencida = true)),
            conConexion = true
        )

        assertEquals(IndicadorSincronizacion.REQUIERE_ATENCION, ui.indicador)
    }

    @Test
    fun conEstado_colaVacia_quedaSincronizadoYCierraElDescarte() {
        val pendiente = FilaSincronizacionUi(9, TipoFilaSincronizacion.MONTAJE, "P1", 0, EstadoFilaSincronizacion.CON_ERROR, null)
        val ui = EstadoSincronizacionUiState(porDescartar = pendiente).conEstado(estado(), conConexion = true)

        assertEquals(IndicadorSincronizacion.SINCRONIZADO, ui.indicador)
        assertEquals(null, ui.porDescartar)
    }
}
