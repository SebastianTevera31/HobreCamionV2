package com.rfz.appflotal.data.network.client.product

import com.rfz.appflotal.data.model.product.response.ProductByIdResponse
import com.rfz.appflotal.data.model.product.response.ProductResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface ProductByIdClient {

    @GET("api/Catalog/ProductById")
    suspend fun doProductById(
        @Query("id_product") id_product: Int,
        @Header("Authorization") token: String
    ): Response<List<ProductByIdResponse>>
}
