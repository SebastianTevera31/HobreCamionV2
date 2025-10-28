package com.rfz.appflotal.domain.retreaddesign

import com.rfz.appflotal.data.model.retreaddesing.response.RetreadDesignByIdResponse
import com.rfz.appflotal.data.repository.retreaddesign.RetreadDesignByIdRepository
import javax.inject.Inject

class RetreadDesignByIdUseCase @Inject constructor(
    private val retreadDesignByIdRepository: RetreadDesignByIdRepository
) {
    suspend fun invoke(retreadDesignId: Int): Result<RetreadDesignByIdResponse> {
        return retreadDesignByIdRepository.onRetreatedDesignById(retreadDesignId)
    }
}