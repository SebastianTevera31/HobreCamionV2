package com.rfz.appflotal.data.repository.product

import com.rfz.appflotal.data.model.product.response.ProductByIdResponse
import com.rfz.appflotal.data.network.service.product.ProductByIdService
import javax.inject.Inject



class ProductByIdRepository @Inject constructor(private val productByIdService: ProductByIdService) {

    suspend fun doCproductById(idproducto: Int, tok: String): Result<List<ProductByIdResponse>> {
        return try {
            val response = productByIdService.doCproductById(idproducto,tok)
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