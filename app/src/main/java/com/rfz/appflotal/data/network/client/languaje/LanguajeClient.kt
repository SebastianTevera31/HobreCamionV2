package com.rfz.appflotal.data.network.client.languaje

import com.rfz.appflotal.data.model.defaultparameter.response.DefaultParameterResponse
import com.rfz.appflotal.data.model.languaje.LanguageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query


interface LanguajeClient {

    @POST("api/set_lang")
    suspend fun doLanguaje(@Header("Authorization") token: String, @Query("lang") lang: String): Response<LanguageResponse>

}