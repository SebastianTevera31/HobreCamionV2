package com.rfz.appflotal.domain.originaldesign

import com.rfz.appflotal.data.model.originaldesign.response.OriginalDesignByIdResponse
import com.rfz.appflotal.data.model.product.response.ProductByIdResponse
import com.rfz.appflotal.data.repository.originaldesign.OriginalDesignByIdRepository
import com.rfz.appflotal.data.repository.product.ProductByIdRepository
import javax.inject.Inject



class OriginalDesignByIdUseCase @Inject constructor(
    private val originalDesignByIdRepository: OriginalDesignByIdRepository
) {
    suspend operator fun invoke(id: Int, token: String): Result<List<OriginalDesignByIdResponse>> {
        return originalDesignByIdRepository.doOriginalDesignById(id, token)
    }
}
