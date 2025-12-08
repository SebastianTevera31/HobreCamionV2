package com.rfz.appflotal.data.model.app_utilities

import com.google.gson.annotations.SerializedName
import java.time.OffsetDateTime

data class UserOpinionDto(
    @SerializedName("fld_opinion") val id: String,
    @SerializedName("fld_registerDate") val registerDate: String,
)

data class UserOpinion(
    val opinion: String,
    val registerDate: OffsetDateTime,
)
