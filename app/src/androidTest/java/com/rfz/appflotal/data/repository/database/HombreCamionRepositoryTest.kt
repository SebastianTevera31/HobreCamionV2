package com.rfz.appflotal.data.repository.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.rfz.appflotal.data.database.AppHombreCamionDatabase
import com.rfz.appflotal.data.model.assembly.AssemblyTireEntity
import com.rfz.appflotal.data.model.sincronizacion.EstadoOperacion
import com.rfz.appflotal.data.model.sincronizacion.OperacionPendienteEntity
import com.rfz.appflotal.data.model.sincronizacion.TipoOperacion
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Al cerrar sesión se borra todo menos la cola de operaciones sin enviar. */
@RunWith(AndroidJUnit4::class)
class HombreCamionRepositoryTest {

    private lateinit var database: AppHombreCamionDatabase
    private lateinit var repository: HombreCamionRepository

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppHombreCamionDatabase::class.java).build()
        repository = HombreCamionRepository(database.hcSoftDao(), database, context)
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun operacion(posicion: String, estado: EstadoOperacion) = OperacionPendienteEntity(
        idCliente = "c-$posicion",
        tipo = TipoOperacion.MONTAJE,
        posicion = posicion,
        idMonitor = 9,
        idUsuario = 7,
        idGrupo = null,
        payload = "{}",
        estado = estado,
        ultimoError = if (estado == EstadoOperacion.CON_ERROR) "motivo" else null,
        creadoEn = 0
    )

    @Test
    fun clearAllLocalData_conOperacionesSinEnviar_lasConservaYBorraLoDemas() = runBlocking {
        val dao = database.operacionPendienteDao()
        val primera = dao.insertar(operacion("P3", EstadoOperacion.PENDIENTE))
        val rechazada = dao.insertar(operacion("P5", EstadoOperacion.CON_ERROR))
        database.assemblyTireDao().upsertAssemblyTire(
            AssemblyTireEntity("P3", idAxle = 1, idTire = 2, idMonitor = 9, odometer = 0, assemblyDate = "", updatedAt = 0)
        )

        repository.clearAllLocalData()

        val conservadas = dao.todas()
        assertEquals(listOf(primera, rechazada), conservadas.map { it.id })
        assertEquals(EstadoOperacion.CON_ERROR, conservadas.last().estado)
        assertNull(database.assemblyTireDao().getAssemblyTire("P3"))
    }

    @Test
    fun clearAllLocalData_sinOperaciones_dejaLaColaVacia() = runBlocking {
        repository.clearAllLocalData()

        assertEquals(0, database.operacionPendienteDao().todas().size)
    }
}
