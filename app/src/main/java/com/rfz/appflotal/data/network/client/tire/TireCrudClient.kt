package com.rfz.appflotal.data.network.client.tire

import com.rfz.appflotal.data.model.message.response.GeneralResponse

import com.rfz.appflotal.data.model.tire.dto.TireCrudDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface TireCrudClient {

    @POST("api/Tire/CrudTire")
    suspend fun doTireCrud(
        @Header("Authorization") token: String,
        @Body requestBody: TireCrudDto,
    ): Response<GeneralResponse>
}