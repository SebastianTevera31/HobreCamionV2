package com.rfz.appflotal.domain.tire

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.tire.dto.InspectionTireDto
import com.rfz.appflotal.data.repository.tire.InspectionTireCrudRepository
import javax.inject.Inject

class InspectionTireCrudUseCase @Inject constructor(
    private val inspectionTireCrudRepository: InspectionTireCrudRepository
) {
    suspend operator fun invoke(requestBody: InspectionTireDto, token: String): Result<List<MessageResponse>> {
        return inspectionTireCrudRepository.doInspectionTire(requestBody, token)
    }
}
