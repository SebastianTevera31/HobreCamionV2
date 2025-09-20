package com.rfz.appflotal.data.model.delete


import com.google.gson.annotations.SerializedName

data class CatalogDeleteDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("fld_table")
    val table: String
)
