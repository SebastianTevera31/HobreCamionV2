package com.rfz.appflotal.data.model.apputilities

import com.google.gson.annotations.SerializedName
import java.time.OffsetDateTime

data class UserOpinionDto(
    @SerializedName("fld_opinion") val id: String,
    @SerializedName("fld_registerDate") val registerDate: String,
)

data class TermsAndConditionsDto(
    @SerializedName("textEs") val termsEs: String,
    @SerializedName("textEn") val termsEn: String,
)

data class UserOpinion(
    val opinion: String,
    val registerDate: OffsetDateTime,
)

data class TermsAndConditions(
    val termsEs: String,
    val termsEn: String,
)
