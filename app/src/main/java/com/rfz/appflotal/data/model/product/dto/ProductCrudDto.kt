package com.rfz.appflotal.data.model.product.dto

import com.google.gson.annotations.SerializedName

data class ProductCrudDto (
    @SerializedName("id_product")
    val idProduct: Int,

    @SerializedName("c_originalDesign_fk_1")
    val originalDesignId: Int,

    @SerializedName("c_tireSize_fk_2")
    val tireSizeId: Int,

    @SerializedName("c_loadCapacity_fk_3")
    val loadCapacityId: Int,

    @SerializedName("fld_treadDepth")
    val treadDepth: Int
)