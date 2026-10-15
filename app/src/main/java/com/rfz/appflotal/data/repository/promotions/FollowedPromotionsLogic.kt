package com.rfz.appflotal.data.repository.promotions

import com.rfz.appflotal.data.model.catalog.TopTenAccessoryResponse
import com.rfz.appflotal.data.model.promotions.AccessoryVehicleType
import com.rfz.appflotal.data.model.promotions.FollowedAccessory
import com.rfz.appflotal.data.model.promotions.FollowedAccessoryEntity
import com.rfz.appflotal.data.model.promotions.NewFollowedPromotion
import com.rfz.appflotal.data.model.promotions.StoreDiscount
import java.net.URI
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
        .map {
            NewFollowedPromotion(
                accessoryId = accessory.id,
                accessoryName = accessory.name,
                discount = it
            )
        }
}

/** Una promoción se marca "Nuevo" durante este tiempo desde que se vio por primera vez. */
const val NEW_PROMOTION_WINDOW_MILLIS = 48L * 60 * 60 * 1000

/**
 * "Nuevo": apareció después de la primera búsqueda del accesorio (lo que ya existía al
 * seguirlo no es novedad) y hace menos de [NEW_PROMOTION_WINDOW_MILLIS].
 */
fun isNewPromotion(firstSeenAt: Long, accessoryFirstSyncedAt: Long?, now: Long): Boolean =
    accessoryFirstSyncedAt != null &&
        firstSeenAt > accessoryFirstSyncedAt &&
        now - firstSeenAt in 0..NEW_PROMOTION_WINDOW_MILLIS

// --- Pantalla principal de "Siguiendo" ---

enum class FollowFilter { ALL, FOLLOWING }

/** Estado que se muestra bajo el nombre de cada fila. */
sealed interface FollowRowStatus {
    data object NotFollowed : FollowRowStatus
    data object Searching : FollowRowStatus
    data object NoDeals : FollowRowStatus
    data class Deals(val count: Int, val bestPercentOff: Int) : FollowRowStatus
}

fun followRowStatus(followed: FollowedAccessory?): FollowRowStatus = when {
    followed == null -> FollowRowStatus.NotFollowed
    followed.promotions.isNotEmpty() -> FollowRowStatus.Deals(
        count = followed.promotions.size,
        bestPercentOff = followed.bestPercentOff?.toInt() ?: 0
    )

    followed.pendingFirstSync -> FollowRowStatus.Searching
    else -> FollowRowStatus.NoDeals
}

/**
 * Fila de la pantalla principal. [catalogAccessory] permite seguir un sugerido;
 * [followed] es null si aún no se sigue.
 */
data class FollowRow(
    val key: String,
    val position: Int?,
    val name: String,
    val catalogAccessory: TopTenAccessoryResponse?,
    val followed: FollowedAccessory?
) {
    val status: FollowRowStatus get() = followRowStatus(followed)
}

data class FollowSections(
    val topTen: List<FollowRow>,
    val custom: List<FollowRow>
)

/**
 * Arma las dos secciones de una pestaña.
 * - Top 10: los sugeridos, seguidos o no. Si el catálogo no cargó, solo los ya seguidos.
 * - Tus búsquedas: las escritas por el usuario y las del catálogo que salieron del Top 10.
 * Con [FollowFilter.FOLLOWING] se ocultan los sugeridos que no se siguen.
 */
fun buildFollowSections(
    topTen: List<TopTenAccessoryResponse>,
    topTenLoaded: Boolean,
    followed: List<FollowedAccessory>,
    vehicleType: AccessoryVehicleType,
    filter: FollowFilter
): FollowSections {
    val followedInTab = followed.filter { it.vehicleType == vehicleType }
    val followedByCatalogId = followedInTab
        .filter { it.catalogAccessoryId != null }
        .associateBy { it.catalogAccessoryId }

    val topTenRows: List<FollowRow>
    val inTopTenIds: Set<Int>
    if (topTenLoaded) {
        val suggestions = suggestedAccessoriesFor(topTen, vehicleType)
        inTopTenIds = suggestions.map { it.idAccessory }.toSet()
        topTenRows = suggestions.map { accessory ->
            FollowRow(
                key = "top-${accessory.idAccessory}",
                position = accessory.position,
                name = accessory.name.orEmpty(),
                catalogAccessory = accessory,
                followed = followedByCatalogId[accessory.idAccessory]
            )
        }
    } else {
        inTopTenIds = followedByCatalogId.keys.filterNotNull().toSet()
        topTenRows = followedByCatalogId.values.map { it.toRow() }
    }

    val customRows = followedInTab
        .filter { it.catalogAccessoryId == null || it.catalogAccessoryId !in inTopTenIds }
        .map { it.toRow() }

    return FollowSections(
        topTen = when (filter) {
            FollowFilter.ALL -> topTenRows
            FollowFilter.FOLLOWING -> topTenRows.filter { it.followed != null }
        },
        custom = customRows
    )
}

private fun FollowedAccessory.toRow() = FollowRow(
    key = "followed-$id",
    position = null,
    name = name,
    catalogAccessory = null,
    followed = this
)

// --- Notificación ---

/** Accesorios que caben en una notificación agrupada; el resto se resume como "+N más". */
const val MAX_NOTIFIED_ACCESSORIES = 3

data class AccessoryNotification(
    val accessoryId: Long,
    val accessoryName: String,
    val best: StoreDiscount,
    val newCount: Int
)

/** Una entrada por accesorio con su mejor oferta nueva, ordenadas por mayor descuento. */
fun buildAccessoryNotifications(promotions: List<NewFollowedPromotion>): List<AccessoryNotification> =
    promotions
        .groupBy { it.accessoryId }
        .map { (accessoryId, items) ->
            AccessoryNotification(
                accessoryId = accessoryId,
                accessoryName = items.first().accessoryName,
                best = items.maxBy { it.discount.percentOff }.discount,
                newCount = items.size
            )
        }
        .sortedByDescending { it.best.percentOff }

/** La URL del producto viene del backend: solo se abre si es http(s). */
fun safeProductUrl(url: String?): String? {
    val value = url?.trim().orEmpty()
    val scheme = runCatching { URI(value).scheme }.getOrNull()?.lowercase(Locale.ROOT)
    return value.takeIf { scheme == "https" || scheme == "http" }
}
