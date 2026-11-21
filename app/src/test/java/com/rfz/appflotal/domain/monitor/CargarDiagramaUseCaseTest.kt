package com.rfz.appflotal.domain.monitor

import com.rfz.appflotal.data.repository.monitor.DiagramaMonitorRepository
import com.rfz.appflotal.domain.sincronizacion.FakeMontajes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

private class FakeDiagrama : DiagramaMonitorRepository {
    var delServidor: List<LlantaDiagrama>? = null
    var cache: List<LlantaDiagrama> = emptyList()
    var guardado: List<LlantaDiagrama>? = null
    var consultasAlServidor = 0

    override suspend fun obtenerDelServidor(monitorId: Int): List<LlantaDiagrama>? {
        consultasAlServidor++
        return delServidor
    }

    override suspend fun obtenerDeCache(monitorId: Int) = cache

    override suspend fun guardarEnCache(monitorId: Int, llantas: List<LlantaDiagrama>) {
        guardado = llantas
    }

    override fun observarActivas(): Flow<Map<String, Boolean>> = flowOf(emptyMap())
}

class CargarDiagramaUseCaseTest {

    private val diagrama = FakeDiagrama()
    private val montajes = FakeMontajes()
    private val cargar = CargarDiagramaUseCase(diagrama, montajes)

    private fun llanta(posicion: String) = LlantaDiagrama(posicion, false, false, true, 0, 0)

    @Test
    fun invoke_conRedYServidorResponde_devuelveServidorYGuardaCache() = runBlocking {
        diagrama.delServidor = listOf(llanta("P1"))
        diagrama.cache = listOf(llanta("P9"))

        assertEquals(listOf(llanta("P1")), cargar(monitorId = 1, conRed = true))
        assertEquals(listOf(llanta("P1")), diagrama.guardado)
        assertEquals(1, montajes.reconciliaciones)
    }

    @Test
    fun invoke_conRedYServidorSinRespuesta_usaLaCache() = runBlocking {
        diagrama.delServidor = null
        diagrama.cache = listOf(llanta("P9"))

        assertEquals(listOf(llanta("P9")), cargar(monitorId = 1, conRed = true))
        assertNull(diagrama.guardado)
    }

    @Test
    fun invoke_sinRed_usaLaCacheSinConsultarAlServidor() = runBlocking {
        diagrama.cache = listOf(llanta("P9"))

        assertEquals(listOf(llanta("P9")), cargar(monitorId = 1, conRed = false))
        assertEquals(0, diagrama.consultasAlServidor)
        assertEquals(0, montajes.reconciliaciones)
    }

    @Test
    fun invoke_servidorSinLlantas_noSobrescribeLaCache() = runBlocking {
        diagrama.delServidor = emptyList()

        assertEquals(emptyList<LlantaDiagrama>(), cargar(monitorId = 1, conRed = true))
        assertNull(diagrama.guardado)
    }
}
