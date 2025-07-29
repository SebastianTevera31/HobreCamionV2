package com.rfz.appflotal.domain.scrap

import com.rfz.appflotal.data.model.scrap.response.ScrapReportResponse
import com.rfz.appflotal.data.repository.scrap.ScrapReportRepository
import javax.inject.Inject

class ScrapReportUseCase @Inject constructor(
    private val scrapReportRepository: ScrapReportRepository
) {
    suspend operator fun invoke(token: String): Result<List<ScrapReportResponse>> {
        return scrapReportRepository.doScrapReport(token)
    }
}
