package com.rfz.appflotal.data.network.client.product

import com.rfz.appflotal.data.model.brand.dto.BrandCrudDto
import com.rfz.appflotal.data.model.brand.response.BranListResponse
import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.product.dto.ProductCrudDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ProductCrudClient {


    @POST("api/Catalog/CrudProduct")
    suspend fun doCrudProduct(@Body requestBody: ProductCrudDto, @Header("Authorization") token: String): Response<List<MessageResponse>>
}