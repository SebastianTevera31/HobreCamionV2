package com.rfz.appflotal.domain.utilization

import com.rfz.appflotal.data.model.utilization.response.UtilizationResponse
import com.rfz.appflotal.data.repository.utilization.UtilizationRepository
import javax.inject.Inject

class UtilizationUseCase @Inject constructor(
    private val utilizationRepository: UtilizationRepository
) {
    suspend operator fun invoke(token: String): Result<List<UtilizationResponse>> {
        return utilizationRepository.doUtilization(token)
    }
}
