package com.rfz.appflotal.data.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.rfz.appflotal.data.database.AppHombreCamionDatabase
import com.rfz.appflotal.data.model.promotions.PromotionEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Filtros por categoría de la lista de promociones. */
@RunWith(AndroidJUnit4::class)
class PromotionsDaoTest {

    private lateinit var database: AppHombreCamionDatabase
    private lateinit var dao: PromotionsDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppHombreCamionDatabase::class.java
        ).build()
        dao = database.promotionsDao()
        runBlocking {
            dao.refreshAll(
                listOf(
                    promotion("a1", "Amazon", "Accesorios", position = 0),
                    promotion("a2", "Walmart", "Accesorios", position = 1),
                    promotion("t1", "Llantera", "Llantas", position = 0),
                    promotion("c1", "Amazon", "Amarre de carga", position = 0)
                )
            )
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun promotion(url: String, store: String, category: String, position: Int) =
        PromotionEntity(
            productUrl = url,
            position = position,
            store = store,
            title = "Item $url",
            sku = null,
            regularPrice = 100.0,
            finalPrice = 80.0,
            percentOff = 20.0,
            currency = "USD",
            imageUrl = null,
            category = category
        )

    @Test
    fun paginaYConteoSoloDeLaCategoria() = runBlocking {
        val page = dao.getPage(search = "", store = "", category = "Accesorios", limit = 10, offset = 0)

        assertEquals(listOf("a1", "a2"), page.map { it.productUrl })
        assertEquals(2, dao.count(search = "", store = "", category = "Accesorios"))
        assertEquals(1, dao.count(search = "", store = "", category = "Llantas"))
    }

    @Test
    fun sinCategoriaIncluyeTodas() = runBlocking {
        assertEquals(4, dao.count(search = "", store = "", category = ""))
    }

    @Test
    fun categoriaSeCombinaConTiendaYBusqueda() = runBlocking {
        assertEquals(
            listOf("c1"),
            dao.getPage(search = "", store = "Amazon", category = "Amarre de carga", limit = 10, offset = 0)
                .map { it.productUrl }
        )
        assertEquals(1, dao.count(search = "a2", store = "", category = "Accesorios"))
        assertEquals(0, dao.count(search = "a2", store = "", category = "Llantas"))
    }

    @Test
    fun tiendasSoloDeLaCategoria() = runBlocking {
        assertEquals(listOf("Amazon", "Walmart"), dao.getStores(category = "Accesorios"))
        assertEquals(listOf("Llantera"), dao.getStores(category = "Llantas"))
        assertEquals(listOf("Amazon", "Llantera", "Walmart"), dao.getStores(category = ""))
    }
}
