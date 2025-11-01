package com.rfz.appflotal.domain.brand

import com.rfz.appflotal.data.model.brand.dto.BrandCrudDto
import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.repository.brand.BrandCrudRepository
import javax.inject.Inject

class BrandCrudUseCase @Inject constructor(
    private val brandCrudRepository: BrandCrudRepository
) {
    suspend operator fun invoke(requestBody: BrandCrudDto, token: String): Result<List<MessageResponse>> {
        return brandCrudRepository.doBrandCrud(requestBody, token)
    }
}
