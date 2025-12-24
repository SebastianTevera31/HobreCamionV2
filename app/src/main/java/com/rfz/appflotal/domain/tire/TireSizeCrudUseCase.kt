package com.rfz.appflotal.domain.tire

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.tire.dto.TireSizeDto
import com.rfz.appflotal.data.repository.tire.TireSizeCrudRepository
import javax.inject.Inject


class TireSizeCrudUseCase @Inject constructor(
    private val tireSizeCrudRepository: TireSizeCrudRepository
) {
    suspend operator fun invoke(requestBody: TireSizeDto, token: String): Result<List<GeneralResponse>> {
        return tireSizeCrudRepository.doCrudTireSize(requestBody, token)
    }
}