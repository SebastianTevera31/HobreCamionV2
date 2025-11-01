package com.rfz.appflotal.domain.originaldesign

import com.rfz.appflotal.data.model.originaldesign.response.OriginalDesignResponse
import com.rfz.appflotal.data.repository.originaldesign.OriginalDesignRepository
import javax.inject.Inject

class OriginalDesignUseCase @Inject constructor(
    private val originalDesignRepository: OriginalDesignRepository
) {
    suspend operator fun invoke( token: String): Result<List<OriginalDesignResponse>> {
        return originalDesignRepository.doOriginalDesign( token)
    }
}
