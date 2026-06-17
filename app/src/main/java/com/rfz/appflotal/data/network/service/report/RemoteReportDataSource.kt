package com.rfz.appflotal.data.network.service.report

import com.rfz.appflotal.data.network.client.report.ReportService
import com.rfz.appflotal.data.network.networkRequestHelper
import javax.inject.Inject

class RemoteReportDataSource @Inject constructor(private val reportService: ReportService) {

    suspend fun getCpkReport(token: String, idUser: Int) = networkRequestHelper {
        reportService.getCpkReportByIDUser(token = "bearer $token", idUser = idUser)
    }

    suspend fun getCO2EmissionsReport(token: String) = networkRequestHelper {
        reportService.getCO2EmissionsReport("bearer $token")
    }

    suspend fun getFuelConsumptionReport(token: String) =
        networkRequestHelper {
            reportService.getFuelConsumptionReport("bearer $token")
        }
}