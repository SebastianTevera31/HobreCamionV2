package com.rfz.appflotal.domain.retreadbrand

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.retreadbrand.dto.RetreadBrandDto
import com.rfz.appflotal.data.repository.retreadbrand.RetreadBrandCrudRepository
import javax.inject.Inject

class RetreadBrandCrudUseCase @Inject constructor(
    private val retreadBrandCrudRepository: RetreadBrandCrudRepository
) {
    suspend operator fun invoke(requestBody: RetreadBrandDto): Result<List<GeneralResponse>> {
        return retreadBrandCrudRepository.doRetreadBrand(requestBody)
    }
}
