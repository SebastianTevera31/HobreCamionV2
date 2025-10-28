package com.rfz.appflotal.domain.utilization

import com.rfz.appflotal.data.model.utilization.response.UtilizationResponse
import com.rfz.appflotal.data.repository.utilization.UtilizationRepository
import javax.inject.Inject

data class Utilization(
    val id: Int,
    val description: String
)

class UtilizationUseCase @Inject constructor(
    private val utilizationRepository: UtilizationRepository
) {
    suspend operator fun invoke(): Result<List<UtilizationResponse>> {
        return utilizationRepository.doUtilization()
    }
}
