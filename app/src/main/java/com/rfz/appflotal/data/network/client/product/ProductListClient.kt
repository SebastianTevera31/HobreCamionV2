package com.rfz.appflotal.data.network.client.product

import com.rfz.appflotal.data.model.product.response.ProductResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ProductListClient {


    @GET("api/Catalog/Product")
    suspend fun doProductList(@Header("Authorization") token: String): Response<List<ProductResponse>>
}