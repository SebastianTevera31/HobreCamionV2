package com.rfz.appflotal.data.model.promotions

import com.google.gson.annotations.SerializedName
import java.text.Normalizer
import java.util.Locale

data class StoreDiscount(
    @SerializedName("store") val store: String,
    @SerializedName("title") val title: String,
    @SerializedName("sku") val sku: String?,
    @SerializedName("regularPrice") val regularPrice: Double,
    @SerializedName("finalPrice") val finalPrice: Double,
    @SerializedName("percentOff") val percentOff: Double,
    @SerializedName("currency") val currency: String,
    @SerializedName("productUrl") val productUrl: String,
    @SerializedName("imageUrl") val imageUrl: String?,
    // "Llantas" | "Accesorios" | "Amarre de carga". Nullable: Gson deja null si no viene.
    @SerializedName("category") val category: String? = null
)

data class DiscountsResponse(
    @SerializedName("result") val result: List<StoreDiscount>,
    @SerializedName("total") val total: Int,
    @SerializedName("stores") val stores: List<String>
)

/** Categorías de GetDiscounts, en el orden de las pestañas. [apiValue] viaja en `category`. */
enum class PromotionCategory(val apiValue: String) {
    ACCESSORIES("Accesorios"),
    TIRES("Llantas"),
    CARGO_SECUREMENT("Amarre de carga");

    companion object {
        /** Sin importar mayúsculas, acentos ni espacios sobrantes; null si no se reconoce. */
        fun fromApi(value: String?): PromotionCategory? {
            val key = value?.let(::normalizeCategory) ?: return null
            return entries.firstOrNull { normalizeCategory(it.apiValue) == key }
        }

        private fun normalizeCategory(value: String): String =
            Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
                .replace(Regex("\\p{M}+"), "")
                .replace(Regex("\\s+"), " ")
                .lowercase(Locale.ROOT)
    }
}
