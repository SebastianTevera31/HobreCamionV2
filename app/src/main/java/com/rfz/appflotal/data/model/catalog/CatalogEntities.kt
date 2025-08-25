package com.rfz.appflotal.data.model.catalog

import com.google.gson.annotations.SerializedName

data class GetCountriesResponse(
    @SerializedName("id_country") val idCountry: Int,
    @SerializedName("fld_name_ES") val fldNameEs: String,
    @SerializedName("fld_name_EN") val fldNameEN: String,
    @SerializedName("fld_name_FR") val fldNameFR: String,
)

data class GetSectorsResponse(
    @SerializedName("id_sector") val idCountry: Int,
    @SerializedName("fld_sector") val fldSector: String,
)