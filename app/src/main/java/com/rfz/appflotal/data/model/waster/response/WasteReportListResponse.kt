package com.rfz.appflotal.data.model.waster.response

import com.google.gson.annotations.SerializedName

data class WasteReportListResponse(
    @SerializedName("id_wasteReport")
    val idWasteReport: Int,

    @SerializedName("fld_description")
    val description: String,

    @SerializedName("fld_typeWasteReport")
    val typeWasteReport: String
)
