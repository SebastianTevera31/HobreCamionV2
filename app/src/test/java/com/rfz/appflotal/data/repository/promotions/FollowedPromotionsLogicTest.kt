package com.rfz.appflotal.data.repository.promotions

import com.rfz.appflotal.data.model.catalog.TopTenAccessoryResponse
import com.rfz.appflotal.data.model.promotions.AccessoryVehicleType
import com.rfz.appflotal.data.model.promotions.FollowedAccessoryEntity
import com.rfz.appflotal.data.model.promotions.StoreDiscount
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Reglas de "Siguiendo": validación, sugeridos, alcance de la sincronización y qué se notifica. */
class FollowedPromotionsLogicTest {

    private fun followed(
        name: String,
        type: AccessoryVehicleType = AccessoryVehicleType.TRUCK,
        initialSyncDone: Boolean = true,
        id: Long = 1
    ) = FollowedAccessoryEntity(
        id = id,
        catalogAccessoryId = null,
        vehicleType = type.name,
        name = cleanAccessoryName(name),
        normalizedName = normalizeAccessoryName(name),
        description = null,
        createdAt = 0,
        initialSyncDone = initialSyncDone
    )

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

    private fun accessory(
        id: Int,
        type: String?,
        position: Int,
        name: String? = "Accessory $id",
        active: Boolean = true
    ) = TopTenAccessoryResponse(
        idAccessory = id,
        vehicleType = type,
        position = position,
        name = name,
        description = null,
        active = active
    )

    private fun validate(
        name: String,
        type: AccessoryVehicleType = AccessoryVehicleType.TRUCK,
        current: List<FollowedAccessoryEntity> = emptyList()
    ) = validateNewFollow(name, type, current)

    // --- Validación ---

    @Test
    fun validateNewFollow_belowMinLengthAfterTrim_returnsTooShort() {
        assertEquals(FollowValidation.TOO_SHORT, validate("  ab   "))
    }

    @Test
    fun validateNewFollow_exactlyMinOrMaxLength_returnsValid() {
        assertEquals(FollowValidation.VALID, validate("a".repeat(MIN_ACCESSORY_NAME_LENGTH)))
        assertEquals(FollowValidation.VALID, validate("a".repeat(MAX_ACCESSORY_NAME_LENGTH)))
    }

    @Test
    fun validateNewFollow_aboveMaxLength_returnsTooLong() {
        assertEquals(FollowValidation.TOO_LONG, validate("a".repeat(MAX_ACCESSORY_NAME_LENGTH + 1)))
    }

    @Test
    fun validateNewFollow_sameNameIgnoringCaseAndSpaces_returnsDuplicate() {
        assertEquals(
            FollowValidation.DUPLICATE,
            validate(" DASH  cam", current = listOf(followed("Dash cam")))
        )
    }

    @Test
    fun validateNewFollow_sameNameOtherVehicleType_returnsValid() {
        assertEquals(
            FollowValidation.VALID,
            validate(
                "Dash cam",
                type = AccessoryVehicleType.RV,
                current = listOf(followed("Dash cam", AccessoryVehicleType.TRUCK))
            )
        )
    }

    @Test
    fun validateNewFollow_atLimit_returnsLimitReached() {
        val full = (1..MAX_FOLLOWED_ACCESSORIES).map { followed("Accessory $it", id = it.toLong()) }

        assertEquals(FollowValidation.LIMIT_REACHED, validate("Otro accesorio", current = full))
    }

    @Test
    fun validateNewFollow_oneBelowLimit_returnsValid() {
        val almostFull = (1 until MAX_FOLLOWED_ACCESSORIES).map {
            followed("Accessory $it", id = it.toLong())
        }

        assertEquals(FollowValidation.VALID, validate("Otro accesorio", current = almostFull))
    }

    // --- Sugeridos del Top 10 y tipo de vehículo ---

    @Test
    fun suggestedAccessoriesFor_mixedCatalog_returnsActiveNamedOfTypeByPosition() {
        val catalog = listOf(
            accessory(id = 3, type = "TRUCK", position = 3),
            accessory(id = 1, type = "TRUCK", position = 1),
            accessory(id = 2, type = "TRUCK", position = 2, active = false),
            accessory(id = 4, type = "TRUCK", position = 4, name = " "),
            accessory(id = 11, type = "RV", position = 1),
            accessory(id = 12, type = null, position = 2)
        )

        assertEquals(
            listOf(1, 3),
            suggestedAccessoriesFor(catalog, AccessoryVehicleType.TRUCK).map { it.idAccessory }
        )
        assertEquals(
            listOf(11),
            suggestedAccessoriesFor(catalog, AccessoryVehicleType.RV).map { it.idAccessory }
        )
    }

    @Test
    fun accessoryVehicleTypeFromApi_caseAndSpaces_returnsTypeOrNull() {
        assertEquals(AccessoryVehicleType.TRUCK, AccessoryVehicleType.fromApi(" truck "))
        assertEquals(AccessoryVehicleType.RV, AccessoryVehicleType.fromApi("rv"))
        assertNull(AccessoryVehicleType.fromApi("BUS"))
        assertNull(AccessoryVehicleType.fromApi(null))
    }

    @Test
    fun vehicleTypeForUser_rvOrMotorhome_returnsRv() {
        assertEquals(AccessoryVehicleType.RV, vehicleTypeForUser("RV"))
        assertEquals(AccessoryVehicleType.RV, vehicleTypeForUser("Motorhome clase A"))
        assertEquals(AccessoryVehicleType.RV, vehicleTypeForUser("Casa rodante"))
    }

    @Test
    fun vehicleTypeForUser_anyOtherOrMissing_returnsTruck() {
        assertEquals(AccessoryVehicleType.TRUCK, vehicleTypeForUser("Tractocamión"))
        assertEquals(AccessoryVehicleType.TRUCK, vehicleTypeForUser("DRIVER"))
        assertEquals(AccessoryVehicleType.TRUCK, vehicleTypeForUser(""))
        assertEquals(AccessoryVehicleType.TRUCK, vehicleTypeForUser(null))
    }

    // --- Sincronización ---

    @Test
    fun accessoriesToSync_eachScope_returnsMatchingAccessories() {
        val synced = followed("Dash cam", id = 1)
        val pending = followed("TPMS", id = 2, initialSyncDone = false)
        val all = listOf(synced, pending)

        assertEquals(listOf(1L, 2L), accessoriesToSync(all, FollowedSyncScope.All).map { it.id })
        assertEquals(
            listOf(2L),
            accessoriesToSync(all, FollowedSyncScope.PendingFirstSync).map { it.id }
        )
        assertEquals(listOf(1L), accessoriesToSync(all, FollowedSyncScope.Single(1)).map { it.id })
        assertTrue(accessoriesToSync(all, FollowedSyncScope.Single(99)).isEmpty())
    }

    @Test
    fun newPromotionsToNotify_firstSync_returnsEmpty() {
        val result = newPromotionsToNotify(
            accessory = followed("Dash cam", initialSyncDone = false),
            fetched = listOf(discount("a"), discount("b")),
            knownProductUrls = emptySet()
        )

        assertTrue(result.isEmpty())
    }

    @Test
    fun newPromotionsToNotify_knownAndRepeatedUrls_returnsUnknownOnce() {
        val result = newPromotionsToNotify(
            accessory = followed("Dash cam"),
            fetched = listOf(discount("a"), discount("b"), discount("b"), discount("c")),
            knownProductUrls = setOf("a")
        )

        assertEquals(listOf("b", "c"), result.map { it.discount.productUrl })
        assertTrue(result.all { it.accessoryName == "Dash cam" })
    }

    /**
     * Un producto que salió de los resultados y volvió llega como conocido (tabla de vistos):
     * conserva su primera fecha, no vuelve a marcarse "Nuevo" ni se notifica otra vez.
     */
    @Test
    fun planAccessorySync_seenAndNewResults_keepsFirstSeenAndNotifiesOnlyNew() {
        val plan = planAccessorySync(
            accessory = followed("Dash cam", id = 4),
            fetched = listOf(discount("a"), discount("b"), discount("a")),
            firstSeenByUrl = mapOf("a" to 100L, "z" to 50L),
            knownProductUrls = setOf("a", "z"),
            now = 900
        )

        assertEquals(listOf("a", "b"), plan.promotions.map { it.productUrl })
        assertEquals(listOf(0, 1), plan.promotions.map { it.position })
        assertEquals(listOf(100L, 900L), plan.promotions.map { it.firstSeenAt })
        assertEquals(listOf(900L, 900L), plan.seen.map { it.lastSeenAt })
        assertTrue(plan.seen.all { it.followedAccessoryId == 4L })
        assertEquals(listOf("b"), plan.toNotify.map { it.discount.productUrl })
    }
}
