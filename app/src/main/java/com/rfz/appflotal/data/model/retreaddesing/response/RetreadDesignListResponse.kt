package com.rfz.appflotal.data.model.retreaddesing.response

import com.google.gson.annotations.SerializedName
import com.rfz.appflotal.domain.retreaddesign.RetreadDesign

data class RetreadDesignListResponse(
    @SerializedName("id_retreadDesign") val idRetreadDesign: Int,
    @SerializedName("fld_description") val description: String,
    @SerializedName("fld_retreadBrand") val retreadBrand: String,
    @SerializedName("id_retreadBrand") val idRetreadBrand: Int,
    @SerializedName("fld_utilization") val utilization: String,
    @SerializedName("id_utilization") val idUtilization: Int,
    @SerializedName("fld_treadDepth") val treadDepth: Int,
    @SerializedName("c_user_fk_1") val userId: Int,
) {
    fun toDomain(): RetreadDesign {
        return RetreadDesign(
            idDesign = idRetreadDesign,
            description = description,
            retreadBrand = retreadBrand,
            idRetreadBrand = idRetreadBrand,
            utilization = utilization,
            idUtilization = idUtilization,
            treadDepth = treadDepth,
            userId = userId
        )
    }
}