package com.rfz.appflotal.data.network.client.defaultparameter


import com.rfz.appflotal.data.model.defaultparameter.response.DefaultParameterResponse
import com.rfz.appflotal.data.model.provider.response.ProviderListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface DefaultParameterClient {

    @GET("api/Catalog/DefaultParameter")
    suspend fun doDefaultParameter( @Header("Authorization") token: String,@Query("id_user") id_user: Int): Response<List<DefaultParameterResponse>>

}