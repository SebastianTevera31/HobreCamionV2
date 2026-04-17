package com.rfz.appflotal.data.model.tire

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rfz.appflotal.data.model.catalog.GetTireInspectionReportResponse
import com.rfz.appflotal.data.model.database.InspectionCatalogEntity


fun GetTireInspectionReportResponse.toEntity() = InspectionCatalogEntity(
    idTireInspectionReport = idTireInspectionReport,
    description = description
)

fun InspectionCatalogEntity.toResponse() = GetTireInspectionReportResponse(
    idTireInspectionReport = idTireInspectionReport,
    description = description
)