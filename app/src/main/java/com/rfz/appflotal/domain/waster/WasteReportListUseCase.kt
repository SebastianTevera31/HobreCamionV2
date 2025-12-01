package com.rfz.appflotal.domain.waster

import com.rfz.appflotal.data.model.waster.WasteReport
import com.rfz.appflotal.data.model.waster.toDomain
import com.rfz.appflotal.data.repository.waster.WasteRepository
import javax.inject.Inject

class WasteReportListUseCase @Inject constructor(
    private val wasteReportListRepository: WasteRepository
) {
    suspend operator fun invoke(): Result<List<WasteReport>> {
        return wasteReportListRepository.doWasteReportList().map { list ->
            list.map { it.toDomain() }
        }
    }
}
