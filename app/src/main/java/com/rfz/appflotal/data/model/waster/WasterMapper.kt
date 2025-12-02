package com.rfz.appflotal.data.model.waster

import com.rfz.appflotal.data.model.waster.response.WasteReportListResponse
import java.time.OffsetDateTime

fun WasteReportListResponse.toDomain(): WasteReport {
    return WasteReport(
        id = idWasteReport,
        description = description,
        type = typeWasteReport
    )
}

fun ScrapTirePile.toDto(): ScrapTirePileDto {
    return ScrapTirePileDto(
        idScrapTirePile = id,
        pTireFk2 = tireId,
        fldDate = date.toString(),
        cScrapReportFk4 = scrapReportId,
        fldTreadDepth = treadDepth
    )
}