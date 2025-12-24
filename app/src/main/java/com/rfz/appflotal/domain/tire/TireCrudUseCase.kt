package com.rfz.appflotal.domain.tire

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.tire.dto.TireCrudDto
import com.rfz.appflotal.data.repository.tire.TireCrudRepository
import javax.inject.Inject

class TireCrudUseCase @Inject constructor(
    private val tireCrudRepository: TireCrudRepository
) {
    suspend operator fun invoke(
        token: String,
        requestBody: TireCrudDto,
    ): Result<GeneralResponse> {
        return tireCrudRepository.doTireCrud(requestBody, token)
    }
}
