package com.rfz.appflotal.data.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.rfz.appflotal.data.database.AppHombreCamionDatabase
import com.rfz.appflotal.data.model.catalogo.CatalogoEntity
import com.rfz.appflotal.data.model.catalogo.DescargaCatalogoEntity
import com.rfz.appflotal.data.model.catalogo.TipoDatoOffline
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Cada descarga reemplaza solo su tipo de catálogo y registra cuándo se hizo. */
@RunWith(AndroidJUnit4::class)
class CatalogoDaoTest {

    private lateinit var database: AppHombreCamionDatabase
    private lateinit var dao: CatalogoDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppHombreCamionDatabase::class.java
        ).build()
        dao = database.catalogoDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun elemento(tipo: TipoDatoOffline, id: Int) = CatalogoEntity(tipo, id, "Elemento $id")

    @Test
    fun reemplazar_unTipo_noTocaLosDemasYRegistraLaDescarga() = runBlocking {
        val destino = TipoDatoOffline.DESTINO
        val causa = TipoDatoOffline.CAUSA_DESMONTAJE
        dao.reemplazar(destino.name, listOf(elemento(destino, 1), elemento(destino, 2)), DescargaCatalogoEntity(destino, 100))
        dao.reemplazar(causa.name, listOf(elemento(causa, 1)), DescargaCatalogoEntity(causa, 200))

        // El servidor ya no envía el destino 2: deja de existir.
        dao.reemplazar(destino.name, listOf(elemento(destino, 1)), DescargaCatalogoEntity(destino, 300))

        assertEquals(listOf(1), dao.obtener(destino.name).map { it.id })
        assertEquals(listOf(1), dao.obtener(causa.name).map { it.id })
        assertEquals(
            mapOf(destino to 300L, causa to 200L),
            dao.observarDescargas().first().associate { it.tipo to it.descargadoEn }
        )
    }
}
