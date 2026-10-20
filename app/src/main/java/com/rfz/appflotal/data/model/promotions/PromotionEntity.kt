package com.rfz.appflotal.data.model.promotions

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "promotion_table")
data class PromotionEntity(
    @PrimaryKey
    val productUrl: String,
    // Orden dentro de su categoría, tal como lo regresó GetDiscounts.
    val position: Int,
    val store: String,
    val title: String,
    val sku: String?,
    val regularPrice: Double,
    val finalPrice: Double,
    val percentOff: Double,
    val currency: String,
    val imageUrl: String?,
    // PromotionCategory.apiValue de la consulta que la trajo.
    val category: String?
)

fun StoreDiscount.toEntity(position: Int, category: PromotionCategory): PromotionEntity =
    PromotionEntity(
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
        category = category.apiValue
    )

/**
 * Une los resultados de cada categoría en la caché. La categoría es la de la consulta (el
 * filtro del backend), no la del campo de cada resultado. Si un producto llega en dos
 * categorías, se queda en la primera, en el orden de [PromotionCategory].
 */
fun categorizedPromotionEntities(
    resultsByCategory: List<Pair<PromotionCategory, List<StoreDiscount>>>
): List<PromotionEntity> {
    val seenUrls = mutableSetOf<String>()
    return resultsByCategory
        .sortedBy { (category, _) -> category.ordinal }
        .flatMap { (category, discounts) ->
            discounts
                .filter { seenUrls.add(it.productUrl) }
                .mapIndexed { index, discount -> discount.toEntity(index, category) }
        }
}

fun PromotionEntity.toDomain(): StoreDiscount = StoreDiscount(
    store = store,
    title = title,
    sku = sku,
    regularPrice = regularPrice,
    finalPrice = finalPrice,
    percentOff = percentOff,
    currency = currency,
    productUrl = productUrl,
    imageUrl = imageUrl,
    category = category
)
