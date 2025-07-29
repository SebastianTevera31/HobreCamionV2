package com.rfz.appflotal.data.network.client.tire

import com.rfz.appflotal.data.model.tire.response.LoadingCapacityResponse
import com.rfz.appflotal.data.model.tire.response.TireSizeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query



interface LoadingCapacityClient {

    @GET("api/Catalog/LoadCapacity")
    suspend fun getLoadCapacity(
        @Query("id_user") idUser: Int,
        @Header("Authorization") token: String
    ): Response<List<LoadingCapacityResponse>>
}