package com.rfz.appflotal.data.model.promotions

import com.google.gson.annotations.SerializedName

data class StoreDiscount(
    @SerializedName("store") val store: String,
    @SerializedName("title") val title: String,
    @SerializedName("sku") val sku: String?,
    @SerializedName("regularPrice") val regularPrice: Double,
    @SerializedName("finalPrice") val finalPrice: Double,
    @SerializedName("percentOff") val percentOff: Double,
    @SerializedName("currency") val currency: String,
    @SerializedName("productUrl") val productUrl: String,
    @SerializedName("imageUrl") val imageUrl: String?
)

data class DiscountsResponse(
    @SerializedName("result") val result: List<StoreDiscount>,
    @SerializedName("total") val total: Int,
    @SerializedName("stores") val stores: List<String>
)
