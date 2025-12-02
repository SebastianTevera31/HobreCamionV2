package com.rfz.appflotal.data.model.waster

import com.google.gson.annotations.SerializedName
import com.rfz.appflotal.data.model.CatalogItem
import java.time.OffsetDateTime

data class WasteReport(
    override val id: Int,
    override val description: String,
    val type: String
) : CatalogItem

data class ScrapTirePile(
    val id: Int,
    val tireId: Int,
    val date: OffsetDateTime,
    val scrapReportId: Int,
    val treadDepth: Int
)

data class ScrapTirePileDto(
    @SerializedName("id_scrapTirePile")
    val idScrapTirePile: Int,

    @SerializedName("p_tire_fk_2")
    val pTireFk2: Int,

    @SerializedName("fld_date")
    val fldDate: String,

    @SerializedName("c_scrapReport_fk_4")
    val cScrapReportFk4: Int,

    @SerializedName("fld_treadDepth")
    val fldTreadDepth: Int
)
