package com.rfz.appflotal.domain.product

import com.rfz.appflotal.data.model.product.response.ProductResponse
import com.rfz.appflotal.data.repository.product.ProductListRepository
import javax.inject.Inject

class ProductListUseCase @Inject constructor(
    private val productListRepository: ProductListRepository
) {
    suspend operator fun invoke(token: String): Result<List<ProductResponse>> {
        return productListRepository.doProductList(token)
    }
}
