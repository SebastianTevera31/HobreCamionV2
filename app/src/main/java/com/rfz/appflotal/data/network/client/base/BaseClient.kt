package com.rfz.appflotal.data.network.client.base

import com.rfz.appflotal.data.model.base.BaseResponse
import com.rfz.appflotal.data.model.controltype.response.ControlTypeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header


interface BaseClient {


    @GET("api/Catalog/Base")
    suspend fun doBase(@Header("Authorization") token: String): Response<List<BaseResponse>>
}