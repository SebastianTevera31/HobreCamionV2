package com.rfz.appflotal.data.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.rfz.appflotal.data.database.AppHombreCamionDatabase
import com.rfz.appflotal.data.model.sincronizacion.OperacionPendienteEntity
import com.rfz.appflotal.data.model.sincronizacion.TipoOperacion
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Orden de envío y bloqueo por posición de la cola de operaciones. */
@RunWith(AndroidJUnit4::class)
class OperacionPendienteDaoTest {

    private lateinit var database: AppHombreCamionDatabase
    private lateinit var dao: OperacionPendienteDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppHombreCamionDatabase::class.java
        ).build()
        dao = database.operacionPendienteDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private suspend fun insertar(posicion: String, idUsuario: Int = 1, idGrupo: String? = null) =
        dao.insertar(
            OperacionPendienteEntity(
                idCliente = "c-$posicion",
                tipo = TipoOperacion.MONTAJE,
                posicion = posicion,
                idMonitor = 1,
                idUsuario = idUsuario,
                idGrupo = idGrupo,
                payload = "{}",
                creadoEn = 0
            )
        )

    @Test
    fun siguienteParaEnviar_variasPendientes_devuelveLaMasAntigua() = runBlocking {
        val primera = insertar("P3")
        insertar("P5")

        assertEquals(primera, dao.siguienteParaEnviar(idUsuario = 1)?.id)
    }

    @Test
    fun siguienteParaEnviar_rechazoEnSuPosicion_saltaALaSiguientePosicion() = runBlocking {
        val rechazada = insertar("P3")
        insertar("P3")
        val otraPosicion = insertar("P5")
        dao.marcarRechazada(rechazada, "motivo")

        assertEquals(otraPosicion, dao.siguienteParaEnviar(idUsuario = 1)?.id)
        assertEquals(listOf("P5"), dao.posicionesEnviables(idUsuario = 1))
    }

    @Test
    fun siguienteParaEnviar_otroUsuario_noLaDevuelve() = runBlocking {
        insertar("P3", idUsuario = 2)

        assertNull(dao.siguienteParaEnviar(idUsuario = 1))
    }

    @Test
    fun eliminarConGrupo_parteDelDesmontaje_quitaTambienSuInspeccion() = runBlocking {
        val inspeccion = insertar("P3", idGrupo = "g")
        insertar("P3", idGrupo = "g")
        val otra = insertar("P5")

        dao.eliminarConGrupo(inspeccion)

        assertEquals(otra, dao.siguienteParaEnviar(idUsuario = 1)?.id)
        assertNull(dao.obtener(inspeccion))
    }
}
