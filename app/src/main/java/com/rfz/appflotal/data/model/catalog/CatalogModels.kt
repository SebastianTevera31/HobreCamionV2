package com.rfz.appflotal.data.model.catalog

import com.google.gson.annotations.SerializedName
import com.rfz.appflotal.data.model.CatalogItem

data class GetCountriesResponse(
    @SerializedName("id_country") val idCountry: Int,
    @SerializedName("fld_name") val fldName: String? = null,
    @SerializedName("fld_name_ES") val fldNameEs: String? = null,
    @SerializedName("fld_name_EN") val fldNameEN: String? = null,
    @SerializedName("fld_name_FR") val fldNameFR: String? = null,
) {
    fun getName(languageSelected: String = "es"): String {
        return fldName
            ?: (if (languageSelected == "es") fldNameEs else fldNameEN)
            ?: fldNameEs
            ?: fldNameEN
            ?: fldNameFR
            ?: ""
    }
}

data class GetSectorsResponse(
    @SerializedName("id_sector") val idSector: Int,
    @SerializedName("fld_sector") val fldSector: String? = null,
)

data class StateDto(
    @SerializedName("id") val idState: Int,
    @SerializedName("fld_place") val stateName: String
)

data class GetTireInspectionReportResponse(
    @SerializedName("id_tireInspectionReport") val idTireInspectionReport: Int,
    @SerializedName("fld_description") val description: String,
) {
    fun toCatalog(): TireInspectionItem {
        return TireInspectionItem(
            id = idTireInspectionReport,
            description = description
        )
    }
}

data class TireInspectionItem(
    override val id: Int,
    override val description: String
) : CatalogItem


