package com.rfz.appflotal.data.repository.promotions

import com.rfz.appflotal.data.model.catalog.TopTenAccessoryResponse
import com.rfz.appflotal.data.model.promotions.AccessoryVehicleType
import com.rfz.appflotal.data.model.promotions.FollowedAccessoryEntity
import com.rfz.appflotal.data.model.promotions.NewFollowedPromotion
import com.rfz.appflotal.data.model.promotions.StoreDiscount
import java.util.Locale

/*
 * Reglas de "Siguiendo" sin dependencias de Android, para poder probarlas con JUnit.
 */

/** Cada accesorio seguido es una llamada a GetDiscounts por sincronización; limita la carga al backend. */
const val MAX_FOLLOWED_ACCESSORIES = 15
const val MIN_ACCESSORY_NAME_LENGTH = 3
const val MAX_ACCESSORY_NAME_LENGTH = 50
const val RESULTS_PER_FOLLOWED_ACCESSORY = 5

enum class FollowValidation {
    VALID,
    TOO_SHORT,
    TOO_LONG,
    DUPLICATE,
    LIMIT_REACHED
}

private val WHITESPACE = Regex("\\s+")

/** Nombre a mostrar y a buscar: sin espacios sobrantes. */
fun cleanAccessoryName(raw: String): String = raw.trim().replace(WHITESPACE, " ")

/** Clave para detectar duplicados sin importar mayúsculas ni espacios. */
fun normalizeAccessoryName(raw: String): String = cleanAccessoryName(raw).lowercase(Locale.ROOT)

fun validateNewFollow(
    rawName: String,
    vehicleType: AccessoryVehicleType,
    followed: List<FollowedAccessoryEntity>
): FollowValidation {
    val name = cleanAccessoryName(rawName)
    val normalized = normalizeAccessoryName(rawName)
    return when {
        name.length < MIN_ACCESSORY_NAME_LENGTH -> FollowValidation.TOO_SHORT
        name.length > MAX_ACCESSORY_NAME_LENGTH -> FollowValidation.TOO_LONG
        followed.any { it.normalizedName == normalized && it.vehicleType == vehicleType.name } ->
            FollowValidation.DUPLICATE

        followed.size >= MAX_FOLLOWED_ACCESSORIES -> FollowValidation.LIMIT_REACHED
        else -> FollowValidation.VALID
    }
}

/** Sugeridos activos de un tipo de vehículo, en el orden del Top 10. */
fun suggestedAccessoriesFor(
    accessories: List<TopTenAccessoryResponse>,
    vehicleType: AccessoryVehicleType
): List<TopTenAccessoryResponse> = accessories
    .filter { it.active && !it.name.isNullOrBlank() }
    .filter { AccessoryVehicleType.fromApi(it.vehicleType) == vehicleType }
    .sortedBy { it.position }

/**
 * Pestaña inicial según el tipo de vehículo del usuario. El catálogo de tipos de vehículo
 * no usa los mismos valores que el Top 10, así que solo se elige RV cuando el texto lo
 * indica claramente; en cualquier otro caso, TRUCK.
 */
fun vehicleTypeForUser(userVehicleType: String?): AccessoryVehicleType {
    val value = userVehicleType?.uppercase(Locale.ROOT).orEmpty()
    val isRv = value.split(Regex("[^A-Z]+")).any { it == "RV" } ||
        listOf("MOTORHOME", "MOTOR HOME", "CASA RODANTE", "AUTOCARAVANA", "RECREATIONAL")
            .any { value.contains(it) }
    return if (isRv) AccessoryVehicleType.RV else AccessoryVehicleType.TRUCK
}

/**
 * Promociones a notificar para un accesorio tras sincronizarlo: las que no estaban en
 * caché ([knownProductUrls]). En su primera sincronización no se notifica nada, solo se
 * guarda lo que ya existía.
 */
fun newPromotionsToNotify(
    accessory: FollowedAccessoryEntity,
    fetched: List<StoreDiscount>,
    knownProductUrls: Set<String>
): List<NewFollowedPromotion> {
    if (!accessory.initialSyncDone) return emptyList()
    return fetched
        .filter { it.productUrl !in knownProductUrls }
        .distinctBy { it.productUrl }
        .map { NewFollowedPromotion(accessoryName = accessory.name, discount = it) }
}
