package com.rfz.appflotal.domain.report

import com.rfz.appflotal.data.model.report.CO2EmissionsReportResponse
import com.rfz.appflotal.data.repository.report.ReportRepository
import javax.inject.Inject

class GetCO2EmissionsUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(): Result<List<CO2EmissionsReportResponse>> {
        return reportRepository.getCO2EmissionsReport()
    }
}
