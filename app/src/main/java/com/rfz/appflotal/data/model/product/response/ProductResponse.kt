package com.rfz.appflotal.data.model.product.response

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("id_product")
    val idProduct: Int,

    @SerializedName("fld_descriptionProduct")
    val descriptionProduct: String,

    @SerializedName("c_user_fk_6")
    val userId: Int,

    @SerializedName("fld_treadDepth")
    val treadDepth: Int
)
