package com.rfz.appflotal.data.model.scrap.response

import com.google.gson.annotations.SerializedName

data class ScrapReportResponse(
    @SerializedName("id_wasteReport")
    val idWasteReport: Int,

    @SerializedName("fld_description")
    val description: String,

    @SerializedName("fld_typeWasteReport")
    val typeWasteReport: String
)
