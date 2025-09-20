package com.rfz.appflotal.data.model.imperfectpair.dto

import com.google.gson.annotations.SerializedName

data class ImperfectPairCreateRequest(
    @SerializedName("fld_score")
    val score: String,

    @SerializedName("fld_new")
    val newValue: Int,

    @SerializedName("fld_renovated")
    val renovated: Int
)