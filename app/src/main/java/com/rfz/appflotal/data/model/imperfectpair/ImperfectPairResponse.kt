package com.rfz.appflotal.data.model.imperfectpair

import com.google.gson.annotations.SerializedName

data class ImperfectPairResponse(
    @SerializedName("id_classificationOfImperfectPair")
    val idClassification: Int,

    @SerializedName("fld_score")
    val score: String,

    @SerializedName("fld_new")
    val newValue: Int,

    @SerializedName("fld_renovated")
    val renovated: Int,

    @SerializedName("c_user_fk_1")
    val userId: Int
)
