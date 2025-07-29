package com.rfz.appflotal.data.network.client.destination

import com.rfz.appflotal.data.model.destination.response.DestinationResponse

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface DestinationClient {


    @GET("api/Catalog/Destination")
    suspend fun doDestination(@Header("Authorization") token: String): Response<List<DestinationResponse>>

}