package com.rfz.appflotal.data.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.rfz.appflotal.data.database.AppHombreCamionDatabase
import com.rfz.appflotal.data.model.catalog.MaintenanceNotificationResponse
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/** Catálogo de recordatorios de mantenimiento: reemplazo por ids, emisión y "Enterado". */
@RunWith(AndroidJUnit4::class)
class MaintenanceNotificationsDaoTest {

    private val day = TimeUnit.DAYS.toMillis(1)

    private lateinit var database: AppHombreCamionDatabase
    private lateinit var dao: MaintenanceNotificationsDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppHombreCamionDatabase::class.java
        ).build()
        dao = database.maintenanceNotificationsDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun response(id: Int, days: Int = 30) =
        MaintenanceNotificationResponse(id = id, message = "Mensaje $id", daysConf = days)

    @Test
    fun applyCatalog_eliminaLosIdsQueYaNoExponeYLosDevuelve() = runBlocking {
        dao.applyCatalog(listOf(response(1), response(2)), now = 0)

        val removed = dao.applyCatalog(listOf(response(2), response(3)), now = 10)

        assertEquals(listOf(1), removed)
        assertEquals(listOf(2, 3), dao.getAll().map { it.id }.sorted())
    }

    @Test
    fun applyCatalog_listaVacia_vaciaLaTabla() = runBlocking {
        dao.applyCatalog(listOf(response(1), response(2)), now = 0)

        val removed = dao.applyCatalog(emptyList(), now = 10)

        assertEquals(listOf(1, 2), removed.sorted())
        assertTrue(dao.getAll().isEmpty())
    }

    @Test
    fun applyCatalog_conservaElCicloDeLosConocidos() = runBlocking {
        dao.applyCatalog(listOf(response(1)), now = 0)
        dao.takeDue(now = 30 * day, odometerKm = null)

        dao.applyCatalog(listOf(response(1, days = 45)), now = 40 * day)

        val stored = dao.getAll().single()
        assertEquals(0L, stored.firstSeenAt)
        assertEquals(30 * day, stored.lastNotifiedAt)
        assertEquals(45, stored.daysConf)
    }

    @Test
    fun takeDue_registraLaEmisionUnaSolaVez() = runBlocking {
        dao.applyCatalog(listOf(response(1, days = 30), response(2, days = 240)), now = 0)

        val first = dao.takeDue(now = 30 * day, odometerKm = null)
        val second = dao.takeDue(now = 30 * day + 1, odometerKm = null)

        assertEquals(listOf(1), first.map { it.id })
        assertEquals(30 * day, first.single().lastNotifiedAt)
        assertTrue(second.isEmpty())
        assertNull(dao.getAll().first { it.id == 2 }.lastNotifiedAt)
    }

    @Test
    fun acknowledge_noMueveElSiguienteVencimiento() = runBlocking {
        dao.applyCatalog(listOf(response(1, days = 30)), now = 0)
        dao.takeDue(now = 30 * day, odometerKm = null)
        dao.acknowledge(1, acknowledgedAt = 45 * day)

        assertTrue(dao.takeDue(now = 59 * day, odometerKm = null).isEmpty())
        assertEquals(listOf(1), dao.takeDue(now = 60 * day, odometerKm = null).map { it.id })
    }

    @Test
    fun takeDue_alineacionVencePorKmYReiniciaLaReferencia() = runBlocking {
        dao.applyCatalog(listOf(response(1, days = 30), response(2, days = 240)), now = 0)

        // Primer odómetro: solo fija la referencia de la alineación.
        assertTrue(dao.takeDue(now = day, odometerKm = 100_000).isEmpty())
        assertEquals(100_000, dao.getAll().first { it.id == 2 }.baselineOdometerKm)
        assertNull(dao.getAll().first { it.id == 1 }.baselineOdometerKm)

        // 120,000 km después, antes de los 30 y de los 240 días: solo vence la alineación.
        val due = dao.takeDue(now = 10 * day, odometerKm = 220_000)

        assertEquals(listOf(2), due.map { it.id })
        val stored = dao.getAll().first { it.id == 2 }
        assertEquals(10 * day, stored.lastNotifiedAt)
        assertEquals(220_000, stored.baselineOdometerKm)
        assertTrue(dao.takeDue(now = 10 * day + 1, odometerKm = 220_000).isEmpty())
    }

    @Test
    fun applyCatalog_conservaLaReferenciaDeOdometro() = runBlocking {
        dao.applyCatalog(listOf(response(2, days = 240)), now = 0)
        dao.takeDue(now = day, odometerKm = 100_000)

        dao.applyCatalog(listOf(response(2, days = 240)), now = 5 * day)

        assertEquals(100_000, dao.getAll().single().baselineOdometerKm)
    }
}
