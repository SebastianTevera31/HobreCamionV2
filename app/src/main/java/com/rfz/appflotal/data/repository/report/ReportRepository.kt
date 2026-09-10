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
    private val getTasksUseCase: GetTasksUseCase,
    private val reportLocalCache: ReportLocalCache
) {
    suspend fun getCpkReport(): Result<List<CpkReportResponse>> {
        val result = runCatching {
            val user = getTasksUseCase().first().first()
            remoteReportDataSource.getCpkReport(
                token = user.fld_token,
                idUser = user.idUser
            ).getOrThrow()
        }
        result.onSuccess { reportLocalCache.saveCpkReport(it) }
        return result.recoverCatching {
            reportLocalCache.getCpkReport().ifEmpty { throw it }
        }
    }

    suspend fun getCO2EmissionsReport(): Result<List<CO2EmissionsReportResponse>> {
        val result = runCatching {
            val user = getTasksUseCase().first().first()
            remoteReportDataSource.getCO2EmissionsReport(token = user.fld_token).getOrThrow()
        }
        result.onSuccess { reportLocalCache.saveCO2Report(it) }
        return result.recoverCatching {
            reportLocalCache.getCO2Report().ifEmpty { throw it }
        }
    }

    suspend fun getFuelConsumptionReport(): Result<List<FuelConsumptionReportResponse>> {
        val result = runCatching {
            val user = getTasksUseCase().first().first()
            remoteReportDataSource.getFuelConsumptionReport(token = user.fld_token).getOrThrow()
        }
        result.onSuccess { reportLocalCache.saveFuelReport(it) }
        return result.recoverCatching {
            reportLocalCache.getFuelReport().ifEmpty { throw it }
        }
    }
}