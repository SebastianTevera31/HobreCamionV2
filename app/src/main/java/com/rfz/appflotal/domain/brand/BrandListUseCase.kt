package com.rfz.appflotal.domain.brand

import com.rfz.appflotal.data.model.brand.response.BranListResponse
import com.rfz.appflotal.data.repository.brand.BrandListRepository
import javax.inject.Inject

class BrandListUseCase @Inject constructor(
    private val brandListRepository: BrandListRepository
) {
    suspend operator fun invoke(token: String, iduser:Int): Result<List<BranListResponse>> {
        return brandListRepository.doBrandList(token,iduser)
    }
}
