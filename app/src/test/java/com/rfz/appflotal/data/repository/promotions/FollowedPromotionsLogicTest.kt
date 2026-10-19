package com.rfz.appflotal.data.repository.promotions

import com.rfz.appflotal.data.model.catalog.TopTenAccessoryResponse
import com.rfz.appflotal.data.model.promotions.AccessoryVehicleType
import com.rfz.appflotal.data.model.promotions.FollowedAccessoryEntity
import com.rfz.appflotal.data.model.promotions.StoreDiscount
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

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

    // --- Normalización ---

    @Test
    fun `cleanAccessoryName quita espacios sobrantes`() {
        assertEquals("Dash cam", cleanAccessoryName("  Dash    cam  "))
    }

    @Test
    fun `normalizeAccessoryName ignora mayusculas y espacios`() {
        assertEquals(normalizeAccessoryName("dash cam"), normalizeAccessoryName("  DASH   Cam "))
    }

    // --- Validación ---

    @Test
    fun `nombre valido se acepta`() {
        assertEquals(
            FollowValidation.VALID,
            validateNewFollow("Dash cam", AccessoryVehicleType.TRUCK, emptyList())
        )
    }

    @Test
    fun `nombre menor al minimo se rechaza aunque tenga espacios`() {
        assertEquals(
            FollowValidation.TOO_SHORT,
            validateNewFollow("  ab   ", AccessoryVehicleType.TRUCK, emptyList())
        )
    }

    @Test
    fun `nombre en el minimo y en el maximo se acepta`() {
        val min = "a".repeat(MIN_ACCESSORY_NAME_LENGTH)
        val max = "a".repeat(MAX_ACCESSORY_NAME_LENGTH)
        assertEquals(FollowValidation.VALID, validateNewFollow(min, AccessoryVehicleType.TRUCK, emptyList()))
        assertEquals(FollowValidation.VALID, validateNewFollow(max, AccessoryVehicleType.TRUCK, emptyList()))
    }

    @Test
    fun `nombre mayor al maximo se rechaza`() {
        assertEquals(
            FollowValidation.TOO_LONG,
            validateNewFollow(
                "a".repeat(MAX_ACCESSORY_NAME_LENGTH + 1),
                AccessoryVehicleType.TRUCK,
                emptyList()
            )
        )
    }

    @Test
    fun `duplicado sin importar mayusculas en el mismo tipo se rechaza`() {
        assertEquals(
            FollowValidation.DUPLICATE,
            validateNewFollow(" DASH  cam", AccessoryVehicleType.TRUCK, listOf(followed("Dash cam")))
        )
    }

    @Test
    fun `mismo nombre en otro tipo de vehiculo se acepta`() {
        assertEquals(
            FollowValidation.VALID,
            validateNewFollow(
                "Dash cam",
                AccessoryVehicleType.RV,
                listOf(followed("Dash cam", AccessoryVehicleType.TRUCK))
            )
        )
    }

    @Test
    fun `al llegar al tope se rechaza`() {
        val full = (1..MAX_FOLLOWED_ACCESSORIES).map { followed("Accessory $it", id = it.toLong()) }
        assertEquals(
            FollowValidation.LIMIT_REACHED,
            validateNewFollow("Otro accesorio", AccessoryVehicleType.TRUCK, full)
        )
    }

    @Test
    fun `un lugar antes del tope se acepta`() {
        val almostFull = (1 until MAX_FOLLOWED_ACCESSORIES).map {
            followed("Accessory $it", id = it.toLong())
        }
        assertEquals(
            FollowValidation.VALID,
            validateNewFollow("Otro accesorio", AccessoryVehicleType.TRUCK, almostFull)
        )
    }

    // --- Sugeridos del Top 10 ---

    @Test
    fun `sugeridos filtra por tipo, activos y nombre, ordenados por posicion`() {
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
    fun `fromApi reconoce TRUCK y RV sin importar mayusculas ni espacios`() {
        assertEquals(AccessoryVehicleType.TRUCK, AccessoryVehicleType.fromApi(" truck "))
        assertEquals(AccessoryVehicleType.RV, AccessoryVehicleType.fromApi("rv"))
        assertNull(AccessoryVehicleType.fromApi("BUS"))
        assertNull(AccessoryVehicleType.fromApi(null))
    }

    // --- Tipo de vehículo del usuario ---

    @Test
    fun `tipo de usuario RV o casa rodante selecciona RV`() {
        assertEquals(AccessoryVehicleType.RV, vehicleTypeForUser("RV"))
        assertEquals(AccessoryVehicleType.RV, vehicleTypeForUser("Motorhome clase A"))
        assertEquals(AccessoryVehicleType.RV, vehicleTypeForUser("Casa rodante"))
    }

    @Test
    fun `cualquier otro tipo de usuario selecciona TRUCK`() {
        assertEquals(AccessoryVehicleType.TRUCK, vehicleTypeForUser("Tractocamión"))
        assertEquals(AccessoryVehicleType.TRUCK, vehicleTypeForUser("DRIVER"))
        assertEquals(AccessoryVehicleType.TRUCK, vehicleTypeForUser(""))
        assertEquals(AccessoryVehicleType.TRUCK, vehicleTypeForUser(null))
    }

    // --- Promociones a notificar ---

    @Test
    fun `primera sincronizacion no notifica`() {
        val result = newPromotionsToNotify(
            accessory = followed("Dash cam", initialSyncDone = false),
            fetched = listOf(discount("a"), discount("b")),
            knownProductUrls = emptySet()
        )
        assertTrue(result.isEmpty())
    }

    @Test
    fun `solo se notifican promociones no conocidas, sin repetir`() {
        val result = newPromotionsToNotify(
            accessory = followed("Dash cam"),
            fetched = listOf(discount("a"), discount("b"), discount("b"), discount("c")),
            knownProductUrls = setOf("a")
        )
        assertEquals(listOf("b", "c"), result.map { it.discount.productUrl })
        assertTrue(result.all { it.accessoryName == "Dash cam" })
    }

    @Test
    fun `sin promociones nuevas no se notifica`() {
        val result = newPromotionsToNotify(
            accessory = followed("Dash cam"),
            fetched = listOf(discount("a")),
            knownProductUrls = setOf("a")
        )
        assertTrue(result.isEmpty())
    }

    // --- Plan de sincronización ---

    /**
     * Simula varias búsquedas diarias del mismo accesorio, guardando lo visto como lo hace
     * el repositorio (tabla de vistos). Devuelve lo notificado en cada búsqueda.
     */
    private class SeenSimulation(private val accessoryName: String) {
        val firstSeen = mutableMapOf<String, Long>()
        private var synced = false

        fun search(urls: List<String>, now: Long): AccessorySyncPlan {
            val accessory = FollowedAccessoryEntity(
                id = 1,
                catalogAccessoryId = null,
                vehicleType = AccessoryVehicleType.TRUCK.name,
                name = accessoryName,
                normalizedName = normalizeAccessoryName(accessoryName),
                description = null,
                createdAt = 0,
                initialSyncDone = synced
            )
            val plan = planAccessorySync(
                accessory = accessory,
                fetched = urls.map {
                    StoreDiscount("Store", "Item $it", null, 100.0, 80.0, 20.0, "USD", it, null)
                },
                firstSeenByUrl = firstSeen.toMap(),
                knownProductUrls = firstSeen.keys.toSet(),
                now = now
            )
            plan.seen.forEach { firstSeen[it.productUrl] = it.firstSeenAt }
            synced = true
            return plan
        }
    }

    @Test
    fun `producto que sale y vuelve a los resultados no se notifica otra vez`() {
        val simulation = SeenSimulation("Dash cam")

        val day1 = simulation.search(listOf("a", "b", "c", "d", "e"), now = 1_000)
        val day2 = simulation.search(listOf("a", "b", "c", "d", "f"), now = 2_000)
        val day3 = simulation.search(listOf("a", "b", "c", "d", "e"), now = 3_000)

        assertTrue(day1.toNotify.isEmpty())
        assertEquals(listOf("f"), day2.toNotify.map { it.discount.productUrl })
        assertTrue(day3.toNotify.isEmpty())
        // Conserva la fecha de la primera vez, así que tampoco vuelve a marcarse "Nuevo".
        assertEquals(1_000L, day3.promotions.first { it.productUrl == "e" }.firstSeenAt)
    }

    @Test
    fun `plan guarda solo los resultados actuales y registra cuando se vieron`() {
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

    @Test
    fun `alcance de la sincronizacion elige los accesorios a consultar`() {
        val synced = followed("Dash cam", id = 1)
        val pending = followed("TPMS", id = 2, initialSyncDone = false)
        val all = listOf(synced, pending)

        assertEquals(listOf(1L, 2L), accessoriesToSync(all, FollowedSyncScope.All).map { it.id })
        assertEquals(
            listOf(2L),
            accessoriesToSync(all, FollowedSyncScope.PendingFirstSync).map { it.id }
        )
        assertEquals(
            listOf(1L),
            accessoriesToSync(all, FollowedSyncScope.Single(1)).map { it.id }
        )
        assertTrue(accessoriesToSync(all, FollowedSyncScope.Single(99)).isEmpty())
    }
}
