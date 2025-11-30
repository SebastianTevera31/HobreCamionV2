package com.rfz.appflotal.data.model.waster

import com.rfz.appflotal.data.model.waster.response.WasteReportListResponse

fun WasteReportListResponse.toDomain(): WasteReport {
    return WasteReport(
        id = idWasteReport,
        description = description,
        type = typeWasteReport
    )
}