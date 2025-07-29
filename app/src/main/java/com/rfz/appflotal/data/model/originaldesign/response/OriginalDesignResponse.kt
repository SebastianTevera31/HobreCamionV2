package com.rfz.appflotal.data.model.originaldesign.response

import com.google.gson.annotations.SerializedName

data class OriginalDesignResponse(

    @SerializedName("id_originalDesign")
    val idOriginalDesign: Int,

    @SerializedName("fld_model")
    val model: String,

    @SerializedName("fld_description")
    val description: String,

    @SerializedName("fld_brand")
    val brandId: String,

    @SerializedName("fld_utilization")
    val fld_utilization: String
)
