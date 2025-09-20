package com.rfz.appflotal.domain.imperfectpair

import com.rfz.appflotal.data.model.imperfectpair.ApiResponse
import com.rfz.appflotal.data.model.imperfectpair.dto.ImperfectPairUpdateRequest
import com.rfz.appflotal.data.repository.imperfectpair.ImperfectPairUpdateRepository
import javax.inject.Inject

class ImperfectPairUpdateUseCase @Inject constructor(
    private val repository: ImperfectPairUpdateRepository
) {
    suspend operator fun invoke(
        token: String,
        request: ImperfectPairUpdateRequest
    ): Result<ApiResponse> {
        return repository.updateImperfectPair(token, request)
    }
}
