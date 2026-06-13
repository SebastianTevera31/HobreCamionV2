package com.rfz.appflotal.data.network.service.report

import com.rfz.appflotal.data.model.report.CpkReportRequest
import com.rfz.appflotal.data.network.client.report.ReportService
import com.rfz.appflotal.data.network.networkRequestHelper
import javax.inject.Inject


class RemoteReportDataSource @Inject constructor(private val reportService: ReportService) {

    suspend fun getCpkReport(token: String, body: CpkReportRequest) = networkRequestHelper {
        reportService.getCpkReport("bearer $token", body)
    }

    suspend fun getCO2EmissionsReport(token: String) = networkRequestHelper {
        reportService.getCO2EmissionsReport("bearer $token")
    }

    suspend fun getFuelConsumptionReport(token: String) = networkRequestHelper {
        reportService.getFuelConsumptionReport("bearer $token")
    }
}