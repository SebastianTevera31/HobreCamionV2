package com.rfz.appflotal.domain.retreadbrand

import com.rfz.appflotal.data.model.retreadbrand.response.RetreadBrandListResponse
import com.rfz.appflotal.data.repository.retreadbrand.RetreadBrandListRepository
import javax.inject.Inject

class RetreadBrandListUseCase @Inject constructor(
    private val retreadBrandListRepository: RetreadBrandListRepository
) {
    suspend operator fun invoke(): Result<List<RetreadBrandListResponse>> {
        return retreadBrandListRepository.doBrandCrud()
    }
}
