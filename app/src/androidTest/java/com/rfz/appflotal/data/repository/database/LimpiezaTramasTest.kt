package com.rfz.appflotal.data.repository.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.rfz.appflotal.core.util.Commons.getCurrentUtcDate
import com.rfz.appflotal.data.database.AppHombreCamionDatabase
import com.rfz.appflotal.data.model.database.DataframeEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant
import java.time.ZoneOffset

/** Tramas resueltas se borran al mes; las pendientes de enviar, a los dos meses. */
@RunWith(AndroidJUnit4::class)
class LimpiezaTramasTest {

    private val ahora = Instant.parse("2026-11-23T12:00:00Z")
    private lateinit var database: AppHombreCamionDatabase
    private lateinit var repository: DataframeTableRepository

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppHombreCamionDatabase::class.java).build()
        repository = DataframeTableRepository(database.dataframeDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    private suspend fun trama(diasAtras: Long, enviada: Boolean, activa: Boolean = true): Long {
        val fecha = ahora.atZone(ZoneOffset.UTC).minusDays(diasAtras).toInstant()
        return repository.insert(
            DataframeEntity(
                monitorId = 9, sensorId = "S$diasAtras", dataFrame = "AA",
                timestamp = getCurrentUtcDate(fecha), sent = enviada, active = activa
            )
        )
    }

    private suspend fun restantes(): Set<String> =
        database.dataframeDao().getLastRecords(9).mapNotNull { it?.sensorId }.toSet()

    @Test
    fun limpiarTramas_enviadaDeHaceMasDeUnMes_seBorra() = runBlocking {
        trama(diasAtras = 40, enviada = true)
        trama(diasAtras = 10, enviada = true)

        assertEquals(1, repository.limpiarTramas(ahora))
        assertEquals(setOf("S10"), restantes())
    }

    @Test
    fun limpiarTramas_rechazadaDeHaceMasDeUnMes_seBorraComoResuelta() = runBlocking {
        trama(diasAtras = 40, enviada = false, activa = false)

        assertEquals(1, repository.limpiarTramas(ahora))
    }

    @Test
    fun limpiarTramas_pendienteDeHaceMasDeUnMes_seConservaHastaLosDosMeses() = runBlocking {
        trama(diasAtras = 40, enviada = false)
        trama(diasAtras = 70, enviada = false)

        assertEquals(1, repository.limpiarTramas(ahora))
        assertEquals(setOf("S40"), restantes())
    }
}
