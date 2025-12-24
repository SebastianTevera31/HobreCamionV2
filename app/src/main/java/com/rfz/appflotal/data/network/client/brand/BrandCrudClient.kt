package com.rfz.appflotal.data.network.client.brand

import com.rfz.appflotal.data.model.brand.dto.BrandCrudDto
import com.rfz.appflotal.data.model.message.response.GeneralResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface BrandCrudClient {

    @POST("api/Catalog/CrudBrand")
    suspend fun doBrandCrud(@Body requestBody: BrandCrudDto,@Header("Authorization") token: String): Response<List<GeneralResponse >>
}