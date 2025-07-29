package com.rfz.appflotal.data.network.service.product

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.product.dto.ProductCrudDto
import com.rfz.appflotal.data.model.product.response.ProductResponse
import com.rfz.appflotal.data.network.client.product.ProductListClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject




class ProductListService @Inject constructor(private val productList: ProductListClient) {
    suspend fun doProductList(tok:String): Response<List<ProductResponse>> {
        return withContext(Dispatchers.IO) {
            productList.doProductList(tok)
        }
    }
}