package com.rfz.appflotal.domain.product

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.product.dto.ProductCrudDto
import com.rfz.appflotal.data.model.product.response.ProductByIdResponse
import com.rfz.appflotal.data.repository.product.ProductByIdRepository
import com.rfz.appflotal.data.repository.product.ProductCrudRepository
import javax.inject.Inject


class ProductByIdUseCase @Inject constructor(
    private val productByIdRepository: ProductByIdRepository
) {
    suspend operator fun invoke(idproducto: Int, token: String): Result<List<ProductByIdResponse>> {
        return productByIdRepository.doCproductById(idproducto, token)
    }
}
