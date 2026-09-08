package com.rfz.appflotal.data.model.promotions

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "promotion_table")
data class PromotionEntity(
    @PrimaryKey
    val productUrl: String,
    val position: Int,
    val store: String,
    val title: String,
    val sku: String?,
    val regularPrice: Double,
    val finalPrice: Double,
    val percentOff: Double,
    val currency: String,
    val imageUrl: String?
)

fun StoreDiscount.toEntity(position: Int): PromotionEntity = PromotionEntity(
    productUrl = productUrl,
    position = position,
    store = store,
    title = title,
    sku = sku,
    regularPrice = regularPrice,
    finalPrice = finalPrice,
    percentOff = percentOff,
    currency = currency,
    imageUrl = imageUrl
)

fun PromotionEntity.toDomain(): StoreDiscount = StoreDiscount(
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
