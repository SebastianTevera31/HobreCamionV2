package com.rfz.appflotal.domain.retreaddesign

import com.rfz.appflotal.data.model.retreaddesing.response.RetreadDesignListResponse
import com.rfz.appflotal.data.repository.retreaddesign.RetreadDesignListRepository
import javax.inject.Inject

data class RetreadDesign(
    val idBrand: Int,
    val description: String,
    val retreadBrand: String,
    val idRetreadBrand: Int,
    val utilization: String,
    val idUtilization: Int,
    val treadDepth: Int,
    val userId: Int
)

class RetreadDesignListUseCase @Inject constructor(
    private val retreadDesignListRepository: RetreadDesignListRepository
) {
    suspend operator fun invoke(): Result<List<RetreadDesignListResponse>> {
        return retreadDesignListRepository.doBrandCrud()
    }
}
