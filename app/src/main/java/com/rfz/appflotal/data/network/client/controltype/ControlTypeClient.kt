package com.rfz.appflotal.data.network.client.controltype

import com.rfz.appflotal.data.model.brand.response.BranListResponse
import com.rfz.appflotal.data.model.controltype.response.ControlTypeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ControlTypeClient {


    @GET("api/Catalog/ControlType")
    suspend fun doControlType(@Header("Authorization") token: String): Response<List<ControlTypeResponse>>
}