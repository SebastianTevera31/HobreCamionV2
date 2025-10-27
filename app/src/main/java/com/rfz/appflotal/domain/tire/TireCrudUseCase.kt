package com.rfz.appflotal.domain.tire

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.tire.dto.TireCrudDto
import com.rfz.appflotal.data.repository.tire.TireCrudRepository
import javax.inject.Inject

class TireCrudUseCase @Inject constructor(
    private val tireCrudRepository: TireCrudRepository
) {
    suspend operator fun invoke(requestBody: TireCrudDto, token: String): Result<MessageResponse> {
        return tireCrudRepository.doTireCrud(requestBody, token)
    }
}
