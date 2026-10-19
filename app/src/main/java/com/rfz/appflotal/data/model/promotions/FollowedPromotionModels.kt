package com.rfz.appflotal.data.model.promotions

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class AccessoryVehicleType {
    TRUCK,
    RV;

    companion object {
        /** Valor de `fld_vehicleType` del catálogo Top 10 ("TRUCK" / "RV"); null si no se reconoce. */
        fun fromApi(value: String?): AccessoryVehicleType? =
            entries.firstOrNull { it.name.equals(value?.trim(), ignoreCase = true) }
    }
}

/**
 * Accesorio que el usuario sigue: uno sugerido del Top 10 (con [catalogAccessoryId])
 * o uno escrito por él (catalogAccessoryId = null). [name] es el término que se busca
 * en GetDiscounts; se guarda para que el seguimiento sobreviva aunque el accesorio
 * salga del Top 10.
 */
@Entity(
    tableName = "followed_accessory_table",
    indices = [Index(value = ["normalizedName", "vehicleType"], unique = true)]
)
data class FollowedAccessoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val catalogAccessoryId: Int?,
    val vehicleType: String,
    val name: String,
    val normalizedName: String,
    val description: String?,
    val createdAt: Long,
    // false hasta la primera sincronización: lo encontrado en ella no se notifica.
    val initialSyncDone: Boolean = false,
    // Fecha de la primera búsqueda exitosa: lo visto después de ella cuenta como "Nuevo".
    val firstSyncedAt: Long? = null,
    // Fecha de la última búsqueda exitosa ("Actualizado hace X h").
    val lastSyncedAt: Long? = null
)

@Entity(
    tableName = "followed_promotion_table",
    primaryKeys = ["followedAccessoryId", "productUrl"],
    indices = [Index(value = ["followedAccessoryId"])]
)
data class FollowedPromotionEntity(
    val followedAccessoryId: Long,
    val productUrl: String,
    val position: Int,
    val store: String,
    val title: String,
    val sku: String?,
    val regularPrice: Double,
    val finalPrice: Double,
    val percentOff: Double,
    val currency: String,
    val imageUrl: String?,
    val firstSeenAt: Long
)

/**
 * Producto que ya apareció en las búsquedas de un accesorio. A diferencia de
 * [FollowedPromotionEntity] (solo los resultados de la última búsqueda), se conserva
 * aunque el producto salga de los resultados, para no volver a notificarlo ni marcarlo
 * "Nuevo" si regresa.
 */
@Entity(
    tableName = "followed_seen_product_table",
    primaryKeys = ["followedAccessoryId", "productUrl"]
)
data class FollowedSeenProductEntity(
    val followedAccessoryId: Long,
    val productUrl: String,
    val firstSeenAt: Long,
    val lastSeenAt: Long
)

data class FollowedAccessory(
    val id: Long,
    val catalogAccessoryId: Int?,
    val vehicleType: AccessoryVehicleType,
    val name: String,
    val description: String?,
    val pendingFirstSync: Boolean,
    val lastSyncedAt: Long?,
    // Ordenadas por mayor descuento.
    val promotions: List<FollowedPromotion>
) {
    val bestPercentOff: Double? get() = promotions.maxOfOrNull { it.discount.percentOff }
}

data class FollowedPromotion(
    val discount: StoreDiscount,
    val isNew: Boolean
)

/** Promoción encontrada por primera vez para un accesorio seguido; es lo que se notifica. */
data class NewFollowedPromotion(
    val accessoryId: Long,
    val accessoryName: String,
    val discount: StoreDiscount
)

fun StoreDiscount.toFollowedEntity(
    followedAccessoryId: Long,
    position: Int,
    firstSeenAt: Long
): FollowedPromotionEntity = FollowedPromotionEntity(
    followedAccessoryId = followedAccessoryId,
    productUrl = productUrl,
    position = position,
    store = store,
    title = title,
    sku = sku,
    regularPrice = regularPrice,
    finalPrice = finalPrice,
    percentOff = percentOff,
    currency = currency,
    imageUrl = imageUrl,
    firstSeenAt = firstSeenAt
)

fun FollowedPromotionEntity.toDomain(): StoreDiscount = StoreDiscount(
    store = store,
    title = title,
    sku = sku,
    regularPrice = regularPrice,
    finalPrice = finalPrice,
    percentOff = percentOff,
    currency = currency,
    productUrl = productUrl,
    imageUrl = imageUrl
)

fun FollowedAccessoryEntity.toDomain(promotions: List<FollowedPromotion>): FollowedAccessory? {
    val type = AccessoryVehicleType.fromApi(vehicleType) ?: return null
    return FollowedAccessory(
        id = id,
        catalogAccessoryId = catalogAccessoryId,
        vehicleType = type,
        name = name,
        description = description,
        pendingFirstSync = !initialSyncDone,
        lastSyncedAt = lastSyncedAt,
        promotions = promotions
    )
}
