package com.rfz.appflotal.data.model.promotions

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
    fun fromApi_knownValueIgnoringCaseAccentsAndSpaces_returnsCategory() {
        assertEquals(PromotionCategory.ACCESSORIES, PromotionCategory.fromApi("Accesorios"))
        assertEquals(PromotionCategory.ACCESSORIES, PromotionCategory.fromApi("Accesórios"))
        assertEquals(PromotionCategory.TIRES, PromotionCategory.fromApi("Llantas"))
        assertEquals(PromotionCategory.TIRES, PromotionCategory.fromApi("  LLANTAS "))
        assertEquals(PromotionCategory.CARGO_SECUREMENT, PromotionCategory.fromApi("Amarre de carga"))
        assertEquals(PromotionCategory.CARGO_SECUREMENT, PromotionCategory.fromApi("amarre   de  CARGA"))
    }

    @Test
    fun fromApi_unknownOrEmpty_returnsNull() {
        assertNull(PromotionCategory.fromApi("Herramientas"))
        assertNull(PromotionCategory.fromApi(""))
        assertNull(PromotionCategory.fromApi(null))
    }

    // --- Caché ---

    @Test
    fun categorizedPromotionEntities_severalCategories_storesQueriedCategoryAndPosition() {
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
    fun categorizedPromotionEntities_productInTwoCategories_keepsFirstWithoutPositionGaps() {
        val entities = categorizedPromotionEntities(
            listOf(
                PromotionCategory.ACCESSORIES to listOf(discount("x"), discount("a1")),
                PromotionCategory.TIRES to listOf(discount("x"), discount("t1"))
            )
        )

        assertEquals(listOf("x", "a1", "t1"), entities.map { it.productUrl })
        assertEquals("Accesorios", entities.first { it.productUrl == "x" }.category)
        assertEquals(0, entities.first { it.productUrl == "t1" }.position)
    }
}
