package com.rfz.appflotal.domain.imperfectpair

import com.rfz.appflotal.data.model.imperfectpair.ImperfectPairResponse
import com.rfz.appflotal.data.repository.imperfectpair.ImperfectPairRepository
import javax.inject.Inject

class ImperfectPairUseCase @Inject constructor(
    private val repository: ImperfectPairRepository
) {
    suspend operator fun invoke(token: String): Result<List<ImperfectPairResponse>> {
        return repository.getImperfectPairs(token)
    }
}
