package com.rfz.appflotal.data.network.service.product

import com.rfz.appflotal.data.model.product.response.ProductByIdResponse
import com.rfz.appflotal.data.network.client.product.ProductByIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject



class ProductByIdService @Inject constructor(private val productByIdClient: ProductByIdClient) {
    suspend fun doCproductById(
        idproducto: Int,
        tok: String
    ): Response<List<ProductByIdResponse>> {
        return withContext(Dispatchers.IO) {
            productByIdClient.doProductById(idproducto, tok)
        }
    }
}