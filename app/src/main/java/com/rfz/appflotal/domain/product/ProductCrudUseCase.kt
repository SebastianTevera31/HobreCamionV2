package com.rfz.appflotal.domain.product

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.product.dto.ProductCrudDto
import com.rfz.appflotal.data.repository.product.ProductCrudRepository
import javax.inject.Inject

class ProductCrudUseCase @Inject constructor(
    private val productCrudRepository: ProductCrudRepository
) {
    suspend operator fun invoke(requestBody: ProductCrudDto, token: String): Result<List<MessageResponse>> {
        return productCrudRepository.doCrudProduct(requestBody, token)
    }
}
