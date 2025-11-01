package com.rfz.appflotal.data.repository.product

import com.rfz.appflotal.data.model.product.response.ProductResponse
import com.rfz.appflotal.data.network.service.product.ProductListService
import javax.inject.Inject


class ProductListRepository @Inject constructor(private val productListService: ProductListService) {

    suspend fun doProductList( tok: String): Result<List<ProductResponse>> {
        return try {
            val response = productListService.doProductList(tok)
            if (response.isSuccessful) {

                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Throwable("Error: Cuerpo de la respuesta nulo"))
            } else {

                when (response.code()) {
                    401 -> Result.failure(Throwable("Error 401: No autorizado"))
                    else -> Result.failure(Throwable("Error en la respuesta del servidor: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}