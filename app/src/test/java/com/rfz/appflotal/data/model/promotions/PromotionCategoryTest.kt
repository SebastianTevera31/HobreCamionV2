package com.rfz.appflotal.data.model.promotions

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Categorías de promociones (Accesorios, Llantas, Amarre de carga) y su caché. */
class PromotionCategoryTest {

    private fun discount(url: String) = StoreDiscount(
        store = "Store",
        title = "Item $url",
        sku = null,
        regularPrice = 100.0,
        finalPrice = 80.0,
        percentOff = 20.0,
        currency = "USD",
        productUrl = url,
        imageUrl = null
    )

    // --- Valores de la API ---

    @Test
    fun `reconoce las tres categorias de la API`() {
        assertEquals(PromotionCategory.ACCESSORIES, PromotionCategory.fromApi("Accesorios"))
        assertEquals(PromotionCategory.TIRES, PromotionCategory.fromApi("Llantas"))
        assertEquals(
            PromotionCategory.CARGO_SECUREMENT,
            PromotionCategory.fromApi("Amarre de carga")
        )
    }

    @Test
    fun `ignora mayusculas, acentos y espacios sobrantes`() {
        assertEquals(PromotionCategory.TIRES, PromotionCategory.fromApi("  LLANTAS "))
        assertEquals(PromotionCategory.ACCESSORIES, PromotionCategory.fromApi("Accesórios"))
        assertEquals(
            PromotionCategory.CARGO_SECUREMENT,
            PromotionCategory.fromApi("amarre   de  CARGA")
        )
    }

    @Test
    fun `categoria desconocida o vacia no se reconoce`() {
        assertNull(PromotionCategory.fromApi("Herramientas"))
        assertNull(PromotionCategory.fromApi(""))
        assertNull(PromotionCategory.fromApi(null))
    }

    @Test
    fun `el orden de las pestanas es accesorios, llantas y amarre de carga`() {
        assertEquals(
            listOf("Accesorios", "Llantas", "Amarre de carga"),
            PromotionCategory.entries.map { it.apiValue }
        )
    }

    @Test
    fun `respuesta sin category se lee como null en lugar de fallar`() {
        val json = """
            {"store":"Store","title":"Item","sku":null,"regularPrice":100.0,"finalPrice":80.0,
             "percentOff":20.0,"currency":"USD","productUrl":"https://store.com/a","imageUrl":null}
        """.trimIndent()

        val parsed = Gson().fromJson(json, StoreDiscount::class.java)

        assertNull(parsed.category)
    }

    // --- Caché ---

    @Test
    fun `cada resultado se guarda con la categoria consultada y su orden dentro de ella`() {
        val entities = categorizedPromotionEntities(
            listOf(
                PromotionCategory.TIRES to listOf(discount("t1"), discount("t2")),
                PromotionCategory.ACCESSORIES to listOf(discount("a1"))
            )
        )

        assertEquals(listOf("a1", "t1", "t2"), entities.map { it.productUrl })
        assertEquals(listOf("Accesorios", "Llantas", "Llantas"), entities.map { it.category })
        assertEquals(listOf(0, 0, 1), entities.map { it.position })
    }

    @Test
    fun `producto repetido en dos categorias se queda en la primera`() {
        val entities = categorizedPromotionEntities(
            listOf(
                PromotionCategory.ACCESSORIES to listOf(discount("x"), discount("a1")),
                PromotionCategory.TIRES to listOf(discount("x"), discount("t1"))
            )
        )

        assertEquals(listOf("x", "a1", "t1"), entities.map { it.productUrl })
        assertEquals("Accesorios", entities.first { it.productUrl == "x" }.category)
        // La posición no deja huecos en la categoría que perdió el repetido.
        assertEquals(0, entities.first { it.productUrl == "t1" }.position)
    }

    @Test
    fun `al leer del cache se conserva la categoria`() {
        val entity = discount("a").toEntity(position = 3, category = PromotionCategory.TIRES)

        assertEquals("Llantas", entity.toDomain().category)
    }
}
