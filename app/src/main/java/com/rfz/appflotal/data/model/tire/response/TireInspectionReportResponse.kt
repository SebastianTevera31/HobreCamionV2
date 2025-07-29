package com.rfz.appflotal.data.model.tire.response

import com.google.gson.annotations.SerializedName

data class TireInspectionReportResponse(
    @SerializedName("id_tireInspectionReport")
    val idTireInspectionReport: Int,

    @SerializedName("fld_description")
    val description: String,

    @SerializedName("c_user_fk_1")
    val userId: Int,

    @SerializedName("fld_active")
    val isActive: Boolean
)
