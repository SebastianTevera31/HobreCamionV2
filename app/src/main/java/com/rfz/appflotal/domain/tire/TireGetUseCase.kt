package com.rfz.appflotal.domain.tire

import com.rfz.appflotal.data.model.tire.response.TireInspectionReportResponse
import com.rfz.appflotal.data.model.tire.response.TirexIdResponse
import com.rfz.appflotal.data.repository.tire.TireGetRepository
import javax.inject.Inject

class TireGetUseCase @Inject constructor(
    private val tireGetRepository: TireGetRepository
) {
    suspend operator fun invoke(idTire: Int, token: String): Result<List<TirexIdResponse>> {
        return tireGetRepository.doTireGet(idTire, token)
    }
}
