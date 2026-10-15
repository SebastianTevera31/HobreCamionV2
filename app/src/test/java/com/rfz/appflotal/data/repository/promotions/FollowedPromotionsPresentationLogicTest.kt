package com.rfz.appflotal.data.repository.promotions

import com.rfz.appflotal.data.model.catalog.TopTenAccessoryResponse
import com.rfz.appflotal.data.model.promotions.AccessoryVehicleType
import com.rfz.appflotal.data.model.promotions.FollowedAccessory
import com.rfz.appflotal.data.model.promotions.FollowedPromotion
import com.rfz.appflotal.data.model.promotions.NewFollowedPromotion
import com.rfz.appflotal.data.model.promotions.StoreDiscount
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Reglas de la pantalla "Siguiendo", la etiqueta "Nuevo" y la notificación agrupada. */
class FollowedPromotionsPresentationLogicTest {

    private val hour = 60L * 60 * 1000

    private fun discount(url: String, percentOff: Double = 20.0) = StoreDiscount(
        store = "Store",
        title = "Item $url",
        sku = null,
        regularPrice = 100.0,
        finalPrice = 100.0 - percentOff,
        percentOff = percentOff,
        currency = "USD",
        productUrl = "https://store.com/$url",
        imageUrl = null
    )

    private fun followed(
        id: Long,
        name: String = "Accessory $id",
        catalogId: Int? = null,
        type: AccessoryVehicleType = AccessoryVehicleType.TRUCK,
        pendingFirstSync: Boolean = false,
        percents: List<Double> = emptyList()
    ) = FollowedAccessory(
        id = id,
        catalogAccessoryId = catalogId,
        vehicleType = type,
        name = name,
        description = null,
        pendingFirstSync = pendingFirstSync,
        lastSyncedAt = null,
        promotions = percents.mapIndexed { i, p ->
            FollowedPromotion(discount = discount("$id-$i", p), isNew = false)
        }
    )

    private fun topTen(id: Int, position: Int, type: String = "TRUCK") = TopTenAccessoryResponse(
        idAccessory = id,
        vehicleType = type,
        position = position,
        name = "Top $id",
        description = null,
        active = true
    )

    // --- "Nuevo" ---

    @Test
    fun `lo encontrado en la primera busqueda no es nuevo`() {
        assertFalse(isNewPromotion(firstSeenAt = 1_000, accessoryFirstSyncedAt = 1_000, now = 2_000))
    }

    @Test
    fun `lo encontrado despues de la primera busqueda y reciente es nuevo`() {
        assertTrue(isNewPromotion(firstSeenAt = 10 * hour, accessoryFirstSyncedAt = hour, now = 20 * hour))
    }

    @Test
    fun `deja de ser nuevo despues de la ventana`() {
        val firstSeen = 10 * hour
        assertTrue(isNewPromotion(firstSeen, hour, now = firstSeen + NEW_PROMOTION_WINDOW_MILLIS))
        assertFalse(isNewPromotion(firstSeen, hour, now = firstSeen + NEW_PROMOTION_WINDOW_MILLIS + 1))
    }

    @Test
    fun `sin primera busqueda registrada nada es nuevo`() {
        assertFalse(isNewPromotion(firstSeenAt = 10 * hour, accessoryFirstSyncedAt = null, now = 11 * hour))
    }

    // --- Estado de la fila ---

    @Test
    fun `estado de fila segun seguimiento y promociones`() {
        assertEquals(FollowRowStatus.NotFollowed, followRowStatus(null))
        assertEquals(FollowRowStatus.Searching, followRowStatus(followed(1, pendingFirstSync = true)))
        assertEquals(FollowRowStatus.NoDeals, followRowStatus(followed(1)))
        assertEquals(
            FollowRowStatus.Deals(count = 3, bestPercentOff = 35),
            followRowStatus(followed(1, percents = listOf(10.0, 35.4, 20.0)))
        )
    }

    // --- Secciones ---

    @Test
    fun `top 10 cargado muestra todos los sugeridos y marca los seguidos`() {
        val sections = buildFollowSections(
            topTen = listOf(topTen(2, 2), topTen(1, 1), topTen(11, 1, type = "RV")),
            topTenLoaded = true,
            followed = listOf(followed(id = 7, catalogId = 1)),
            vehicleType = AccessoryVehicleType.TRUCK,
            filter = FollowFilter.ALL
        )

        assertEquals(listOf(1, 2), sections.topTen.map { it.position })
        assertEquals(7L, sections.topTen[0].followed?.id)
        assertNull(sections.topTen[1].followed)
        assertTrue(sections.custom.isEmpty())
    }

    @Test
    fun `filtro siguiendo oculta los sugeridos no seguidos`() {
        val sections = buildFollowSections(
            topTen = listOf(topTen(1, 1), topTen(2, 2)),
            topTenLoaded = true,
            followed = listOf(followed(id = 7, catalogId = 2)),
            vehicleType = AccessoryVehicleType.TRUCK,
            filter = FollowFilter.FOLLOWING
        )

        assertEquals(listOf(2), sections.topTen.map { it.position })
    }

    @Test
    fun `busquedas propias y sugeridos que salieron del top 10 van a tus busquedas`() {
        val sections = buildFollowSections(
            topTen = listOf(topTen(1, 1)),
            topTenLoaded = true,
            followed = listOf(
                followed(id = 7, name = "Cobija", catalogId = null),
                followed(id = 8, name = "Viejo top", catalogId = 99),
                followed(id = 9, name = "De RV", type = AccessoryVehicleType.RV)
            ),
            vehicleType = AccessoryVehicleType.TRUCK,
            filter = FollowFilter.ALL
        )

        assertEquals(listOf("Cobija", "Viejo top"), sections.custom.map { it.name })
    }

    @Test
    fun `sin catalogo cargado los seguidos del catalogo siguen en top 10`() {
        val sections = buildFollowSections(
            topTen = emptyList(),
            topTenLoaded = false,
            followed = listOf(
                followed(id = 7, name = "Dash cam", catalogId = 1),
                followed(id = 8, name = "Cobija", catalogId = null)
            ),
            vehicleType = AccessoryVehicleType.TRUCK,
            filter = FollowFilter.ALL
        )

        assertEquals(listOf("Dash cam"), sections.topTen.map { it.name })
        assertEquals(listOf("Cobija"), sections.custom.map { it.name })
    }

    // --- Notificación ---

    @Test
    fun `notificacion agrupa por accesorio con su mejor oferta, ordenado por descuento`() {
        val entries = buildAccessoryNotifications(
            listOf(
                NewFollowedPromotion(1, "Dash cam", discount("a", 10.0)),
                NewFollowedPromotion(1, "Dash cam", discount("b", 35.0)),
                NewFollowedPromotion(2, "TPMS", discount("c", 50.0))
            )
        )

        assertEquals(listOf(2L, 1L), entries.map { it.accessoryId })
        assertEquals(50.0, entries[0].best.percentOff, 0.0)
        assertEquals(35.0, entries[1].best.percentOff, 0.0)
        assertEquals(2, entries[1].newCount)
    }

    // --- URL segura ---

    @Test
    fun `solo se aceptan urls http y https`() {
        assertEquals("https://store.com/x", safeProductUrl(" https://store.com/x "))
        assertEquals("http://store.com/x", safeProductUrl("http://store.com/x"))
        assertNull(safeProductUrl("javascript:alert(1)"))
        assertNull(safeProductUrl("intent://scan/#Intent;scheme=zxing;end"))
        assertNull(safeProductUrl("file:///sdcard/a.html"))
        assertNull(safeProductUrl("no es url"))
        assertNull(safeProductUrl(null))
    }
}
