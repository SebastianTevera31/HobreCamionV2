package com.rfz.appflotal.data.repository.report

import com.rfz.appflotal.data.model.report.CO2EmissionsReportResponse
import com.rfz.appflotal.data.model.report.CpkReportRequest
import com.rfz.appflotal.data.model.report.CpkReportResponse
import com.rfz.appflotal.data.model.report.FuelConsumptionReportResponse
import com.rfz.appflotal.data.network.service.report.RemoteReportDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ReportRepository @Inject constructor(
    private val remoteReportDataSource: RemoteReportDataSource,
    private val getTasksUseCase: GetTasksUseCase
) {
    suspend fun getCpkReport(idTire: Int): Result<List<CpkReportResponse>> {
        val user = getTasksUseCase().first().first()
        return remoteReportDataSource.getCpkReport(
            token = user.fld_token,
            body = CpkReportRequest(
                idUser = user.idUser,
                idTire = idTire
            )
        )
    }

    suspend fun getCO2EmissionsReport(): Result<List<CO2EmissionsReportResponse>> {
        val user = getTasksUseCase().first().first()
        return remoteReportDataSource.getCO2EmissionsReport(token = user.fld_token)
    }

    suspend fun getFuelConsumptionReport(): Result<List<FuelConsumptionReportResponse>> {
        val user = getTasksUseCase().first().first()
        return remoteReportDataSource.getFuelConsumptionReport(token = user.fld_token)
    }
}