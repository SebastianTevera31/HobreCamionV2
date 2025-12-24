package com.rfz.appflotal.data.network.client.tire

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.tire.dto.TireSizeDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface TireSizeCrudClient {

    @POST("api/Catalog/CrudTireSize")
    suspend fun doCrudTireSize(@Body requestBody: TireSizeDto, @Header("Authorization") token: String): Response<List<GeneralResponse>>
}