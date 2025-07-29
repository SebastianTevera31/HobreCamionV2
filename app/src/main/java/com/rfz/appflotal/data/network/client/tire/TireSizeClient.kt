package com.rfz.appflotal.data.network.client.tire

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.tire.dto.InspectionTireDto
import com.rfz.appflotal.data.model.tire.response.TireSizeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query


interface TireSizeClient {

    @GET("api/Catalog/TireSize")
    suspend fun getTireSizes(
        @Query("id_user") idUser: Int,
        @Header("Authorization") token: String
    ): Response<List<TireSizeResponse>>
}