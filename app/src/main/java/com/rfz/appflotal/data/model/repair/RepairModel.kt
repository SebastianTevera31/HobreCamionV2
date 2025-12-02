package com.rfz.appflotal.data.model.repair

import com.google.gson.annotations.SerializedName
import com.rfz.appflotal.data.model.CatalogItem

data class RepairCause(
    override val id: Int, override val description: String
): CatalogItem

data class RepairCauseDto(
    @SerializedName("id_repair") val id: Int,
    @SerializedName("fld_description") val description: String
)