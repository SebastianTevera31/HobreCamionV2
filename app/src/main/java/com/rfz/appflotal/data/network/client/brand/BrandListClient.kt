package com.rfz.appflotal.data.network.client.brand

import com.rfz.appflotal.data.model.brand.dto.BrandCrudDto
import com.rfz.appflotal.data.model.brand.response.BranListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface BrandListClient {

    @GET("api/Catalog/BrandList")
    suspend fun doBrandList(@Header("Authorization") token: String,@Query("id_user") id_user: Int): Response<List<BranListResponse>>
}