package com.rfz.appflotal.domain.sincronizacion

import com.rfz.appflotal.data.model.sincronizacion.ResultadoEnvio
import com.rfz.appflotal.data.model.sincronizacion.ResultadoOperacion
import com.rfz.appflotal.data.model.sincronizacion.TipoOperacion
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EnviarOperacionesUseCaseTest {

    private val cola = FakeColaSincronizacion()
    private val montajes = FakeMontajes()
    private val enviar = EnviarOperacionesUseCase(
        procesarCola = ProcesarColaUseCase(cola, montajes),
        cola = cola,
        descartar = DescartarOperacionUseCase(cola, montajes)
    )

    @Test
    fun invoke_servidorAcepta_devuelveEnviada() = runBlocking {
        cola.agregar(1, "P3")

        assertEquals(ResultadoOperacion.Enviada, enviar(listOf(1)))
        assertEquals(0, cola.programaciones)
    }

    @Test
    fun invoke_sinConexion_devuelveEnColaYProgramaElEnvio() = runBlocking {
        cola.agregar(1, "P3")
        cola.respuestas[1] = ResultadoEnvio.ErrorConexion

        assertEquals(ResultadoOperacion.EnCola, enviar(listOf(1)))
        assertEquals(1, cola.programaciones)
    }

    @Test
    fun invoke_inspeccionDelDesmontajeRechazada_descartaElGrupoYDevuelveElMotivo() = runBlocking {
        cola.agregar(1, "P3", TipoOperacion.INSPECCION, idGrupo = "g")
        cola.agregar(2, "P3", TipoOperacion.DESMONTAJE, idGrupo = "g")
        cola.respuestas[1] = ResultadoEnvio.Rechazada("motivo")

        assertEquals(ResultadoOperacion.Rechazada("motivo"), enviar(listOf(1, 2)))
        // El desmontaje no se envía sin su inspección y no queda en la cola.
        assertEquals(listOf(1L), cola.enviadas)
        assertTrue(cola.operaciones.isEmpty())
    }

    @Test
    fun invoke_detrasDeOtraRechazada_devuelveEnColaSinDescartarla() = runBlocking {
        cola.agregar(1, "P3")
        cola.agregar(2, "P3")
        cola.respuestas[1] = ResultadoEnvio.Rechazada("motivo")

        assertEquals(ResultadoOperacion.EnCola, enviar(listOf(2)))
        assertEquals(2, cola.operaciones.size)
    }
}
