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

    private fun sections(
        topTen: List<TopTenAccessoryResponse>,
        followed: List<FollowedAccessory>,
        topTenLoaded: Boolean = true,
        filter: FollowFilter = FollowFilter.ALL
    ) = buildFollowSections(topTen, topTenLoaded, followed, AccessoryVehicleType.TRUCK, filter)

    // --- "Nuevo" ---

    @Test
    fun isNewPromotion_foundInFirstSync_returnsFalse() {
        assertFalse(isNewPromotion(firstSeenAt = 1_000, accessoryFirstSyncedAt = 1_000, now = 2_000))
    }

    @Test
    fun isNewPromotion_foundAfterFirstSync_isNewOnlyWithinWindow() {
        val firstSeen = 10 * hour

        assertTrue(isNewPromotion(firstSeen, hour, now = firstSeen + NEW_PROMOTION_WINDOW_MILLIS))
        assertFalse(isNewPromotion(firstSeen, hour, now = firstSeen + NEW_PROMOTION_WINDOW_MILLIS + 1))
    }

    @Test
    fun isNewPromotion_noFirstSyncRecorded_returnsFalse() {
        assertFalse(isNewPromotion(firstSeenAt = 10 * hour, accessoryFirstSyncedAt = null, now = 11 * hour))
    }

    // --- Estado de la fila ---

    @Test
    fun followRowStatus_eachFollowState_returnsMatchingStatus() {
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
    fun buildFollowSections_topTenLoaded_showsSuggestionsAndMarksFollowed() {
        val result = sections(
            topTen = listOf(topTen(2, 2), topTen(1, 1), topTen(11, 1, type = "RV")),
            followed = listOf(followed(id = 7, catalogId = 1))
        )

        assertEquals(listOf(1, 2), result.topTen.map { it.position })
        assertEquals(7L, result.topTen[0].followed?.id)
        assertNull(result.topTen[1].followed)
        assertTrue(result.custom.isEmpty())
    }

    @Test
    fun buildFollowSections_followingFilter_hidesUnfollowedSuggestions() {
        val result = sections(
            topTen = listOf(topTen(1, 1), topTen(2, 2)),
            followed = listOf(followed(id = 7, catalogId = 2)),
            filter = FollowFilter.FOLLOWING
        )

        assertEquals(listOf(2), result.topTen.map { it.position })
    }

    @Test
    fun buildFollowSections_customOrOutOfTopTen_goesToCustomSection() {
        val result = sections(
            topTen = listOf(topTen(1, 1)),
            followed = listOf(
                followed(id = 7, name = "Cobija", catalogId = null),
                followed(id = 8, name = "Viejo top", catalogId = 99),
                followed(id = 9, name = "De RV", type = AccessoryVehicleType.RV)
            )
        )

        assertEquals(listOf("Cobija", "Viejo top"), result.custom.map { it.name })
    }

    @Test
    fun buildFollowSections_customNameMatchesSuggestion_marksSuggestionAsFollowed() {
        val result = sections(
            topTen = listOf(topTen(1, 1), topTen(2, 2)),
            followed = listOf(followed(id = 7, name = "top 1", catalogId = null))
        )

        // "top 1" es la misma búsqueda que el sugerido "Top 1": no se repite en "Tus búsquedas".
        assertEquals(7L, result.topTen[0].followed?.id)
        assertNull(result.topTen[1].followed)
        assertTrue(result.custom.isEmpty())
    }

    @Test
    fun buildFollowSections_topTenNotLoaded_keepsCatalogFollowedInTopTen() {
        val result = sections(
            topTen = emptyList(),
            topTenLoaded = false,
            followed = listOf(
                followed(id = 7, name = "Dash cam", catalogId = 1),
                followed(id = 8, name = "Cobija", catalogId = null)
            )
        )

        assertEquals(listOf("Dash cam"), result.topTen.map { it.name })
        assertEquals(listOf("Cobija"), result.custom.map { it.name })
    }

    // --- Notificación ---

    @Test
    fun buildAccessoryNotifications_severalAccessories_groupsWithBestDealSortedByDiscount() {
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
    fun safeProductUrl_nonHttpScheme_returnsNull() {
        assertEquals("https://store.com/x", safeProductUrl(" https://store.com/x "))
        assertEquals("http://store.com/x", safeProductUrl("http://store.com/x"))
        assertNull(safeProductUrl("javascript:alert(1)"))
        assertNull(safeProductUrl("intent://scan/#Intent;scheme=zxing;end"))
        assertNull(safeProductUrl("file:///sdcard/a.html"))
        assertNull(safeProductUrl("no es url"))
        assertNull(safeProductUrl(null))
    }
}
