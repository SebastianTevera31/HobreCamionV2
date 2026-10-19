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
        dao.markNotified(listOf(1), notifiedAt = 30 * day)

        dao.applyCatalog(listOf(response(1, days = 45)), now = 40 * day)

        val stored = dao.getAll().single()
        assertEquals(0L, stored.firstSeenAt)
        assertEquals(30 * day, stored.lastNotifiedAt)
        assertEquals(45, stored.daysConf)
    }

    @Test
    fun takeDue_registraLaEmisionUnaSolaVez() = runBlocking {
        dao.applyCatalog(listOf(response(1, days = 30), response(2, days = 240)), now = 0)

        val first = dao.takeDue(now = 30 * day)
        val second = dao.takeDue(now = 30 * day + 1)

        assertEquals(listOf(1), first.map { it.id })
        assertEquals(30 * day, first.single().lastNotifiedAt)
        assertTrue(second.isEmpty())
        assertNull(dao.getAll().first { it.id == 2 }.lastNotifiedAt)
    }

    @Test
    fun acknowledge_noMueveElSiguienteVencimiento() = runBlocking {
        dao.applyCatalog(listOf(response(1, days = 30)), now = 0)
        dao.takeDue(now = 30 * day)
        dao.acknowledge(1, acknowledgedAt = 45 * day)

        assertTrue(dao.takeDue(now = 59 * day).isEmpty())
        assertEquals(listOf(1), dao.takeDue(now = 60 * day).map { it.id })
    }
}
