package com.rfz.appflotal.data.model.acquisitiontype.response

import com.google.gson.annotations.SerializedName

data class AcquisitionTypeResponse(

    @SerializedName("id_acquisitionType")
    val idAcquisitionType: Int,

    @SerializedName("fld_description")
    val description: String,

    @SerializedName("fld_descriptionEn")
    val enDescription: String
)
