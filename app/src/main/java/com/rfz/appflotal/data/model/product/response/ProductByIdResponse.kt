package com.rfz.appflotal.data.model.product.response


data class ProductByIdResponse(
    val id_product: Int,
    val c_originalDesign_fk_1: Int,
    val c_tireSize_fk_2: Int,
    val c_loadCapacity_fk_3: Int,
    val fld_treadDepth: Int,
    val c_user_fk_6: Int,
    val fld_active: Boolean
)
