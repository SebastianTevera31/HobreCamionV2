package com.rfz.appflotal.domain.sincronizacion

import com.rfz.appflotal.data.model.sincronizacion.EstadoOperacion
import com.rfz.appflotal.data.model.sincronizacion.ResultadoEnvio
import com.rfz.appflotal.data.model.sincronizacion.ResultadoProcesamiento
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProcesarColaUseCaseTest {

    private val cola = FakeColaSincronizacion()
    private val montajes = FakeMontajes()
    private val procesarCola = ProcesarColaUseCase(cola, montajes)

    @Test
    fun invoke_colaVacia_completaSinReconciliar() = runBlocking {
        assertEquals(ResultadoProcesamiento.COMPLETA, procesarCola())
        assertEquals(0, montajes.reconciliaciones)
    }

    @Test
    fun invoke_todasAceptadas_lasEnviaEnOrdenYReconcilia() = runBlocking {
        cola.agregar(2, "P5")
        cola.agregar(1, "P3")

        assertEquals(ResultadoProcesamiento.COMPLETA, procesarCola())
        assertEquals(listOf(1L, 2L), cola.enviadas)
        assertTrue(cola.operaciones.isEmpty())
        assertEquals(1, montajes.reconciliaciones)
    }

    @Test
    fun invoke_errorConexion_detieneLaColaYLaDejaEnviando() = runBlocking {
        cola.agregar(1, "P3")
        cola.agregar(2, "P5")
        cola.respuestas[1] = ResultadoEnvio.ErrorConexion

        assertEquals(ResultadoProcesamiento.REINTENTAR, procesarCola())
        assertEquals(listOf(1L), cola.enviadas)
        // Pudo llegar al servidor: la siguiente vez se verifica antes de reenviarla.
        assertEquals(EstadoOperacion.ENVIANDO, cola.estadoDe(1))
        assertEquals(EstadoOperacion.PENDIENTE, cola.estadoDe(2))
        assertEquals(0, montajes.reconciliaciones)
    }

    @Test
    fun invoke_rechazo_bloqueaSoloLasSiguientesDeSuPosicion() = runBlocking {
        cola.agregar(1, "P3")
        cola.agregar(2, "P3")
        cola.agregar(3, "P5")
        cola.respuestas[1] = ResultadoEnvio.Rechazada("motivo")

        assertEquals(ResultadoProcesamiento.COMPLETA, procesarCola())
        assertEquals(listOf(1L, 3L), cola.enviadas)
        assertEquals(EstadoOperacion.CON_ERROR, cola.estadoDe(1))
        assertEquals("motivo", cola.obtener(1)?.ultimoError)
        assertEquals(EstadoOperacion.PENDIENTE, cola.estadoDe(2))
        assertEquals(1, montajes.reconciliaciones)
    }

    @Test
    fun invoke_rechazoYaAplicadoEnServidor_laDaPorAceptada() = runBlocking {
        cola.agregar(1, "P3")
        cola.respuestas[1] = ResultadoEnvio.Rechazada("motivo")
        cola.aplicadas[1] = true

        assertEquals(ResultadoProcesamiento.COMPLETA, procesarCola())
        assertNull(cola.obtener(1))
    }

    @Test
    fun invoke_interrumpidaYaAplicada_noLaReenvia() = runBlocking {
        cola.agregar(1, "P3", estado = EstadoOperacion.ENVIANDO)
        cola.aplicadas[1] = true

        assertEquals(ResultadoProcesamiento.COMPLETA, procesarCola())
        assertTrue(cola.enviadas.isEmpty())
        assertNull(cola.obtener(1))
    }

    @Test
    fun invoke_interrumpidaSinPoderComprobar_laReenvia() = runBlocking {
        cola.agregar(1, "P3", estado = EstadoOperacion.ENVIANDO)
        cola.aplicadas[1] = null

        assertEquals(ResultadoProcesamiento.COMPLETA, procesarCola())
        assertEquals(listOf(1L), cola.enviadas)
    }

    @Test
    fun invoke_sesionVencida_laRegresaAPendienteYSeDetiene() = runBlocking {
        cola.agregar(1, "P3")
        cola.agregar(2, "P5")
        cola.respuestas[1] = ResultadoEnvio.SesionVencida

        assertEquals(ResultadoProcesamiento.SESION_VENCIDA, procesarCola())
        assertEquals(listOf(1L), cola.enviadas)
        assertEquals(EstadoOperacion.PENDIENTE, cola.estadoDe(1))
    }
}
