package com.rfz.appflotal.data.model.diagram.response

import com.google.gson.annotations.SerializedName

data class DiagramResponse(

    @SerializedName("id_diagram")
    val idDiagram: Int,

    @SerializedName("fld_description")
    val description: String,

    @SerializedName("fld_image")
    val imageBase64: String
)
