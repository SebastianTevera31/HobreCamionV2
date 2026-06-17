package com.rfz.appflotal.domain.report

import com.rfz.appflotal.data.model.report.FuelConsumptionReportResponse
import com.rfz.appflotal.data.repository.report.ReportRepository
import javax.inject.Inject

class GetFuelConsumptionUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(): Result<List<FuelConsumptionReportResponse>> {
        return reportRepository.getFuelConsumptionReport()
    }
}
