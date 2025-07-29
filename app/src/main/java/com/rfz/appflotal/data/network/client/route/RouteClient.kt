package com.rfz.appflotal.data.network.client.route

import com.rfz.appflotal.data.model.route.response.RouteResponse

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface RouteClient {


    @GET("api/Catalog/Route")
    suspend fun doRoute(@Header("Authorization") token: String): Response<List<RouteResponse>>
}