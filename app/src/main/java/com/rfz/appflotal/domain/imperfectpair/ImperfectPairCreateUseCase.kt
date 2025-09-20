package com.rfz.appflotal.domain.imperfectpair

import com.rfz.appflotal.data.model.imperfectpair.ApiResponse
import com.rfz.appflotal.data.model.imperfectpair.dto.ImperfectPairCreateRequest
import com.rfz.appflotal.data.repository.imperfectpair.ImperfectPairCreateRepository
import javax.inject.Inject

class ImperfectPairCreateUseCase @Inject constructor(
    private val repository: ImperfectPairCreateRepository
) {
    suspend operator fun invoke(
        token: String,
        request: ImperfectPairCreateRequest
    ): Result<ApiResponse> {
        return repository.createImperfectPair(token, request)
    }
}
